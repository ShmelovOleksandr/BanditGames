package be.kdg.integration5.checkersachievementcontext.domain.achievement;

import be.kdg.integration5.checkersachievementcontext.domain.achievement.cumulativeachievement_types.PlayNGamesAchievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.gamestateachievement_types.MovePieceAchievement;

import java.util.Set;
import java.util.UUID;

//TODO
// Come up with a better solution if time allows
public abstract class AchievementsProvider {
    public static final Set<Achievement> ACHIEVEMENTS_SET = Set.of(
            new PlayNGamesAchievement(new AchievementId(UUID.fromString("ad4c99a5-1184-4e80-a087-05e2c36d0435")), "Play 10 Games", "You need to play 10 times the Checkers Game.",  "https://picsum.photos/seed/picsum/720/480", 10),
            new MovePieceAchievement(new AchievementId(UUID.fromString("74706643-9d78-4591-9bc7-a156a95a2eb6")), "Move Your Piece", "You need to move any piece in the game.", "https://picsum.photos/seed/picsum/720/480")
    );
}
