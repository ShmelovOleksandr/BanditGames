package be.kdg.integration5.checkersachievementcontext.adapter.in.api.util;

import be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto.AchievementGetDto;
import be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto.CumulativeAchievementGetDto;
import be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto.GameStateAchievementGetDto;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.CumulativeAchievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.GameStateAchievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementDtoConverter {

    public AchievementGetDto toDto(Achievement achievement) {
        if (achievement instanceof GameStateAchievement) {
            return new GameStateAchievementGetDto(
                    achievement.getAchievementId().uuid(),
                    achievement.getName(),
                    achievement.getDescription(),
                    achievement.getImagUrl(),
                    achievement.isAchieved()
            );
        } else if (achievement instanceof CumulativeAchievement<?, ?> cumulativeAchievement) {
            return new CumulativeAchievementGetDto(
                    cumulativeAchievement.getAchievementId().uuid(),
                    cumulativeAchievement.getName(),
                    cumulativeAchievement.getDescription(),
                    cumulativeAchievement.getImagUrl(),
                    cumulativeAchievement.isAchieved(),
                    cumulativeAchievement.getGoalInNumber(),
                    cumulativeAchievement.getCounterInNumber()
            );
        }
        throw new RuntimeException("No compatible achievement type found");
    }

}
