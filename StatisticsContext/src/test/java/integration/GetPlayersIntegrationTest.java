package integration;

import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaEntity;
import be.kdg.integration5.statisticscontext.domain.*;
import be.kdg.integration5.statisticscontext.port.in.GetPlayersUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@ContextConfiguration(classes = {StatisticsContextApplication.class})
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class GetPlayersIntegrationTest extends KeycloakTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetPlayersUseCase getPlayersUseCase;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private Page<Player> playerPage;

    @BeforeEach
    void setUp() {
        // Create mock players
        Player player1 = new Player(
                new PlayerId(UUID.randomUUID()),
                "John Doe",
                25,
                Player.Gender.MALE,
                new Location("USA", "New York"),
                new Metrics(),
                new Predictions(0.85, 0.1, Predictions.PlayerClass.PRO_GAMER)
        );

        Player player2 = new Player(
                new PlayerId(UUID.randomUUID()),
                "Jane Smith",
                30,
                Player.Gender.FEMALE,
                new Location( "UK", "London"),
                new Metrics(),
                new Predictions(0.75, 0.2, Predictions.PlayerClass.FREQUENT_PLAYER)
        );

        // Create a mock Page object
        playerPage = new PageImpl<>(List.of(player1, player2), Pageable.ofSize(10), 2);
    }

    @Test
    void shouldReturnPaginatedPlayersWithPredictions() throws Exception {
        // Arrange
        String adminToken = getAdminBearer();
        Mockito.when(getPlayersUseCase.getPlayers(any(Pageable.class))).thenReturn(playerPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/players")
                        .header("Authorization", adminToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].playerName", is("John Doe")))
                .andExpect(jsonPath("$.content[0].age", is(25)))
                .andExpect(jsonPath("$.content[0].gender", is("MALE")))
                .andExpect(jsonPath("$.content[0].country", is("USA")))
                .andExpect(jsonPath("$.content[0].city", is("New York")))
                .andExpect(jsonPath("$.content[0].churn", is(0.85)))
                .andExpect(jsonPath("$.content[0].firstMoveWinProbability", is(0.1)))
                .andExpect(jsonPath("$.content[0].playerClass", is("PRO_GAMER")))
                .andExpect(jsonPath("$.content[1].playerName", is("Jane Smith")))
                .andExpect(jsonPath("$.content[1].age", is(30)))
                .andExpect(jsonPath("$.content[1].gender", is("FEMALE")))
                .andExpect(jsonPath("$.content[1].country", is("UK")))
                .andExpect(jsonPath("$.content[1].city", is("London")))
                .andExpect(jsonPath("$.content[1].churn", is(0.75)))
                .andExpect(jsonPath("$.content[1].firstMoveWinProbability", is(0.2)))
                .andExpect(jsonPath("$.content[1].playerClass", is("FREQUENT_PLAYER")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void shouldReturnNoContentWhenNoPlayersFound() throws Exception {
        // Arrange
        String adminToken = getAdminBearer();
        Mockito.when(getPlayersUseCase.getPlayers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), Pageable.ofSize(10), 0));

        // Act & Assert
        mockMvc.perform(get("/api/v1/players")
                        .header("Authorization", adminToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}