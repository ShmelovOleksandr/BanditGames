package be.kdg.integration5.checkersachievementcontext.port.in;

import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;

import java.util.List;
import java.util.Objects;

public record HandleCheckersGameStartedCommand(GameId gameId, List<PlayerId> playerIds) {
    public HandleCheckersGameStartedCommand {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(playerIds);
    }
}
