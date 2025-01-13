package be.kdg.integration5.checkersachievementcontext.core;

import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameFinishedCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameFinishedUseCase;
import be.kdg.integration5.checkersachievementcontext.port.out.FindGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistGamePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HandleCheckersGameFinishedUseCaseImpl implements HandleCheckersGameFinishedUseCase {
    private final FindGamePort findGamePort;
    private final PersistGamePort persistGamePort;

    @Autowired
    public HandleCheckersGameFinishedUseCaseImpl(FindGamePort findGamePort, PersistGamePort persistGamePort) {
        this.findGamePort = findGamePort;
        this.persistGamePort = persistGamePort;
    }

    @Override
    @Transactional
    public void handleCheckersGameFinished(HandleCheckersGameFinishedCommand handleCheckersGameFinishedCommand) {
        GameId gameId = handleCheckersGameFinishedCommand.gameId();
        Game game = findGamePort.findById(gameId);

        game.finish();

        List<Player> players = game.getPlayers();
        for (Player player : players)
            player.checkAchievementsFulfilled(game);

        persistGamePort.save(game);
    }
}
