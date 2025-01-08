package be.kdg.integration5.guessitcontext.controller.messaging;

import be.kdg.integration5.common.events.LobbyCreatedEvent;
import be.kdg.integration5.guessitcontext.domain.Session;
import be.kdg.integration5.guessitcontext.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GamePlatformListener {
    private static final String LOBBY_QUEUE = "lobby_queue";
    private final SessionService sessionService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${game.name}")
    private  String gameName;

    public GamePlatformListener(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RabbitListener(queues = LOBBY_QUEUE, messageConverter = "#{jackson2JsonMessageConverter}")
    public void createGameFromLobby(LobbyCreatedEvent event) {
        if (event.gameName().equals(this.gameName)) {
            logger.info("Receive lobby created event: {}", event.lobbyId());
            Session session = sessionService.createSession(event);
            sessionService.sendGameSessionStartEvent(session);
        }
    }
}
