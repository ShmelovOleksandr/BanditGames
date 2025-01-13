package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.util;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.types.MovePieceAchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.types.PlayNGamesAchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.AchievementId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.cumulativeachievement_types.PlayNGamesAchievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.gamestateachievement_types.MovePieceAchievement;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AchievementJpaConverter {

    // TODO
    //  Implement proper solution with Strategy pattern if time allows
    public AchievementJpaEntity toJpa(@NonNull Achievement achievement, PlayerJpaEntity performer) {
        if (achievement instanceof PlayNGamesAchievement playNGamesAchievement) {
            return new PlayNGamesAchievementJpaEntity(
                    playNGamesAchievement.getAchievementId().uuid(),
                    playNGamesAchievement.getName(),
                    playNGamesAchievement.getDescription(),
                    playNGamesAchievement.getImagUrl(),
                    playNGamesAchievement.isAchieved(),
                    performer,
                    playNGamesAchievement.getGoal(),
                    playNGamesAchievement.getCounter().stream().map(GameId::uuid).collect(Collectors.toSet())
            );
        } else if (achievement instanceof MovePieceAchievement) {
            return new MovePieceAchievementJpaEntity(
                    achievement.getAchievementId().uuid(),
                    achievement.getName(),
                    achievement.getDescription(),
                    achievement.getImagUrl(),
                    achievement.isAchieved(),
                    performer
            );
        }

        throw new RuntimeException("No compatible achievement type found");
    }


    // TODO
    //  Implement proper solution with Strategy pattern if time allows
    public Achievement toDomain(@NonNull AchievementJpaEntity achievementJpaEntity) {
        if (achievementJpaEntity instanceof PlayNGamesAchievementJpaEntity playNGamesAchievementJpaEntity) {
            return new PlayNGamesAchievement(
                    new AchievementId(playNGamesAchievementJpaEntity.getAchievementId().getAchievementId()),
                    playNGamesAchievementJpaEntity.getName(),
                    playNGamesAchievementJpaEntity.getDescription(),
                    playNGamesAchievementJpaEntity.getImageUrl(),
                    playNGamesAchievementJpaEntity.isAchieved(),
                    playNGamesAchievementJpaEntity.getDesiredNumberOfGames(),
                    playNGamesAchievementJpaEntity.getGameIdsPlayed().stream().map(GameId::new).collect(Collectors.toSet())
            );
        } else if (achievementJpaEntity instanceof MovePieceAchievementJpaEntity) {
            return new MovePieceAchievement(
                    new AchievementId(achievementJpaEntity.getAchievementId().getAchievementId()),
                    achievementJpaEntity.getName(),
                    achievementJpaEntity.getDescription(),
                    achievementJpaEntity.getImageUrl(),
                    achievementJpaEntity.isAchieved()
            );
        }

        throw new RuntimeException("No compatible achievement type found");
    }
}
