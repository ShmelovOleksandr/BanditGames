package be.kdg.integration5.common.events;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record LobbyCreatedEvent(String gameTitle, UUID lobbyId, UUID firstPlayerId, List<PlayerEvent> players) {

    public LobbyCreatedEvent {
        Objects.requireNonNull(gameTitle);
        Objects.requireNonNull(lobbyId);
        Objects.requireNonNull(firstPlayerId);
        Objects.requireNonNull(players);
    }

    public record PlayerEvent(UUID playerId, String username) {
        public PlayerEvent {
            Objects.requireNonNull(playerId);
            Objects.requireNonNull(username);
        }
    }
}
