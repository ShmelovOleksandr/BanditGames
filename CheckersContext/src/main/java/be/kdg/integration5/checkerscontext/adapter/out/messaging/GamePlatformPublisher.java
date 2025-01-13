package be.kdg.integration5.checkerscontext.adapter.out.messaging;

import be.kdg.integration5.checkerscontext.domain.Player;
import be.kdg.integration5.checkerscontext.port.out.NotifyGamePlatformPort;
import be.kdg.integration5.common.events.FinishGameSessionEvent;
import be.kdg.integration5.common.events.GameAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class GamePlatformPublisher implements NotifyGamePlatformPort {

    private static final String PLATFORM_GAME_EXCHANGE = "platform_game_exchange";
    private static final Logger LOGGER = LoggerFactory.getLogger(GamePlatformPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final RulesLoader rulesLoader;

    @Value("${game.name}")
    private String gameName;

    @Value("${game.description}")
    private String description;

    @Value("${game.price}")
    private Double price;

    @Value("${game.currency}")
    private String currency;

    @Value("${game.maxLobbyPlayersAmount}")
    private int maxLobbyPlayersAmount;

    @Value("${game.frontendUrl}")
    private String frontendUrl;

    @Value("${game.backendApiUrl}")
    private String backendApiUrl;

    @Value("${game.gameImageUrl}")
    private String gameImageUrl;

    public GamePlatformPublisher(RabbitTemplate rabbitTemplate, RulesLoader rulesLoader) {
        this.rabbitTemplate = rabbitTemplate;
        this.rulesLoader = rulesLoader;
    }


    @Override
    public void notifyGamePlatform() {
        final String ROUTING_KEY = String.format("newgame.%s.registered", UUID.randomUUID());
        LOGGER.info("Notifying game platform that game {} is running: {}", gameName, ROUTING_KEY);
        List<GameAddedEvent.GameRule> loadedRules = rulesLoader.loadRules();
        GameAddedEvent event = new GameAddedEvent(
                gameName,
                description,
                price,
                currency,
                maxLobbyPlayersAmount,
                frontendUrl,
                backendApiUrl,
                gameImageUrl,
                loadedRules
        );
        rabbitTemplate.convertAndSend(PLATFORM_GAME_EXCHANGE, ROUTING_KEY, event);
    }
}
