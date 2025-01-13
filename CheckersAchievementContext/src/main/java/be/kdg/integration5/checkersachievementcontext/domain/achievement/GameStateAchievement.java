package be.kdg.integration5.checkersachievementcontext.domain.achievement;

public abstract class GameStateAchievement extends Achievement {

    public GameStateAchievement(AchievementId achievementId, String name, String description, String imagUrl) {
        super(achievementId, name, description, imagUrl);
    }

    public GameStateAchievement(AchievementId achievementId, String name, String description, String imagUrl, boolean isAchieved) {
        super(achievementId, name, description, imagUrl, isAchieved);
    }
}
