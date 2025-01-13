package integration;

import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaRepository;
import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.port.in.GeneratePlayerDataUseCase;
import be.kdg.integration5.statisticscontext.port.out.FindPlayerPort;
import be.kdg.integration5.statisticscontext.port.out.GeneratePlayerPort;
import be.kdg.integration5.statisticscontext.port.out.PersistPlayerPort;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class GeneratePlayerDataIntegrationTest {

    @Autowired
    private GeneratePlayerDataUseCase generatePlayerDataUseCase;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private PlayerMetricsJpaRepository playerMetricsJpaRepository;

    @Autowired
    private PredictionsJpaRepository predictionsJpaRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private FindPlayerPort findPlayerPort;

    @AfterEach
    void tearDown() throws Exception {
        predictionsJpaRepository.deleteAll();
        playerMetricsJpaRepository.deleteAll();
        playerJpaRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldReadPlayersFromCsvAndPersistThem() {
        // Act
        int savedPlayersCount = generatePlayerDataUseCase.generatePlayerDataFromCsv();

        // Assert
        List<Player> savedPlayers = findPlayerPort.findAllFetched(); // Ensure your PersistPlayerPort has this method
        assertThat("Saved player count should match the number of players read from the CSV",
                savedPlayersCount, is(savedPlayers.size()));

        assertThat("Saved players list should not be empty", savedPlayers, is(not(empty())));
        assertThat("All saved players should have non-null player IDs",
                savedPlayers, everyItem(hasProperty("playerId", notNullValue())));

    }
}
