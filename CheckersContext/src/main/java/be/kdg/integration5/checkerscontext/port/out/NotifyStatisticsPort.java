package be.kdg.integration5.checkerscontext.port.out;

import be.kdg.integration5.common.events.FinishGameSessionEvent;
import be.kdg.integration5.common.events.PlayerMoveEvent;
import be.kdg.integration5.common.events.StartGameSessionEvent;

public interface NotifyStatisticsPort {

    void notifyPlayerMove(PlayerMoveEvent event);
    void notifyGameSessionStarted(StartGameSessionEvent event);
}
