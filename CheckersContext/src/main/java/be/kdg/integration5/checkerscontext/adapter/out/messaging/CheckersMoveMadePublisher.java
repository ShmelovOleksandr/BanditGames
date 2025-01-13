package be.kdg.integration5.checkerscontext.adapter.out.messaging;

import be.kdg.integration5.checkerscontext.adapter.out.messaging.event.CheckersMoveMadeEvent;
import be.kdg.integration5.checkerscontext.domain.PiecePosition;
import be.kdg.integration5.checkerscontext.port.out.CheckersMoveMadeCommand;
import be.kdg.integration5.checkerscontext.port.out.NotifyCheckersMoveMadePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CheckersMoveMadePublisher implements NotifyCheckersMoveMadePort {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckersMoveMadePublisher.class);
    private static final String EXCHANGE_NAME = "checkers_events";

    private final RabbitTemplate rabbitTemplate;

    public CheckersMoveMadePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notifyCheckersMoveMade(CheckersMoveMadeCommand checkersMoveMadeCommand) {
        UUID gameUUID = checkersMoveMadeCommand.gameId().uuid();
        final String ROUTING_KEY = String.format("checkers.%s.move.made", gameUUID);
        LOGGER.info("Notifying RabbitMQ: {}", ROUTING_KEY);

        UUID moverId = checkersMoveMadeCommand.moverId().uuid();
        PiecePosition initialPosition = checkersMoveMadeCommand.move().getInitialPosition();
        PiecePosition futurePosition = checkersMoveMadeCommand.move().getFuturePosition();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, new CheckersMoveMadeEvent(
                gameUUID,
                moverId,
                new CheckersMoveMadeEvent.CheckersMovePosition(initialPosition.x(), initialPosition.y()),
                new CheckersMoveMadeEvent.CheckersMovePosition(futurePosition.x(), futurePosition.y()),
                LocalDateTime.now()
        ));
    }
}
