package be.kdg.integration5.checkersachievementcontext.adapter.in.api;

import be.kdg.integration5.checkersachievementcontext.adapter.in.api.dto.AchievementGetDto;
import be.kdg.integration5.checkersachievementcontext.adapter.in.api.util.AchievementDtoConverter;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.port.in.GetAllOpenAchievementsForPlayerUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players/{playerId}/achievements")
public class PlayerRestController {
    private final GetAllOpenAchievementsForPlayerUseCase getAllOpenAchievementsForPlayerUseCase;
    private final AchievementDtoConverter achievementDtoConverter;

    @Autowired
    public PlayerRestController(GetAllOpenAchievementsForPlayerUseCase getAllOpenAchievementsForPlayerUseCase, AchievementDtoConverter achievementDtoConverter) {
        this.getAllOpenAchievementsForPlayerUseCase = getAllOpenAchievementsForPlayerUseCase;
        this.achievementDtoConverter = achievementDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<AchievementGetDto>> getAllAchievementsForPlayer(@PathVariable("playerId") UUID playerId, @RequestParam(value = "open", required = false) Boolean open) {
        List<Achievement> allAchievementsForPlayer = getAllOpenAchievementsForPlayerUseCase.getAllAchievementsForPlayer(new PlayerId(playerId), open);
        if (allAchievementsForPlayer.isEmpty())
            return ResponseEntity.noContent().build();

        List<AchievementGetDto> achievementGetDtos = allAchievementsForPlayer.stream().map(achievementDtoConverter::toDto).toList();
        return ResponseEntity.ok(achievementGetDtos);
    }
}
