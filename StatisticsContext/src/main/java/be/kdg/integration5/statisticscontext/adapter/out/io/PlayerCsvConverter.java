package be.kdg.integration5.statisticscontext.adapter.out.io;

import be.kdg.integration5.statisticscontext.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PlayerCsvConverter {

    public List<String[]> toCsvRows(List<Player> players) {
        List<String[]> rows = new ArrayList<>();
        for (Player player: players) {
            Metrics metrics = player.getMetrics();
            Predictions predictions = player.getPredictions();
            rows.add(new String[]{
                    player.getPlayerId().uuid().toString(),
                    player.getLocation().country(),
                    player.getLocation().city(),
                    Integer.toString(player.getAge()),
                    player.getGender().name(),
                    Integer.toString(metrics.getTotalGamesPlayed()),
                    Integer.toString(metrics.getTotalWins()),
                    Integer.toString(metrics.getTotalLosses()),
                    Integer.toString(metrics.getTotalDraws()),
                    Integer.toString(metrics.getTotalIsFirst()),
                    Double.toString(metrics.getAvgMoveDuration()),
                    Integer.toString(metrics.getTotalWeekdaysPlayed()),
                    Integer.toString(metrics.getTotalWeekendsPlayed()),
                    Integer.toString(metrics.getTotalMorningPlays()),
                    Integer.toString(metrics.getTotalAfternoonPlays()),
                    Integer.toString(metrics.getTotalEveningPlays()),
                    Integer.toString(metrics.getTotalNightPlays()),
                    Double.toString(predictions.getChurn()),
                    Double.toString(predictions.getFirstMoveWinProbability()),
                    predictions.getPlayerClass().name()
            });
        }
        return rows;
    }

    public List<Player> fromCsvRows(List<String[]> csvRows) {
        List<Player> players = new ArrayList<>();
        for (String[] row : csvRows) {
            try {
                // Convert each row into a Player object
                Player player = new Player(
                        row[1], // PlayerId
                        new PlayerId(UUID.fromString(row[0])), // Name
                                        Integer.parseInt(row[2]), // Age
                                        Player.Gender.valueOf(row[3].toUpperCase()), // Gender
                                        new Location(row[4], row[5]) // Country, City
                                );

                // Add Metrics
                Metrics metrics = new Metrics();
                metrics.setTotalGamesPlayed(Integer.parseInt(row[6]));
                metrics.setTotalWins(Integer.parseInt(row[7]));
                metrics.setTotalLosses(Integer.parseInt(row[8]));
                metrics.setTotalDraws(Integer.parseInt(row[9]));
                metrics.setTotalIsFirst(Integer.parseInt(row[10]));
                metrics.setAvgMoveDuration(Double.parseDouble(row[11]));
                metrics.setAvgMoveAmount(Double.parseDouble(row[12]));
                metrics.setAvgGameDuration(Double.parseDouble(row[13]));
                metrics.setTotalWeekdaysPlayed(Integer.parseInt(row[14]));
                metrics.setTotalWeekendsPlayed(Integer.parseInt(row[15]));
                metrics.setTotalMorningPlays(Integer.parseInt(row[16]));
                metrics.setTotalAfternoonPlays(Integer.parseInt(row[17]));
                metrics.setTotalEveningPlays(Integer.parseInt(row[18]));
                metrics.setTotalNightPlays(Integer.parseInt(row[19]));
                player.setMetrics(metrics);

                // Add Predictions
                Predictions predictions = new Predictions(
                        Double.parseDouble(row[20]), // Churn
                        Double.parseDouble(row[21]), // First Move Win Probability
                        Predictions.PlayerClass.valueOf(row[22].toUpperCase()) // Player Class
                );
                player.setPredictions(predictions);

                players.add(player);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing CSV row: " + String.join(", ", row), e);
            }
        }
        return players;
    }
}
