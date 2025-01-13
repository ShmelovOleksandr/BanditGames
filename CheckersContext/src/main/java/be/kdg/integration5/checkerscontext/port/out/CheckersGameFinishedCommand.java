package be.kdg.integration5.checkerscontext.port.out;

import be.kdg.integration5.checkerscontext.domain.GameId;
import be.kdg.integration5.checkerscontext.domain.PlayerId;

import java.util.Objects;

public record CheckersGameFinishedCommand(GameId gameId, PlayerId winnerId, Boolean isDraw) {
    public CheckersGameFinishedCommand {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(isDraw);
    }
}
