package be.kdg.integration5.checkersachievementcontext.integration;

import be.kdg.integration5.checkersachievementcontext.CheckersAchievementContextApplication;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game.GameDatabaseAdapter;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game.GameJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game.GameJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaRepository;
import be.kdg.integration5.checkersachievementcontext.domain.*;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.Achievement;
import be.kdg.integration5.checkersachievementcontext.domain.achievement.AchievementsProvider;
import org.junit.jupiter.api.AfterEach;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@ContextConfiguration(classes = {CheckersAchievementContextApplication.class})
class GameDatabaseAdapterTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private GameDatabaseAdapter gameDatabaseAdapter;

    @Autowired
    private GameJpaRepository gameJpaRepository;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private MoveJpaRepository moveJpaRepository;

    @Autowired
    private AchievementJpaRepository achievementJpaRepository;

    @DynamicPropertySource
    static void configureDataSourceProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    private Game game;

    @BeforeEach
    void setUp() {
        Set<Achievement> allAchievements = AchievementsProvider.ACHIEVEMENTS_SET;
        Player player1 = new Player(new PlayerId(UUID.randomUUID()), allAchievements);
        Player player2 = new Player(new PlayerId(UUID.randomUUID()), allAchievements);
        ArrayList<Move> movesHistory = new ArrayList<>(List.of(
                new Move(player1, new PiecePosition(0, 1), new PiecePosition(1, 2), LocalDateTime.now()),
                new Move(player2, new PiecePosition(5, 4), new PiecePosition(4, 5), LocalDateTime.now().plusMinutes(1))
        ));
        Board board = new Board(movesHistory);
        GameId gameId = new GameId(UUID.randomUUID());
        game = new Game(gameId, List.of(player1, player2), false, board);
    }

    @Test
    void save() {
        playerJpaRepository.saveAll(game.getPlayers().stream().map(player -> new PlayerJpaEntity(player.getPlayerId().uuid())).toList());

        gameDatabaseAdapter.save(game);

        GameJpaEntity fetchedGameJpaEntity = gameJpaRepository.findById(game.getGameId().uuid()).orElseThrow();
        List<MoveJpaEntity> movesInDB = moveJpaRepository.findAll();
        List<PlayerJpaEntity> playersInDB = playerJpaRepository.findAll();

        assertNotNull(fetchedGameJpaEntity);

        assertNotNull(movesInDB);
        assertEquals(2, movesInDB.size());

        assertNotNull(playersInDB);
        assertEquals(2, playersInDB.size());
    }

    @Test
    void findById() {
        playerJpaRepository.saveAll(game.getPlayers().stream().map(player -> new PlayerJpaEntity(player.getPlayerId().uuid())).toList());
        gameDatabaseAdapter.save(game);

        Game fetchedGame = gameDatabaseAdapter.findById(game.getGameId());

        List<Move> fetchedMoves = fetchedGame.getBoard().movesHistory();
        List<Player> fetchedPlayers = fetchedGame.getPlayers();

        assertNotNull(fetchedGame);

        assertNotNull(fetchedMoves);
        assertEquals(2, fetchedMoves.size());

        assertNotNull(fetchedPlayers);
        assertEquals(2, fetchedPlayers.size());
    }

    @AfterEach
    void tearDown() {
        moveJpaRepository.deleteAll();
        achievementJpaRepository.deleteAll();
        gameJpaRepository.deleteAll();
        playerJpaRepository.deleteAll();
    }
}