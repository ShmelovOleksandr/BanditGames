package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.types;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@DiscriminatorValue("MOVEPIECE")
public class MovePieceAchievementJpaEntity extends AchievementJpaEntity {
    public MovePieceAchievementJpaEntity() {
    }

    public MovePieceAchievementJpaEntity(UUID achievementId, String name, String description, String imageUrl, boolean isAchieved, PlayerJpaEntity performer) {
        super(achievementId, name, description, imageUrl, isAchieved, performer);
    }
}
