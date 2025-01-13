package be.kdg.integration5.guessitcontext.service;

import be.kdg.integration5.common.events.GameAddedEvent;

import java.util.List;

public interface RulesService {
    List<GameAddedEvent.GameRule> loadRules();
}
