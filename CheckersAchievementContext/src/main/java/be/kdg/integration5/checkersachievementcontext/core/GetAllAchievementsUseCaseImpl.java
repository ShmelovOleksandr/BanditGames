package be.kdg.integration5.checkersachievementcontext.core;

import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.port.in.GetAllAchievementsUseCase;
import be.kdg.integration5.checkersachievementcontext.port.out.FindAchievementsPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllAchievementsUseCaseImpl implements GetAllAchievementsUseCase {
    private final FindAchievementsPort findAchievementsPort;

    @Autowired
    public GetAllAchievementsUseCaseImpl(FindAchievementsPort findAchievementsPort) {
        this.findAchievementsPort = findAchievementsPort;
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return findAchievementsPort.findAll();
    }
}
