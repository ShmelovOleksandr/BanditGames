package be.kdg.integration5.checkersachievementcontext.domain.achievement;

import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import lombok.Getter;

@Getter
public abstract class CumulativeAchievement<GoalClass, CounterClass> extends Achievement {
    protected final GoalClass goal;
    protected CounterClass counter;

    public CumulativeAchievement(AchievementId achievementId, String name, String description, String imagUrl, GoalClass goal, CounterClass counter) {
        super(achievementId, name, description, imagUrl);
        this.goal = goal;
        this.counter = counter;
    }

    public CumulativeAchievement(AchievementId achievementId, String name, String description, String imagUrl, boolean isAchieved, GoalClass goal, CounterClass counter) {
        super(achievementId, name, description, imagUrl, isAchieved);
        this.goal = goal;
        this.counter = counter;
    }

    public abstract long getGoalInNumber();
    public abstract long getCounterInNumber();

    public abstract boolean compare(GoalClass goal, CounterClass counter);

    public abstract void checkConditionAndHandle(Game game, PlayerId playerId);

    @Override
    public boolean isConditionMet(Game game, PlayerId playerId) {
        checkConditionAndHandle(game, playerId);

        return compare(goal, counter);
    }
}
