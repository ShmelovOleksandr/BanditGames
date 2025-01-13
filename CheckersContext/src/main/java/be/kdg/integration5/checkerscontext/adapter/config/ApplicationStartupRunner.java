package be.kdg.integration5.checkerscontext.adapter.config;

import be.kdg.integration5.checkerscontext.port.out.NotifyGamePlatformPort;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final NotifyGamePlatformPort notifyGamePlatformPort;
    private boolean hasRun = false;

    public ApplicationStartupRunner(NotifyGamePlatformPort notifyGamePlatformPort) {
        this.notifyGamePlatformPort = notifyGamePlatformPort;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!hasRun) {
            notifyGamePlatformPort.notifyGamePlatform();
            hasRun = true; // Mark it as executed
        }
    }
}