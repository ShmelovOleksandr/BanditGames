package be.kdg.integration5.checkersachievementcontext.adapter.in.messaging;

import be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event.CheckersMoveMadeEvent;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PiecePosition;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CheckersMoveMadeListener {
    private static final String CHECKERS_MOVE_MADE = "checkers_move_made_queue";
    private final Logger logger = LoggerFactory.getLogger(CheckersMoveMadeListener.class);

    private final HandleCheckersMoveMadeUseCase handleCheckersMoveMadeUseCase;

    public CheckersMoveMadeListener(HandleCheckersMoveMadeUseCase handleCheckersMoveMadeUseCase) {
        this.handleCheckersMoveMadeUseCase = handleCheckersMoveMadeUseCase;
    }

    @RabbitListener(queues = CHECKERS_MOVE_MADE, messageConverter = "#{jackson2JsonMessageConverter}")
    public void createGameFromLobby(CheckersMoveMadeEvent checkersMoveMadeEvent) {
        logger.info("Receive checkers move made event: {}", checkersMoveMadeEvent.gameId());
        HandleCheckersMoveMadeCommand handleCheckersMoveMadeCommand = new HandleCheckersMoveMadeCommand(
                new GameId(checkersMoveMadeEvent.gameId()),
                new PlayerId(checkersMoveMadeEvent.moverId()),
                new PiecePosition(checkersMoveMadeEvent.oldPosition().x(), checkersMoveMadeEvent.oldPosition().y()),
                new PiecePosition(checkersMoveMadeEvent.newPosition().x(), checkersMoveMadeEvent.newPosition().y()),
                checkersMoveMadeEvent.madeAt()
        );
        handleCheckersMoveMadeUseCase.handleCheckersMoveMade(handleCheckersMoveMadeCommand);
    }
}
