package be.kdg.integration5.checkersachievementcontext.adapter.in.api;

import be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto.AchievementGetDto;
import be.kdg.integration5.checkersachievementcontext.adapter.in.api.util.AchievementDtoConverter;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.port.in.GetAllAchievementsUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementsRestController {
    private final GetAllAchievementsUseCase getAllAchievementsUseCase;
    private final AchievementDtoConverter achievementDtoConverter;

    @Autowired
    public AchievementsRestController(GetAllAchievementsUseCase getAllAchievementsUseCase, AchievementDtoConverter achievementDtoConverter) {
        this.getAllAchievementsUseCase = getAllAchievementsUseCase;
        this.achievementDtoConverter = achievementDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<AchievementGetDto>> getAllAchievements() {
        List<Achievement> allAchievements = getAllAchievementsUseCase.getAllAchievements();
        if (allAchievements.isEmpty())
            return ResponseEntity.noContent().build();

        List<AchievementGetDto> achievementGetDtos = allAchievements.stream().map(achievementDtoConverter::toDto).toList();
        return ResponseEntity.ok(achievementGetDtos);
    }
}
