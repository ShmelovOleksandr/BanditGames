package be.kdg.integration5.guessitcontext.controller.api;

import be.kdg.integration5.guessitcontext.controller.dto.AchievementGetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players/{playerId}/achievements")
public class PlayerAchievementsController {
    @GetMapping
    public ResponseEntity<List<AchievementGetDto>> getAllAchievementsForPlayer(@PathVariable("playerId") UUID playerId, @RequestParam(value = "open", required = false) Boolean open) {
        return ResponseEntity.noContent().build();
    }
}
