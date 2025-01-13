package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.util.AchievementJpaConverter;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlayerJpaConverter {
    private final AchievementJpaConverter achievementJpaConverter;

    @Autowired
    public PlayerJpaConverter(AchievementJpaConverter achievementJpaConverter) {
        this.achievementJpaConverter = achievementJpaConverter;
    }

    public PlayerJpaEntity toJpa(Player player) {
        PlayerJpaEntity playerJpaEntity = new PlayerJpaEntity(player.getPlayerId().uuid());
        Set<AchievementJpaEntity> achievementJpaEntities = player.getAchievements().stream().map(achievement -> achievementJpaConverter.toJpa(achievement, playerJpaEntity)).collect(Collectors.toSet());
        achievementJpaEntities.forEach(achievementJpaEntity -> achievementJpaEntity.setPerformer(playerJpaEntity));
        playerJpaEntity.setAchievements(achievementJpaEntities);
        return playerJpaEntity;
    }

    public Player toDomain(PlayerJpaEntity playerJpaEntity) {
        return new Player(
                new PlayerId(playerJpaEntity.getPlayerId()),
                playerJpaEntity.getAchievements().stream().map(achievementJpaConverter::toDomain).collect(Collectors.toSet())
        );
    }
}
