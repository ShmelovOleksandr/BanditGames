package unit;

import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.io.SessionCsvConverter;
import be.kdg.integration5.statisticscontext.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class SessionCsvConverterUnitTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SessionCsvConverter sessionCsvConverter;

    @Test
    void testToCsv_Rows_convertsSessionsCorrectly() {
        // Arrange
        Player player1 = new Player(
                new PlayerId(UUID.randomUUID()),
                "John Doe",
                25,
                Player.Gender.MALE,
                new Location("USA", "New York"),
                new Metrics(),
                new Predictions()
        );
        Player player2 = new Player(
                new PlayerId(UUID.randomUUID()),
                "Jane Smith",
                30,
                Player.Gender.FEMALE,
                new Location("Canada", "Toronto"),
                new Metrics(),
                new Predictions()
        );

        Move move1 = new Move(LocalDateTime.of(2024, 1, 1, 10, 0), 1);
        move1.setEndTime(LocalDateTime.of(2024, 1, 1, 10, 2));

        Move move2 = new Move(LocalDateTime.of(2024, 1, 1, 10, 3), 2);
        move2.setEndTime(LocalDateTime.of(2024, 1, 1, 10, 5));

        PlayerActivity activity1 = new PlayerActivity(player1, List.of(move1, move2));
        PlayerActivity activity2 = new PlayerActivity(player2, List.of());

        Game game = new Game(new GameId(UUID.randomUUID()), "test");

        Session session = new Session(
                new SessionId(UUID.randomUUID()),
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 10, 30),
                false,
                List.of(activity1, activity2),
                game,
                player1
        );

        List<Session> sessions = List.of(session);

        // Act
        List<String[]> csvRows = sessionCsvConverter.toCsvRows(sessions);

        // Assert
        assertThat(csvRows, hasSize(2));

        // Row 1
        assertThat(csvRows.get(0), arrayContaining(
                "1800.0",
                game.gameId().uuid().toString(),
                "false",
                "2",
                player1.getPlayerId().uuid().toString(),
                session.getSessionId().uuid().toString(),
                "true",
                "MALE",
                "25",
                "USA",
                "New York",
                "2024-01-01T10:00",
                "120.0",
                "true"
        ));

        // Row 2
        assertThat(csvRows.get(1), arrayContaining(
                "1800.0",
                game.gameId().uuid().toString(),
                "false",
                "0",
                player2.getPlayerId().uuid().toString(),
                session.getSessionId().uuid().toString(),
                "false",
                "FEMALE",
                "30",
                "Canada",
                "Toronto",
                "2024-01-01T10:00",
                "0.0",
                "false"
        ));
    }
}
