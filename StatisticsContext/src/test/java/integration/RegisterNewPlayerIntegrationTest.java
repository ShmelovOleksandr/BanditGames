package integration;

import be.kdg.integration5.common.events.statistics.NewPlayerRegisteredEvent;
import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaRepository;
import be.kdg.integration5.statisticscontext.domain.Metrics;
import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.domain.Predictions;
import be.kdg.integration5.statisticscontext.port.in.RegisterNewPlayerUseCase;
import org.junit.jupiter.api.AfterEach;
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

import java.util.UUID;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class RegisterNewPlayerIntegrationTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private PlayerMetricsJpaRepository playerMetricsJpaRepository;

    @Autowired
    private PredictionsJpaRepository predictionsJpaRepository;

    @Autowired
    private RegisterNewPlayerUseCase registerNewPlayerUseCase;

    @AfterEach
    void tearDown() {
        playerMetricsJpaRepository.deleteAll();
        predictionsJpaRepository.deleteAll();
        playerJpaRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewPlayer() {
        // Arrange
        NewPlayerRegisteredEvent event = new NewPlayerRegisteredEvent(
                UUID.randomUUID(),
                "test",
                "MALE",
                10,
                "Belgium",
                "Antwerp"
        );

        // Act
        Player player = registerNewPlayerUseCase.register(event);

        // Assert
        assertThat(player.getPlayerId().uuid(), is(event.playerId()));
        assertThat(player.getPlayerName(), is(event.playerName()));
        assertThat(player.getAge(), is(event.age()));
        assertThat(player.getGender(), is(Player.Gender.valueOf(event.gender())));
        assertThat(player.getLocation().country(), is(event.country()));
        assertThat(player.getLocation().city(), is(event.city()));

        Metrics metrics = player.getMetrics();
        assertThat(metrics.getTotalGamesPlayed(), is(0));
        assertThat(metrics.getTotalWins(), is(0));
        assertThat(metrics.getTotalLosses(), is(0));
        assertThat(metrics.getTotalDraws(), is(0));

        Predictions predictions = player.getPredictions();
        assertThat(predictions.getPlayerClass(), is(Predictions.PlayerClass.BEGINNER));

        PlayerJpaEntity savedPlayer = playerJpaRepository.findById(player.getPlayerId().uuid()).orElse(null);
        assertThat(savedPlayer, is(notNullValue()));
        assertThat(savedPlayer.getPlayerName(), is(event.playerName()));
    }

}
