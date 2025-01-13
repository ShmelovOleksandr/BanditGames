package be.kdg.integration5.checkerscontext.port.out;

import be.kdg.integration5.common.events.GameAddedEvent;

import java.util.List;

public interface LoadGameRulesPort {
    List<GameAddedEvent.GameRule> loadRules();
}
