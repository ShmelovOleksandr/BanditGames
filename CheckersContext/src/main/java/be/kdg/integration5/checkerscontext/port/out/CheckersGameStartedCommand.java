package be.kdg.integration5.checkerscontext.port.out;

import be.kdg.integration5.checkerscontext.domain.GameId;
import be.kdg.integration5.checkerscontext.domain.Player;

import java.util.List;
import java.util.Objects;

public record CheckersGameStartedCommand(GameId gameId, List<Player> players) {
    public CheckersGameStartedCommand {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(players);
    }
}
