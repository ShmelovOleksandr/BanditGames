package be.kdg.integration5.gameplatformcontext.adapter.out.messaging;

import be.kdg.integration5.common.events.GameAddedEvent;
import be.kdg.integration5.common.events.LobbyCreatedEvent;
import be.kdg.integration5.gameplatformcontext.port.out.NotifyChatBotPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatBotPublisher implements NotifyChatBotPort {
    private static final String PLATFORM_GAME_EXCHANGE = "platform_game_exchange";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RabbitTemplate rabbitTemplate;

    public ChatBotPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notify(GameAddedEvent event) {
        final String  routingKey = String.format("rules.%s.registered", UUID.randomUUID());
        logger.info("Notifying ChatBot with new rules: {}", routingKey);
        rabbitTemplate.convertAndSend(
                PLATFORM_GAME_EXCHANGE,
                routingKey,
                event
        );
    }
}
