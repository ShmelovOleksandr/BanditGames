package be.kdg.integration5.checkersachievementcontext.adapter.in.messaging;

import be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event.CheckersGameStartedEvent;
import be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event.CheckersMoveMadeEvent;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PiecePosition;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedUseCase;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CheckersGameStartedListener {
    private static final String CHECKERS_GAME_STARTED = "checkers_game_started_queue";
    private final Logger logger = LoggerFactory.getLogger(CheckersGameStartedListener.class);

    private final HandleCheckersGameStartedUseCase handleCheckersGameStartedUseCase;

    public CheckersGameStartedListener(HandleCheckersGameStartedUseCase handleCheckersGameStartedUseCase) {
        this.handleCheckersGameStartedUseCase = handleCheckersGameStartedUseCase;
    }


    @RabbitListener(queues = CHECKERS_GAME_STARTED, messageConverter = "#{jackson2JsonMessageConverter}")
    public void createGameFromLobby(CheckersGameStartedEvent checkersGameStartedEvent) {
        logger.info("Receive checkers move made event: {}", checkersGameStartedEvent.gameId());
        HandleCheckersGameStartedCommand handleCheckersGameStartedCommand = new HandleCheckersGameStartedCommand(
                new GameId(checkersGameStartedEvent.gameId()),
                checkersGameStartedEvent.playerIds().stream().map(PlayerId::new).toList()
        );
        handleCheckersGameStartedUseCase.handleCheckersGameStarted(handleCheckersGameStartedCommand);
    }
}
