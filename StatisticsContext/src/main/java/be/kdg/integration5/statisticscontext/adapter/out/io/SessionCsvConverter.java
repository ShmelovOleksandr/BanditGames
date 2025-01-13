package be.kdg.integration5.statisticscontext.adapter.out.io;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.domain.PlayerActivity;
import be.kdg.integration5.statisticscontext.domain.Session;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class SessionCsvConverter {

    public List<String[]> toCsvRows(List<Session> sessions) {
        List<String[]> rows = new ArrayList<>();

        for (Session session : sessions) {
            double sessionDurationSeconds = session.getDurationSeconds();

            for (PlayerActivity activity: session.getActivities()) {
                Player player = activity.getPlayer();

                if (player.getMetrics() == null) continue;
                if (player.getPredictions() == null) continue;

                double avgMoveDurationSeconds = activity.getMoves()
                        .stream()
                        .mapToDouble(move -> Duration.between(move.getStartTime(), move.getEndTime()).toSeconds())
                        .average()
                        .orElse(0.0);

                int moveAmount = activity.getMoves().size();

                boolean isWinner = player.equals(session.getWinner());
                boolean isFirst = player.equals(session.getFirstPlayer());

                rows.add(new String[]{
                        Double.toString(sessionDurationSeconds),
                        session.getGame().gameId().uuid().toString(),
                        String.valueOf(session.isDraw()),
                        Integer.toString(moveAmount),
                        player.getPlayerId().uuid().toString(),
                        session.getSessionId().uuid().toString(),
                        String.valueOf(isWinner),
                        player.getGender().name(),
                        Integer.toString(player.getAge()),
                        player.getLocation().country(),
                        player.getLocation().city(),
                        session.getStartTime().truncatedTo(ChronoUnit.MINUTES).toString(),
                        Double.toString(avgMoveDurationSeconds),
                        String.valueOf(isFirst)

                });
            }
        }

        return rows;
    }
}
