package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.util.AchievementJpaConverter;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.AchievementsProvider;
import be.kdg.integration5.checkersachievementcontext.port.out.FindAchievementsPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementDatabaseAdapter implements FindAchievementsPort {
    private final AchievementJpaRepository achievementJpaRepository;
    private final AchievementJpaConverter achievementJpaConverter;

    @Autowired
    public AchievementDatabaseAdapter(AchievementJpaRepository achievementJpaRepository, AchievementJpaConverter achievementJpaConverter) {
        this.achievementJpaRepository = achievementJpaRepository;
        this.achievementJpaConverter = achievementJpaConverter;
    }

    @Override
    public List<Achievement> findAllAchievementsForPlayer(PlayerId playerId) {
        return achievementJpaRepository.findAllByPerformer_PlayerId(playerId.uuid()).stream().map(achievementJpaConverter::toDomain).toList();
    }

    @Override
    public List<Achievement> findAllAchievementsForPlayerByIsAchieved(PlayerId playerId, boolean isAchieved) {
        return achievementJpaRepository.findAllByIsAchievedAndPerformer_PlayerId(isAchieved, playerId.uuid()).stream().map(achievementJpaConverter::toDomain).toList();
    }


    //TODO
    // Separate FindAchievementsPort into smaller interfaces and move this implementation to the separate class.
    // Change if time allows
    @Override
    public List<Achievement> findAll() {
        return new ArrayList<>(AchievementsProvider.ACHIEVEMENTS_SET);
    }
}
