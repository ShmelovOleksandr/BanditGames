package be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto;

import java.util.UUID;

public class GameStateAchievementGetDto extends AchievementGetDto {
    private static final String achievementTypeName = "GAME_STATE_ACHIEVEMENT";

    public GameStateAchievementGetDto(UUID achievementId, String name, String description, String imageUrl, Boolean isAchieved) {
        super(achievementId, name, description, imageUrl, achievementTypeName, isAchieved);
    }
}
