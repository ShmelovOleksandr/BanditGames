package be.kdg.integration5.guessitcontext.controller.api;

import be.kdg.integration5.guessitcontext.controller.dto.AchievementGetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementsController {

    @GetMapping
    public ResponseEntity<List<AchievementGetDto>> getAllAchievements() {
        return ResponseEntity.noContent().build();
    }
}
