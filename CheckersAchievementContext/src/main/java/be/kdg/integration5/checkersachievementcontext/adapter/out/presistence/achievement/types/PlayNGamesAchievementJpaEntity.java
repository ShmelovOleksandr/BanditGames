package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.types;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.util.StringSetUUIDConverter;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@DiscriminatorValue("PLAYNGAMES")
public class PlayNGamesAchievementJpaEntity extends AchievementJpaEntity {
    @Column
    private int desiredNumberOfGames;

    @Convert(converter = StringSetUUIDConverter.class)
    @Column
    private Set<UUID> gameIdsPlayed;

    public PlayNGamesAchievementJpaEntity() {
    }

    public PlayNGamesAchievementJpaEntity(UUID achievementId, String name, String description, String imageUrl, boolean isAchieved, PlayerJpaEntity performer, int desiredNumberOfGames, Set<UUID> gameIdsPlayed) {
        super(achievementId, name, description, imageUrl, isAchieved, performer);
        this.desiredNumberOfGames = desiredNumberOfGames;
        this.gameIdsPlayed = gameIdsPlayed;
    }

}
