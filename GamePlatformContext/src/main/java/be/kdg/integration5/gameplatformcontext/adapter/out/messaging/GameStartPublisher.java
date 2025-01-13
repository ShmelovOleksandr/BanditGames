package be.kdg.integration5.gameplatformcontext.adapter.out.messaging;

import be.kdg.integration5.common.events.LobbyCreatedEvent;
import be.kdg.integration5.gameplatformcontext.domain.Lobby;
import be.kdg.integration5.gameplatformcontext.port.out.NotifyGameStartPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameStartPublisher implements NotifyGameStartPort {

    private static final String LOBBY_EVENTS_EXCHANGE = "lobby_events";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RabbitTemplate rabbitTemplate;

    public GameStartPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notifyGameStart(Lobby lobby) {
        UUID lobbyId = lobby.getLobbyId().uuid();
        final String  routingKey = String.format("lobby.%s.created", lobbyId);
        logger.info("Notifying RabbitMQ: {}", routingKey);
        rabbitTemplate.convertAndSend(
                LOBBY_EVENTS_EXCHANGE,
                routingKey,
                new LobbyCreatedEvent(
                        lobby.getPlayingGame().getTitle(),
                        lobbyId,
                        lobby.getLobbyOwner().getPlayerId().uuid(),
                        lobby.getPlayers()
                                .stream()
                                .map(player -> new LobbyCreatedEvent.PlayerEvent(
                                        player.getPlayerId().uuid(),
                                        player.getUsername()))
                                .toList()

                )
        );
    }
}
