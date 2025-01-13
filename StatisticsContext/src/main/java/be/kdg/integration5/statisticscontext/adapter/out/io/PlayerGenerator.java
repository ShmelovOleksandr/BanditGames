package be.kdg.integration5.statisticscontext.adapter.out.io;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.port.out.GeneratePlayerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerGenerator implements GeneratePlayerPort {

    @Value("${data.players.csv}")
    private String playerCsvFileName;

    private final PlayerCsvConverter playerCsvConverter;

    public PlayerGenerator(PlayerCsvConverter playerCsvConverter) {
        this.playerCsvConverter = playerCsvConverter;
    }

    @Override
    public List<Player> readPlayerCsv() {
        List<String[]> players = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(playerCsvFileName);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    players.add(values);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read the players CSV file", e);
        }
        return playerCsvConverter.fromCsvRows(players);
    }

}
