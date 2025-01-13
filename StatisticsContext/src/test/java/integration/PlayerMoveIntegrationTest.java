package integration;


import be.kdg.integration5.common.events.PlayerMoveEvent;
import be.kdg.integration5.common.events.StartGameSessionEvent;
import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_session.PlayerSessionJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.session.SessionJpaRepository;
import be.kdg.integration5.statisticscontext.domain.*;
import be.kdg.integration5.statisticscontext.port.in.GameSessionStartedUseCase;
import be.kdg.integration5.statisticscontext.port.in.PlayerMadeMoveUseCase;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class PlayerMoveIntegrationTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GameJpaRepository gameJpaRepository;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private MoveJpaRepository moveJpaRepository;

    @Autowired
    private PlayerSessionJpaRepository playerSessionJpaRepository;

    @Autowired
    private SessionJpaRepository sessionJpaRepository;

    @Autowired
    private PlayerMetricsJpaRepository playerMetricsJpaRepository;

    @Autowired
    private PlayerJpaConverter playerJpaConverter;

    @Autowired
    private PlayerMetricsJpaConverter playerMetricsJpaConverter;

    @Autowired
    private PlayerMadeMoveUseCase playerMadeMoveUseCase;

    @Autowired
    private GameSessionStartedUseCase gameSessionStartedUseCase;

    private List<Player> playerList;

    private Session session;

    @BeforeEach
    void setup() {
        // Seed test data for game and players
        Game chessGame = new Game(new GameId(UUID.randomUUID()), "chess");
        gameJpaRepository.save(new GameJpaEntity(chessGame.gameId().uuid(), chessGame.name()));

        Player player1 = new Player("Player 1", new PlayerId(UUID.randomUUID()), 25, Player.Gender.MALE, new Location("USA", "New York"));
        Player player2 = new Player("Player 2", new PlayerId(UUID.randomUUID()), 30, Player.Gender.FEMALE, new Location("Canada", "Toronto"));

        playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        List<PlayerJpaEntity> playerEntities = playerList.stream().map(playerJpaConverter::toJpa).toList();

        StartGameSessionEvent event = new StartGameSessionEvent(
                chessGame.name(),
                UUID.randomUUID(),
                player1.getPlayerId().uuid(),
                playerList.stream().map(p -> p.getPlayerId().uuid()).toList(),
                LocalDateTime.now()

        );

        List<PlayerMetricsJpaEntity> playerMetricsJpaEntities = playerEntities.stream().map(p -> {
            PlayerMetricsJpaEntity playerMetricsJpaEntity = new PlayerMetricsJpaEntity();
            playerMetricsJpaConverter.updateFields(playerMetricsJpaEntity, new Metrics());
            playerMetricsJpaEntity.setPlayerId(p.getPlayerId());
            playerMetricsJpaEntity.setPlayer(p);
            return playerMetricsJpaEntity;
        }).toList();

        playerJpaRepository.saveAll(playerEntities);
        playerMetricsJpaRepository.saveAll(playerMetricsJpaEntities);

        session = gameSessionStartedUseCase.startGame(event);

    }


    @AfterEach
    void tearDown() {
        session = null;
        playerList = new ArrayList<>();
        moveJpaRepository.deleteAll();
        playerSessionJpaRepository.deleteAll();
        sessionJpaRepository.deleteAll();
        gameJpaRepository.deleteAll();
        playerMetricsJpaRepository.deleteAll();
        playerJpaRepository.deleteAll();
    }

    @Test
    void shouldSavePlayerMoveSuccessfully() {
        // Arrange
        PlayerMoveEvent event = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                playerList.get(0).getPlayerId().uuid(),
                playerList.get(1).getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(1)
        );

        // Act
        playerMadeMoveUseCase.saveMove(event);

        // Assert
        List<MoveJpaEntity> moveEntities = moveJpaRepository.findAll();
        List<PlayerMetricsJpaEntity> playerMetricsEntities = playerMetricsJpaRepository.findAll();

        assertThat(moveEntities, hasSize(2));
        assertThat(moveEntities, hasItem(hasProperty("endTime", nullValue())));
        assertThat(moveEntities, everyItem(hasProperty("moveNumber", equalTo(1))));
        assertThat(playerMetricsEntities, hasSize(2));
    }

    @Test
    void shouldSaveMultipleSuccessfulMoves() {
        // Arrange
        PlayerMoveEvent event1 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                playerList.get(0).getPlayerId().uuid(),
                playerList.get(1).getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(1)
        );

        PlayerMoveEvent event2 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                playerList.get(1).getPlayerId().uuid(),
                playerList.get(0).getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(2)
        );

        PlayerMoveEvent event3 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                playerList.get(0).getPlayerId().uuid(),
                playerList.get(1).getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(3)
        );

        // Act
        playerMadeMoveUseCase.saveMove(event1);
        playerMadeMoveUseCase.saveMove(event2);
        playerMadeMoveUseCase.saveMove(event3);

        // Assert
        List<MoveJpaEntity> moveEntities = moveJpaRepository.findAll();
        assertThat(moveEntities, hasSize(4));
        assertThat(moveEntities, hasItem(hasProperty("endTime", nullValue())));
        // Filter items for moveNumber = 1
        List<MoveJpaEntity> moveNumberOne = moveEntities.stream()
                .filter(move -> move.getMoveNumber() == 1)
                .collect(Collectors.toList());
        assertThat(moveNumberOne, hasSize(2));

        // Filter items for moveNumber = 2
        List<MoveJpaEntity> moveNumberTwo = moveEntities.stream()
                .filter(move -> move.getMoveNumber() == 2)
                .collect(Collectors.toList());
        assertThat(moveNumberTwo, hasSize(2));

        // Filter items with endTime = null
        List<MoveJpaEntity> endTimeNull = moveEntities.stream()
                .filter(move -> move.getEndTime() == null)
                .collect(Collectors.toList());
        assertThat(endTimeNull, hasSize(1));

    }
}
