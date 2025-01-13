package be.kdg.integration5.checkersachievementcontext.integration;

import be.kdg.integration5.checkersachievementcontext.CheckersAchievementContextApplication;
import be.kdg.integration5.checkersachievementcontext.core.HandleCheckersMoveMadeUseCaseImpl;
import be.kdg.integration5.checkersachievementcontext.domain.*;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.AchievementsProvider;
import be.kdg.integration5.checkersachievementcontext.port.in.HandleCheckersMoveMadeCommand;
import be.kdg.integration5.checkersachievementcontext.port.out.FindGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.FindPlayerPort;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@ContextConfiguration(classes = {CheckersAchievementContextApplication.class})
class HandleCheckersMoveMadeIntegrationTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private HandleCheckersMoveMadeUseCaseImpl useCase;

    @Autowired
    private FindGamePort findGamePort;

    @Autowired
    private PersistGamePort persistGamePort;

    @Autowired
    private FindPlayerPort findPlayerPort;

    @Autowired
    private PersistPlayerPort persistPlayerPort;

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

    GameId gameId;

    @BeforeEach
    void setupData() {
        // Initialize test data
        gameId = new GameId(UUID.randomUUID());
        PlayerId player1Id = new PlayerId(UUID.randomUUID());
        PlayerId player2Id = new PlayerId(UUID.randomUUID());

        List<Player> players = List.of(
                new Player(player1Id, AchievementsProvider.ACHIEVEMENTS_SET),
                new Player(player2Id, AchievementsProvider.ACHIEVEMENTS_SET)
        );
        Game game = new Game(gameId, players);

        persistPlayerPort.saveAll(players);
        persistGamePort.save(game);
    }

    @Test
    void testHandleCheckersMoveMade() {
        // Arrange
        Game game = findGamePort.findById(gameId);
        Player mover = game.getPlayers().get(0);
        PiecePosition oldPosition = new PiecePosition(0, 1);
        PiecePosition newPosition = new PiecePosition(1, 2);

        HandleCheckersMoveMadeCommand command = new HandleCheckersMoveMadeCommand(
                game.getGameId(),
                mover.getPlayerId(),
                oldPosition,
                newPosition,
                LocalDateTime.now()
        );

        // Act
        useCase.handleCheckersMoveMade(command);

        // Assert
        Game updatedGame = findGamePort.findById(game.getGameId());
        assertNotNull(updatedGame);
        assertNotNull(updatedGame.getBoard());
        assertEquals(1, updatedGame.getBoard().movesHistory().size());

        Move lastMove = updatedGame.getBoard().movesHistory().get(0);
        assertEquals(oldPosition, lastMove.oldPosition());
        assertEquals(newPosition, lastMove.newPosition());
        assertEquals(mover.getPlayerId(), lastMove.mover().getPlayerId());
    }
}
