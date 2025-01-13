package be.kdg.integration5.checkersachievementcontext.core;

import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedUseCase;
import be.kdg.integration5.checkersachievementcontext.port.out.FindPlayerPort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistPlayerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HandleCheckersGameStartedUseCaseImpl implements HandleCheckersGameStartedUseCase {
    private final FindPlayerPort findPlayerPort;
    private final PersistPlayerPort persistPlayerPort;
    private final PersistGamePort persistGamePort;

    public HandleCheckersGameStartedUseCaseImpl(FindPlayerPort findPlayerPort, PersistPlayerPort persistPlayerPort, PersistGamePort persistGamePort) {
        this.findPlayerPort = findPlayerPort;
        this.persistPlayerPort = persistPlayerPort;
        this.persistGamePort = persistGamePort;
    }

    @Override
    @Transactional
    public void handleCheckersGameStarted(HandleCheckersGameStartedCommand handleCheckersGameStartedCommand) {
        GameId gameId = handleCheckersGameStartedCommand.gameId();
        List<PlayerId> playerIds = handleCheckersGameStartedCommand.playerIds();
        List<Player> fetchedPlayersList = findPlayerPort.findAllById(playerIds);

        List<Player> players = !fetchedPlayersList.isEmpty() ? fetchedPlayersList : playerIds.stream().map(Player::new).toList();

        Game game = new Game(gameId, players);

        for (Player player : players)
            player.checkAchievementsFulfilled(game);

        persistPlayerPort.saveAll(players);
        persistGamePort.save(game);
    }
}
