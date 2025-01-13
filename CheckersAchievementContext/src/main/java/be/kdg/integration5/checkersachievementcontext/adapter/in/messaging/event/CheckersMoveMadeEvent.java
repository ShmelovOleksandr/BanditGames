package be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record CheckersMoveMadeEvent(UUID gameId, UUID moverId, CheckersMovePosition oldPosition, CheckersMovePosition newPosition, LocalDateTime madeAt) {
    public CheckersMoveMadeEvent {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(moverId);
        Objects.requireNonNull(oldPosition);
        Objects.requireNonNull(newPosition);
        Objects.requireNonNull(madeAt);
    }

    public record CheckersMovePosition(int x, int y) {
        public CheckersMovePosition {
            if (x < 0 || y < 0)
                throw new IllegalArgumentException("X and Y cannot be negative.");
        }
    }
}
