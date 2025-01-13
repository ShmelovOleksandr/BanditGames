package be.kdg.integration5.checkersachievementcontext.port.in;

import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;

import java.util.List;

public interface GetAllOpenAchievementsForPlayerUseCase {

    List<Achievement> getAllAchievementsForPlayer(PlayerId playerId, Boolean isAchieved);
}
