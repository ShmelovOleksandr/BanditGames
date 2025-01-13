package be.kdg.integration5.checkersachievementcontext.core;

import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.port.in.GetAllOpenAchievementsForPlayerUseCase;
import be.kdg.integration5.checkersachievementcontext.port.out.FindAchievementsPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllOpenAchievementsForPlayerUseCaseImpl implements GetAllOpenAchievementsForPlayerUseCase {
    private final FindAchievementsPort findAchievementsPort;

    @Autowired
    public GetAllOpenAchievementsForPlayerUseCaseImpl(FindAchievementsPort findAchievementsPort) {
        this.findAchievementsPort = findAchievementsPort;
    }

    @Override
    public List<Achievement> getAllAchievementsForPlayer(PlayerId playerId, Boolean isAchieved) {
        if (isAchieved == null)
            return findAchievementsPort.findAllAchievementsForPlayer(playerId);

        return findAchievementsPort.findAllAchievementsForPlayerByIsAchieved(playerId, isAchieved);
    }
}
