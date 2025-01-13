package be.kdg.integration5.checkersachievementcontext.domain;

import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.AchievementsProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;


@Getter
@Setter
@ToString(exclude = "achievements")
public class Player {
    private PlayerId playerId;
    private Set<Achievement> achievements;

    public Player(PlayerId playerId) {
        this.playerId = playerId;
        this.achievements = AchievementsProvider.ACHIEVEMENTS_SET;
    }

    public Player(PlayerId playerId, Set<Achievement> achievements) {
        this.playerId = playerId;
        this.achievements = achievements;
    }

    public void checkAchievementsFulfilled(Game game) {
        achievements.forEach(achievement -> {
            if (achievement.isFulfilled(game, playerId))
                achievement.open();
        });
    }
}
