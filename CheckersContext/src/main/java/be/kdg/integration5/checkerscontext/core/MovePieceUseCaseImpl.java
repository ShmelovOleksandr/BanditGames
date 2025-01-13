package be.kdg.integration5.checkerscontext.core;

import be.kdg.integration5.checkerscontext.domain.*;
import be.kdg.integration5.checkerscontext.port.in.MovePieceCommand;
import be.kdg.integration5.checkerscontext.port.in.MovePieceUseCase;
import be.kdg.integration5.checkerscontext.port.out.*;
import be.kdg.integration5.common.events.PlayerMoveEvent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class MovePieceUseCaseImpl implements MovePieceUseCase {
    private final FindGamePort findGamePort;
    private final PersistGamePort persistGamePort;
    private final NotifyPlayerPort notifyPlayerPort;
    private final NotifyGameEndPort notifyGameEndPort;
    private final NotifyStatisticsPort notifyStatisticsPort;
    private final NotifyCheckersMoveMadePort notifyCheckersMoveMadePort;
    private final NotifyCheckersGameFinishedPort notifyCheckersGameFinishedPort;

    public MovePieceUseCaseImpl(FindGamePort findGamePort, PersistGamePort persistGamePort, NotifyPlayerPort notifyPlayerPort, NotifyGameEndPort notifyGameEndPort, NotifyStatisticsPort notifyStatisticsPort, NotifyCheckersMoveMadePort notifyCheckersMoveMadePort, NotifyCheckersGameFinishedPort notifyCheckersGameFinishedPort) {
        this.findGamePort = findGamePort;
        this.persistGamePort = persistGamePort;
        this.notifyPlayerPort = notifyPlayerPort;
        this.notifyGameEndPort = notifyGameEndPort;
        this.notifyStatisticsPort = notifyStatisticsPort;
        this.notifyCheckersMoveMadePort = notifyCheckersMoveMadePort;
        this.notifyCheckersGameFinishedPort = notifyCheckersGameFinishedPort;
    }

    @Override
    public void movePiece(MovePieceCommand movePieceCommand) {
        GameId gameId = movePieceCommand.gameId();
        Game game = findGamePort.findById(gameId);

        Game updatedGame = game;
        if (!game.isFinished()) {
            PlayerId moverId = movePieceCommand.playerId();
            Move move = movePieceCommand.move();

            game.getBoard().movePiece(moverId, move);
            boolean isGameOver = updatedGame.checkForGameOver();

            updatedGame = persistGamePort.update(game);
            PlayerId nextPlayerId = updatedGame.getBoard().getCurrentPlayer().getPlayerId();

            notifyCheckersMoveMadePort.notifyCheckersMoveMade(new CheckersMoveMadeCommand(gameId, moverId, move));
            notifyStatisticsPort.notifyPlayerMove(new PlayerMoveEvent(gameId.uuid(), moverId.uuid(), nextPlayerId.uuid(), LocalDateTime.now()));
            if (isGameOver) {
                notifyGameEndPort.notifyGameEnd(updatedGame);
                notifyCheckersGameFinishedPort.notifyCheckersGameFinished(new CheckersGameFinishedCommand(gameId, updatedGame.getWinner().getPlayerId(), updatedGame.isDraw()));
            }
        }
        notifyPlayerPort.notifyAllPlayersWithGameState(updatedGame);
    }
}
