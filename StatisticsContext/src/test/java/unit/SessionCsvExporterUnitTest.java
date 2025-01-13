package unit;

import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.io.SessionCsvConverter;
import be.kdg.integration5.statisticscontext.adapter.out.io.SessionCsvExporter;
import be.kdg.integration5.statisticscontext.domain.Game;
import be.kdg.integration5.statisticscontext.domain.GameId;
import be.kdg.integration5.statisticscontext.domain.Session;
import be.kdg.integration5.statisticscontext.domain.SessionId;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class SessionCsvExporterUnitTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SessionCsvConverter sessionCsvConverter;

    @Autowired
    private SessionCsvExporter sessionCsvExporter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionCsvExporter = new SessionCsvExporter(sessionCsvConverter);
    }

    @Test
    void testExportEntities_correctHeadersAndData() throws Exception {
        // Arrange
        Game game = new Game(new GameId(UUID.randomUUID()), "test");
        SessionId sessionId = new SessionId(UUID.randomUUID());


        List<Session> mockSessions = List.of(new Session(sessionId, game));
        List<String[]> mockConvertedSessions = List.of(
                new String[]{"1800.0", "1", "false", "2", "player1", "session1", "true", "MALE", "25", "USA", "New York", "2024-01-01T10:00", "120.0", "true"},
                new String[]{"1800.0", "1", "false", "0", "player2", "session1", "false", "FEMALE", "30", "Canada", "Toronto", "2024-01-01T10:00", "0.0", "false"}
        );
        when(sessionCsvConverter.toCsvRows(mockSessions)).thenReturn(mockConvertedSessions);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        sessionCsvExporter.exportEntities(mockSessions, outputStream);

        // Assert
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

        List<String[]> csvRows = csvReader.readAll();

        String[] expectedHeader = {
                "SessionDuration",
                "GameId",
                "IsDraw",
                "NumberOfMoves",
                "PlayerId",
                "SessionId",
                "IsWinner",
                "Gender",
                "Age",
                "Country",
                "City",
                "PlayedDateTime",
                "AvgMoveTime",
                "IsFirst"
        };
        assertThat(csvRows.get(0), is(expectedHeader));
        assertThat(csvRows, hasSize(3));
        assertThat(csvRows.get(1), is(mockConvertedSessions.get(0)));
        assertThat(csvRows.get(2), is(mockConvertedSessions.get(1)));

        // Verify interactions with the converter
        verify(sessionCsvConverter, times(1)).toCsvRows(mockSessions);
    }

}
