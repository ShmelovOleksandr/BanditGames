package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement;

import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AchievementJpaRepository extends JpaRepository<AchievementJpaEntity, String> {
    List<AchievementJpaEntity> findAllByIsAchievedAndPerformer_PlayerId(boolean isAchieved, UUID playerId);

    List<AchievementJpaEntity> findAllByPerformer_PlayerId(UUID performerId);
}
