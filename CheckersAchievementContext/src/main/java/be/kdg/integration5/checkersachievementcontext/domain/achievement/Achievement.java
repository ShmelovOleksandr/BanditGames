package be.kdg.integration5.checkersachievementcontext.domain.achievement;

import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class Achievement {
    private AchievementId achievementId;
    private final String name;
    private String description;
    private String imagUrl;

    @Setter(AccessLevel.NONE)
    private boolean isAchieved;

    public Achievement(AchievementId achievementId, String name, String description, String imagUrl) {
        this.achievementId = achievementId;
        this.name = name;
        this.description = description;
        this.imagUrl = imagUrl;
    }

    public Achievement(AchievementId achievementId, String name, String description, String imagUrl, boolean isAchieved) {
        this.achievementId = achievementId;
        this.name = name;
        this.description = description;
        this.imagUrl = imagUrl;
        this.isAchieved = isAchieved;
    }

    public void open() {
        this.isAchieved = true;
    }

    public boolean isFulfilled(Game game, PlayerId playerId) {
        if (this.isAchieved)
            return true;
        return isConditionMet(game, playerId);
    }

    protected abstract boolean isConditionMet(Game game, PlayerId playerId);

}
