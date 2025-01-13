package be.kdg.integration5.checkersachievementcontext.core;

import be.kdg.integration5.checkersachievementcontext.domain.*;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeCommand;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeUseCase;
import be.kdg.integration5.checkersachievementcontext.port.out.FindGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.FindPlayerPort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistPlayerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HandleCheckersMoveMadeUseCaseImpl implements HandleCheckersMoveMadeUseCase {
    private final FindGamePort findGamePort;
    private final PersistGamePort persistGamePort;
    private final FindPlayerPort findPlayerPort;
    private final PersistPlayerPort persistPlayerPort;

    @Autowired
    public HandleCheckersMoveMadeUseCaseImpl(FindGamePort findGamePort, PersistGamePort persistGamePort, FindPlayerPort findPlayerPort, PersistPlayerPort persistPlayerPort) {
        this.findGamePort = findGamePort;
        this.persistGamePort = persistGamePort;
        this.findPlayerPort = findPlayerPort;
        this.persistPlayerPort = persistPlayerPort;
    }

    @Override
    @Transactional
    public void handleCheckersMoveMade(HandleCheckersMoveMadeCommand handleCheckersMoveMadeCommand) {
        GameId gameId = handleCheckersMoveMadeCommand.gameId();
        Game game = findGamePort.findById(gameId);
        Player mover = findPlayerPort.findById(handleCheckersMoveMadeCommand.moverId());

        Move madeMove = new Move(
                mover,
                handleCheckersMoveMadeCommand.oldPosition(),
                handleCheckersMoveMadeCommand.newPosition(),
                handleCheckersMoveMadeCommand.madeAt()
        );

        Board board = game.getBoard();
        board.addMove(madeMove);

        List<Player> players = game.getPlayers();
        for (Player player : players)
            player.checkAchievementsFulfilled(game);

        persistPlayerPort.saveAll(players);
        persistGamePort.save(game);
    }
}
