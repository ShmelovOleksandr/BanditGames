package be.kdg.integration5.guessitcontext.service;

import be.kdg.integration5.guessitcontext.controller.ws.MoveResponseDto;
import be.kdg.integration5.guessitcontext.controller.ws.SessionResponseDto;
import be.kdg.integration5.guessitcontext.domain.*;
import be.kdg.integration5.guessitcontext.exception.IncorrectPlayerTurnException;

import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionService sessionService;

    public GameServiceImpl(SimpMessagingTemplate messagingTemplate, SessionService sessionService) {
        this.messagingTemplate = messagingTemplate;
        this.sessionService = sessionService;

    }

    @Override
    @Transactional
    public MoveResult makeMove(PlayerId playerId, Integer guess) {
        Session session = sessionService.findNotFinishedWithPlayer(false, playerId);

        if (!session.getCurrentPlayer().getPlayerId().equals(playerId.uuid()))
            throw new IncorrectPlayerTurnException("It is not %s player turn.".formatted(playerId.uuid()));

        MoveResult moveResult = checkGuess(guess, session.getTargetNumber());

        if (moveResult.isFinished()) {
            session.setFinished(true);
            session.setWinner(session.getCurrentPlayer());
            sessionService.update(session);
            notifyAllPlayersSession(session);
            sessionService.sendGameSessionEndEvent(session);
        } else {
            Session updatedSession = sessionService.setNextPlayer(session);
            sessionService.update(updatedSession);
            sessionService.sendPlayerMoveEvent(playerId, updatedSession);
            notifyPlayersAfterIncorrectMove(moveResult, updatedSession);

        }

        return moveResult;
    }

    @Override
    public void sendSessionInfo(SessionId sessionId) {
        Session session = sessionService.findById(sessionId);
        notifyAllPlayersSession(session);
    }

    @Override
    public void notifyAllPlayersSession(Session session) {
        UUID winnerId = session.getWinner() != null ? session.getWinner().getPlayerId() : null;
        SessionResponseDto sessionResponseDto = new SessionResponseDto(session.getSessionId(), session.getCurrentPlayer().getPlayerId(), session.isFinished(), winnerId);
        session.getPlayers().forEach(player ->
                messagingTemplate.convertAndSend(getUserQueue(new PlayerId(player.getPlayerId())), sessionResponseDto));
    }

    @Override
    public void notifyPlayersAfterIncorrectMove(MoveResult moveResult, Session session) {
        UUID currentPlayerId = session.getCurrentPlayer().getPlayerId();
        List<UUID> otherPlayers = sessionService.getPlayerUUIDExceptFor(new PlayerId(currentPlayerId), session);

        MoveResponseDto moveResponseDto = new MoveResponseDto(moveResult.resultType().getMessage());

        messagingTemplate.convertAndSend(getUserQueue(new PlayerId(currentPlayerId)), moveResponseDto);

        SessionResponseDto sessionResponseDto = new SessionResponseDto(session.getSessionId(), currentPlayerId, session.isFinished(), null);
        otherPlayers.forEach(
                playerId -> messagingTemplate.convertAndSend(getUserQueue(new PlayerId(playerId)), sessionResponseDto));
    }


    private String getUserQueue(PlayerId playerId) {
        return "/queue/user/" + playerId.uuid().toString();
    }


    private MoveResult checkGuess(int guess, int targetNumber) {
        if (guess < 1 ) {
            return new MoveResult(ResultType.INCORRECT, false);
        } else if (guess < targetNumber) {
            return new MoveResult(ResultType.LESS, false);
        } else if (guess > targetNumber) {
            return new MoveResult(ResultType.MORE, false);
        } else {
            return new MoveResult(ResultType.SUCCESS, true);
        }
    }

}
