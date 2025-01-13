package be.kdg.integration5.checkersachievementcontext.adapter.in.messaging.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CheckersGameStartedEvent(UUID gameId, List<UUID> playerIds, LocalDateTime startedAt) {
    public CheckersGameStartedEvent {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(playerIds);
        Objects.requireNonNull(startedAt);
    }
}
