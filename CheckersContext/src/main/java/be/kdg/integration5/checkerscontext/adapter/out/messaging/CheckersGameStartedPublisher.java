package be.kdg.integration5.checkerscontext.adapter.out.messaging;

import be.kdg.integration5.checkerscontext.adapter.out.messaging.event.CheckersGameStartedEvent;
import be.kdg.integration5.checkerscontext.adapter.out.messaging.event.CheckersMoveMadeEvent;
import be.kdg.integration5.checkerscontext.domain.PiecePosition;
import be.kdg.integration5.checkerscontext.domain.PlayerId;
import be.kdg.integration5.checkerscontext.port.out.CheckersGameStartedCommand;
import be.kdg.integration5.checkerscontext.port.out.CheckersMoveMadeCommand;
import be.kdg.integration5.checkerscontext.port.out.NotifyCheckersGameStartedPort;
import be.kdg.integration5.checkerscontext.port.out.NotifyCheckersMoveMadePort;
import be.kdg.integration5.common.events.StartGameSessionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CheckersGameStartedPublisher implements NotifyCheckersGameStartedPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckersGameStartedPublisher.class);
    private static final String EXCHANGE_NAME = "game_events";

    @Value("${game.name}")
    private String gameName;

    private final RabbitTemplate rabbitTemplate;

    public CheckersGameStartedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notifyCheckersGameStarted(CheckersGameStartedCommand checkersGameStartedCommand) {
        UUID gameUUID = checkersGameStartedCommand.gameId().uuid();
        final String ROUTING_KEY = String.format("game.%s.started", gameUUID);
        LOGGER.info("Notifying RabbitMQ: {}", ROUTING_KEY);
        List<UUID> playerUUIDs = checkersGameStartedCommand.players().stream().map(player -> player.getPlayerId().uuid()).toList();
        UUID firstPlayerId = checkersGameStartedCommand.firstPlayer().getPlayerId().uuid();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, new StartGameSessionEvent(
                gameName,
                gameUUID,
                firstPlayerId,
                playerUUIDs,
                LocalDateTime.now()
        ));
    }
}
