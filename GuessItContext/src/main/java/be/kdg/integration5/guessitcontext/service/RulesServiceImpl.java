package be.kdg.integration5.guessitcontext.service;

import be.kdg.integration5.common.events.GameAddedEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class RulesServiceImpl implements RulesService {

    private final ObjectMapper objectMapper;

    public RulesServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public List<GameAddedEvent.GameRule> loadRules() {
        try (InputStream inputStream = getClass().getResourceAsStream("/rules.json")) {
            return objectMapper.readValue(inputStream, new TypeReference<List<GameAddedEvent.GameRule>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load rules from JSON file", e);
        }
    }
}
