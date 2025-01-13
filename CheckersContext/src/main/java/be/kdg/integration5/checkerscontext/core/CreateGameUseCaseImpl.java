package be.kdg.integration5.checkerscontext.core;

import be.kdg.integration5.checkerscontext.domain.Game;
import be.kdg.integration5.checkerscontext.domain.GameId;
import be.kdg.integration5.checkerscontext.domain.Player;
import be.kdg.integration5.checkerscontext.domain.PlayerId;
import be.kdg.integration5.checkerscontext.port.in.CreateGameCommand;
import be.kdg.integration5.checkerscontext.port.in.CreateGameUseCase;
import be.kdg.integration5.checkerscontext.port.out.*;
import be.kdg.integration5.common.events.StartGameSessionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CreateGameUseCaseImpl implements CreateGameUseCase {
    private final PersistGamePort persistGamePort;
    private final PersistPlayerPort persistPlayerPort;
    private final NotifyCheckersGameStartedPort notifyCheckersGameStartedPort;
    private final NotifyStatisticsPort notifyStatisticsPort;

    @Value("${game.name}")
    private String gameName;

    public CreateGameUseCaseImpl(PersistGamePort persistGamePort, PersistPlayerPort persistPlayerPort, NotifyCheckersGameStartedPort notifyCheckersGameStartedPort, NotifyStatisticsPort notifyStatisticsPort) {
        this.persistGamePort = persistGamePort;
        this.persistPlayerPort = persistPlayerPort;
        this.notifyCheckersGameStartedPort = notifyCheckersGameStartedPort;
        this.notifyStatisticsPort = notifyStatisticsPort;
    }

    @Override
    public void initiate(CreateGameCommand command) {
        List<Player> players = command.playersJoined().stream().map(
                pj -> new Player(new PlayerId(pj.playerId()), pj.playerName())
        ).toList();
        players = persistPlayerPort.saveAll(players);
        Game game = new Game(new GameId(command.lobbyId()), players);
        game.start();
        persistGamePort.save(game);
        notifyCheckersGameStartedPort.notifyCheckersGameStarted(new CheckersGameStartedCommand(game.getPlayedMatchId(), game.getPlayers()));
        notifyStatisticsPort.notifyGameSessionStarted(new StartGameSessionEvent(
                gameName,
                game.getPlayedMatchId().uuid(),
                game.getBoard().getCurrentPlayer().getPlayerId().uuid(),
                players.stream().map(Player::getPlayerId).map(PlayerId::uuid).toList(),
                LocalDateTime.now()
        ));
    }
}
