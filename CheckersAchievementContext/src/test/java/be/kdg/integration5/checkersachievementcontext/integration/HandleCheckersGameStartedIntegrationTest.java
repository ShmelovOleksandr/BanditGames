package be.kdg.integration5.checkersachievementcontext.integration;

import be.kdg.integration5.checkersachievementcontext.CheckersAchievementContextApplication;
import be.kdg.integration5.checkersachievementcontext.core.HandleCheckersGameStartedUseCaseImpl;
import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersGameStartedCommand;
import be.kdg.integration5.checkersachievementcontext.port.out.FindGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistPlayerPort;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@ContextConfiguration(classes = {CheckersAchievementContextApplication.class})
class HandleCheckersGameStartedIntegrationTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private HandleCheckersGameStartedUseCaseImpl useCase;

    @Autowired
    private FindGamePort findGamePort;

    @Autowired
    private PersistPlayerPort persistPlayerPort;

    @Autowired
    private PersistGamePort persistGamePort;

    @org.testcontainers.junit.jupiter.Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void setProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    private PlayerId player1Id;
    private PlayerId player2Id;
    @BeforeEach
    void setupData() {
        // Initialize test data
        player1Id = new PlayerId(UUID.randomUUID());
        player2Id = new PlayerId(UUID.randomUUID());

        Player player1 = new Player(player1Id);
        Player player2 = new Player(player2Id);

        persistPlayerPort.saveAll(List.of(player1, player2));
    }

    @Test
    void testHandleCheckersGameStarted() {
        // Arrange
        GameId gameId = new GameId(UUID.randomUUID());

        HandleCheckersGameStartedCommand command = new HandleCheckersGameStartedCommand(
                gameId,
                List.of(player1Id, player2Id)
        );

        // Act
        useCase.handleCheckersGameStarted(command);

        // Assert
        Game game = findGamePort.findById(gameId);
        assertNotNull(game);
        assertEquals(gameId, game.getGameId());

        List<Player> players = game.getPlayers();
        assertNotNull(players);
        assertEquals(2, players.size());

        for (Player player : players) {
            assertNotNull(player.getPlayerId());
            assertNotNull(player.getAchievements());
        }
    }
}