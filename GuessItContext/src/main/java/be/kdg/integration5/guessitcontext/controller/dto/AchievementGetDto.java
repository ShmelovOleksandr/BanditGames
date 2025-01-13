package be.kdg.integration5.guessitcontext.controller.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AchievementGetDto {
    private final UUID achievementId;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String achievementType;
    private final Boolean isAchieved;

    public AchievementGetDto(UUID achievementId, String name, String description, String imageUrl, String achievementType, Boolean isAchieved) {
        if (achievementId == null || name == null || description == null || imageUrl == null || achievementType == null || isAchieved == null)
            throw new NullPointerException();

        this.achievementId = achievementId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.achievementType = achievementType;
        this.isAchieved = isAchieved;
    }
}
