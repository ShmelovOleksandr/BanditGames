package be.kdg.integration5.checkerscontext.adapter.out.messaging;

import be.kdg.integration5.checkerscontext.adapter.out.messaging.event.CheckersGameFinishedEvent;
import be.kdg.integration5.checkerscontext.adapter.out.messaging.event.CheckersGameStartedEvent;
import be.kdg.integration5.checkerscontext.domain.PlayerId;
import be.kdg.integration5.checkerscontext.port.out.CheckersGameFinishedCommand;
import be.kdg.integration5.checkerscontext.port.out.CheckersGameStartedCommand;
import be.kdg.integration5.checkerscontext.port.out.NotifyCheckersGameFinishedPort;
import be.kdg.integration5.checkerscontext.port.out.NotifyCheckersGameStartedPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CheckersGameFinishedPublisher implements NotifyCheckersGameFinishedPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckersGameFinishedPublisher.class);
    private static final String EXCHANGE_NAME = "checkers_events";

    private final RabbitTemplate rabbitTemplate;

    public CheckersGameFinishedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notifyCheckersGameFinished(CheckersGameFinishedCommand checkersGameFinishedCommand) {
        UUID gameUUID = checkersGameFinishedCommand.gameId().uuid();
        final String ROUTING_KEY = String.format("checkers.game.%s.finished", gameUUID);
        LOGGER.info("Notifying RabbitMQ: {}", ROUTING_KEY);
        UUID winnerUUID = checkersGameFinishedCommand.winnerId() != null ? checkersGameFinishedCommand.winnerId().uuid() : null;
        Boolean isDraw = checkersGameFinishedCommand.isDraw();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, new CheckersGameFinishedEvent(
                gameUUID,
                winnerUUID,
                isDraw,
                LocalDateTime.now()
        ));
    }
}
