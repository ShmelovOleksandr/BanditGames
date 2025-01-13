package be.kdg.integration5.guessitcontext.config;

import be.kdg.integration5.guessitcontext.service.GameService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements ApplicationListener<ApplicationReadyEvent> {

    private boolean hasRun = false;
    private final GameService gameService;

    public ApplicationStartupRunner(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!hasRun) {
            gameService.notifyGameRegistration();
            hasRun = true; // Mark it as executed
        }
    }
}