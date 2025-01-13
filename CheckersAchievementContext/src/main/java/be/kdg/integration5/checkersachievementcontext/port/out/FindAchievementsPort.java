package be.kdg.integration5.checkersachievementcontext.port.out;

import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;

import java.util.List;

public interface FindAchievementsPort {
    List<Achievement> findAllAchievementsForPlayer(PlayerId playerId);

    List<Achievement> findAllAchievementsForPlayerByIsAchieved(PlayerId playerId, boolean isAchieved);

    List<Achievement> findAll();
}
