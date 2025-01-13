package be.kdg.integration5.checkersachievementcontext.port.in;

import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.PiecePosition;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;

import java.time.LocalDateTime;
import java.util.Objects;

public record HandleCheckersMoveMadeCommand(GameId gameId, PlayerId moverId, PiecePosition oldPosition, PiecePosition newPosition, LocalDateTime madeAt) {
    public HandleCheckersMoveMadeCommand {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(moverId);
        Objects.requireNonNull(oldPosition);
        Objects.requireNonNull(newPosition);
        Objects.requireNonNull(madeAt);
    }
}
