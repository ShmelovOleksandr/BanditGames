package integration;

import be.kdg.integration5.common.events.FinishGameSessionEvent;
import be.kdg.integration5.common.events.PlayerMoveEvent;
import be.kdg.integration5.common.events.StartGameSessionEvent;
import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_session.PlayerSessionJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.session.SessionJpaRepository;
import be.kdg.integration5.statisticscontext.domain.*;
import be.kdg.integration5.statisticscontext.port.in.GameSessionFinishedUseCase;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class SessionEndIntegrationTest {

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
    private PredictionsJpaRepository predictionsJpaRepository;

    @Autowired
    private PlayerJpaConverter playerJpaConverter;

    @Autowired
    private PlayerMetricsJpaConverter playerMetricsJpaConverter;

    @Autowired
    private PlayerMadeMoveUseCase playerMadeMoveUseCase;

    @Autowired
    private GameSessionStartedUseCase gameSessionStartedUseCase;

    @Autowired
    private GameSessionFinishedUseCase gameSessionFinishedUseCase;

    private List<Player> playerList;

    private Session session;

    @BeforeEach
    void setup() {
        // Seed test data for game and players
        Game chessGame = new Game(new GameId(UUID.randomUUID()), "game");
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

        List<PredictionsJpaEntity> predictionsJpaEntities = playerEntities.stream().map(playerJpaEntity -> {
            PredictionsJpaEntity predictionsJpaEntity = new PredictionsJpaEntity(
                    0,
                    Predictions.PlayerClass.BEGINNER,
                    0
            );
            predictionsJpaEntity.setPlayerId(playerJpaEntity.getPlayerId());
            predictionsJpaEntity.setPlayer(playerJpaEntity);
            return predictionsJpaEntity;
        }).toList();

        playerJpaRepository.saveAll(playerEntities);
        playerMetricsJpaRepository.saveAll(playerMetricsJpaEntities);
        predictionsJpaRepository.saveAll(predictionsJpaEntities);

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
        predictionsJpaRepository.deleteAll();
        playerJpaRepository.deleteAll();
    }


    @Test
    void shouldUpdateSessionValuesAndPlayerMetrics() {
        // Arrange
        LocalDateTime finishTime = LocalDateTime.now().plusMinutes(4);
        Player winner = playerList.get(0);
        Player loser = playerList.get(1);

        PlayerMoveEvent event1 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                winner.getPlayerId().uuid(),
                loser.getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(1)
        );

        PlayerMoveEvent event2 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                loser.getPlayerId().uuid(),
                winner.getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(2)
        );

        PlayerMoveEvent event3 = new PlayerMoveEvent(
                session.getSessionId().uuid(),
                winner.getPlayerId().uuid(),
                loser.getPlayerId().uuid(),
                LocalDateTime.now().plusMinutes(3)
        );

        FinishGameSessionEvent gameSessionFinishedEvent = new FinishGameSessionEvent(
                session.getSessionId().uuid(),
                winner.getPlayerId().uuid(),
                finishTime,
                false
        );

        // Act
        playerMadeMoveUseCase.saveMove(event1);
        playerMadeMoveUseCase.saveMove(event2);
        playerMadeMoveUseCase.saveMove(event3);

        Session finishedSession = gameSessionFinishedUseCase.finishGameSession(gameSessionFinishedEvent);

        // Assert
        assertThat(finishedSession.getFinishTime(), equalTo(finishTime));
        assertThat(finishedSession.isDraw(), equalTo(false));
        assertThat(finishedSession.getWinner(), equalTo(winner));

        Metrics winnerMetrics = finishedSession.getWinner().getMetrics();

        assertThat(winnerMetrics.getTotalGamesPlayed(), equalTo(1));
        assertThat(winnerMetrics.getTotalWins(), equalTo(1));
        assertThat(winnerMetrics.getTotalLosses(), equalTo(0));
        assertThat(winnerMetrics.getTotalDraws(), equalTo(0));
        assertThat(winnerMetrics.getTotalIsFirst(), equalTo(1)); // Assuming player 0 started the game
        assertThat(winnerMetrics.getAvgMoveDuration(), equalTo(60.0));
        assertThat(winnerMetrics.getAvgMoveAmount(), equalTo(2.0));
        assertThat(winnerMetrics.getAvgGameDuration(), equalTo(240.0));

        DayOfWeek sessionDay = finishTime.getDayOfWeek();
        if (sessionDay.getValue() <= 5) {
            assertThat(winnerMetrics.getTotalWeekdaysPlayed(), equalTo(1));
            assertThat(winnerMetrics.getTotalWeekendsPlayed(), equalTo(0));
        } else {
            assertThat(winnerMetrics.getTotalWeekdaysPlayed(), equalTo(0));
            assertThat(winnerMetrics.getTotalWeekendsPlayed(), equalTo(1));
        }

        int sessionHour = finishTime.getHour();
        if (sessionHour >= 6 && sessionHour < 12) {
            assertThat(winnerMetrics.getTotalMorningPlays(), equalTo(1));
            assertThat(winnerMetrics.getTotalAfternoonPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalEveningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalNightPlays(), equalTo(0));
        } else if (sessionHour >= 12 && sessionHour < 18) {
            assertThat(winnerMetrics.getTotalMorningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalAfternoonPlays(), equalTo(1));
            assertThat(winnerMetrics.getTotalEveningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalNightPlays(), equalTo(0));
        } else if (sessionHour >= 18 && sessionHour < 24) {
            assertThat(winnerMetrics.getTotalMorningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalAfternoonPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalEveningPlays(), equalTo(1));
            assertThat(winnerMetrics.getTotalNightPlays(), equalTo(0));
        } else {
            assertThat(winnerMetrics.getTotalMorningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalAfternoonPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalEveningPlays(), equalTo(0));
            assertThat(winnerMetrics.getTotalNightPlays(), equalTo(1));
        }
    }

}
