package be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CumulativeAchievementGetDto extends AchievementGetDto {
    private static final String achievementTypeName = "CUMULATIVE_ACHIEVEMENT";

    private final Long desiredAmount;
    private final Long currentAmount;

    public CumulativeAchievementGetDto(UUID achievementId, String name, String description, String imageUrl, Boolean isAchieved, Long desiredAmount, Long currentAmount) {
        super(achievementId, name, description, imageUrl, achievementTypeName, isAchieved);
        if (desiredAmount == null || currentAmount == null)
            throw new NullPointerException("desiredAmount or currentAmount is null");

        this.desiredAmount = desiredAmount;
        this.currentAmount = currentAmount;
    }
}
