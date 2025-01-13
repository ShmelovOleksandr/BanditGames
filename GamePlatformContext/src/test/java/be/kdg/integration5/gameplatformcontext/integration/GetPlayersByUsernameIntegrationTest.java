package be.kdg.integration5.gameplatformcontext.integration;

import be.kdg.integration5.gameplatformcontext.GamePlatformContextApplication;
import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.player.PlayerJpaEntity;
import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.player.PlayerJpaRepository;
import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.port.in.GetPlayersByUserNameUseCase;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@ContextConfiguration(classes = { GamePlatformContextApplication.class })
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
public class GetPlayersByUsernameIntegrationTest extends KeycloakTestContainers{

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private GetPlayersByUserNameUseCase getPlayersByUserNameUseCase;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        playerJpaRepository.save(new PlayerJpaEntity(UUID.randomUUID(), 25, Player.Gender.MALE, "john_doe"));
        playerJpaRepository.save(new PlayerJpaEntity(UUID.randomUUID(), 30, Player.Gender.FEMALE, "jane_doe"));
        playerJpaRepository.save(new PlayerJpaEntity(UUID.randomUUID(), 27, Player.Gender.MALE, "john_smith"));
    }

    @AfterEach
    void tearDown() {
        playerJpaRepository.deleteAll();
    }
    @Test
    void testGetPlayersByUsernameUseCase() {
        // Act
        List<Player> players = getPlayersByUserNameUseCase.getPlayers("john");

        // Assert
        assertThat(players, hasSize(2));
        assertThat(players, hasItems(
                hasProperty("username", is("john_doe")),
                hasProperty("username", is("john_smith"))
        ));
    }

    @Test
    void testGetPlayersByUsernameApi() throws Exception {
        // Arrange
        String adminToken = getAdminBearer();

        // Act and Assert
        mockMvc.perform(get("/api/v1/players")
                        .header("Authorization", adminToken)
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("john_doe", "john_smith")));
    }

    @Test
    void testGetPlayersByUsernameApi_NoResults() throws Exception {
        String adminToken = getAdminBearer();
        // Act and Assert
        mockMvc.perform(get("/api/v1/players")
                        .header("Authorization", adminToken)
                        .param("username", "nonexistent"))
                .andExpect(status().isNoContent());
    }
}
