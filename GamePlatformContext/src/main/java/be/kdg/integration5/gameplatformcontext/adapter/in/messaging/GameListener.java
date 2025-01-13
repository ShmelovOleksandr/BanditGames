package be.kdg.integration5.gameplatformcontext.adapter.in.messaging;

import be.kdg.integration5.common.events.GameAddedEvent;
import be.kdg.integration5.gameplatformcontext.port.in.RegisterNewGameUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameListener {

    private static final String NEW_REGISTERED_GAME_QUEUE = "new_registered_game_queue";

    private final RegisterNewGameUseCase registerNewGameUseCase;

    public GameListener(RegisterNewGameUseCase registerNewGameUseCase) {
        this.registerNewGameUseCase = registerNewGameUseCase;
    }

    @RabbitListener(queues = NEW_REGISTERED_GAME_QUEUE, messageConverter = "#{jackson2JsonMessageConverter}")
    public void registerGameOnPlatform(GameAddedEvent gameAddedEvent) {
        registerNewGameUseCase.register(gameAddedEvent);
    }
}
