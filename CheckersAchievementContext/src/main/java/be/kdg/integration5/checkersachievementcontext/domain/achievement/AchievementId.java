package be.kdg.integration5.checkersachievementcontext.domain.achievement;

import java.util.Objects;
import java.util.UUID;

public record AchievementId(UUID uuid) {
    public AchievementId {
        Objects.requireNonNull(uuid);
    }
}
