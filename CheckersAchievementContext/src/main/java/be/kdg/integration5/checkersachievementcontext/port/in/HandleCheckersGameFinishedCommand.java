package be.kdg.integration5.checkersachievementcontext.port.in;

import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;

import java.util.Objects;

public record HandleCheckersGameFinishedCommand(GameId gameId, PlayerId winner, Boolean isDraw) {
    public HandleCheckersGameFinishedCommand {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(isDraw);
    }
}
