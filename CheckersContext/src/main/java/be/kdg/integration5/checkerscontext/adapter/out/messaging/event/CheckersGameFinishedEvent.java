package be.kdg.integration5.checkerscontext.adapter.out.messaging.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record CheckersGameFinishedEvent(UUID gameId, UUID winnerId, Boolean isDraw, LocalDateTime finishedAt) {
    public CheckersGameFinishedEvent {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(isDraw);
        Objects.requireNonNull(finishedAt);
    }
}
