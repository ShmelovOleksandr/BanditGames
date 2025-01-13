package be.kdg.integration5.gameplatformcontext.port.out;

import be.kdg.integration5.common.events.GameAddedEvent;

public interface NotifyChatBotPort {
    void notify(GameAddedEvent event);
}
