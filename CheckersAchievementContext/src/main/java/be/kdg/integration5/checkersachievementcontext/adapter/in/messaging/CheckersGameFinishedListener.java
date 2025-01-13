package be.kdg.integration5.checkersachievementcontext.adapter.in.messaging;

import be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event.CheckersGameFinishedEvent;
import be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event.CheckersGameStartedEvent;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameFinishedCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameFinishedUseCase;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CheckersGameFinishedListener {
    private static final String CHECKERS_GAME_FINISHED = "checkers_game_finished_queue";
    private final Logger logger = LoggerFactory.getLogger(CheckersGameFinishedListener.class);

    private final HandleCheckersGameFinishedUseCase handleCheckersGameFinishedUseCase;

    public CheckersGameFinishedListener(HandleCheckersGameFinishedUseCase handleCheckersGameFinishedUseCase) {
        this.handleCheckersGameFinishedUseCase = handleCheckersGameFinishedUseCase;
    }


    @RabbitListener(queues = CHECKERS_GAME_FINISHED, messageConverter = "#{jackson2JsonMessageConverter}")
    public void createGameFromLobby(CheckersGameFinishedEvent checkersGameFinishedEvent) {
        logger.info("Receive checkers move made event: {}", checkersGameFinishedEvent.gameId());
        HandleCheckersGameFinishedCommand handleCheckersGameFinishedCommand = new HandleCheckersGameFinishedCommand(
                new GameId(checkersGameFinishedEvent.gameId()),
                checkersGameFinishedEvent.winnerId() != null ? new PlayerId(checkersGameFinishedEvent.winnerId()) : null,
                checkersGameFinishedEvent.isDraw()
        );
        handleCheckersGameFinishedUseCase.handleCheckersGameFinished(handleCheckersGameFinishedCommand);
    }
}
