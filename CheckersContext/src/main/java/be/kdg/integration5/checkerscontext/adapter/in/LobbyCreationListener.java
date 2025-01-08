package be.kdg.integration5.checkerscontext.adapter.in;

import be.kdg.integration5.checkerscontext.port.in.CreateGameCommand;
import be.kdg.integration5.checkerscontext.port.in.CreateGameUseCase;
import be.kdg.integration5.common.events.LobbyCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LobbyCreationListener {

    private static final String LOBBY_QUEUE = "lobby_queue";

    private final CreateGameUseCase createGameUseCase;
    private final Logger logger = LoggerFactory.getLogger(LobbyCreationListener.class);

    @Value("${game.name}")
    private  String gameName;

    public LobbyCreationListener(CreateGameUseCase createGameUseCase) {
        this.createGameUseCase = createGameUseCase;
    }

    @RabbitListener(queues = LOBBY_QUEUE, messageConverter = "#{jackson2JsonMessageConverter}")
    public void createGameFromLobby(LobbyCreatedEvent event) {
        if (event.gameName().equals(this.gameName)) {
            CreateGameCommand command = new CreateGameCommand(
                    event.lobbyId(),
                    event.players().stream()
                            .map(pl -> new CreateGameCommand.PlayerJoinedCommand(
                                    pl.playerId(),
                                    pl.username()
                            ))
                            .collect(Collectors.toList()));
            logger.info("Receive lobby created event: {}", event.lobbyId());
            createGameUseCase.initiate(command);
        }
    }
}
