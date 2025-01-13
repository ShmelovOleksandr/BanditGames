package be.kdg.integration5.gameplatformcontext.core;

import be.kdg.integration5.gameplatformcontext.domain.*;
import be.kdg.integration5.gameplatformcontext.port.in.LeaveTheLobbyUseCase;
import be.kdg.integration5.gameplatformcontext.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class LeaveTheLobbyUseCaseIntegrationTest {

    @Autowired
    private LeaveTheLobbyUseCase leaveTheLobbyUseCase;

    @Autowired
    private PersistLobbyPort persistLobbyPort;

    @Autowired
    private FindGamePort findGamePort;

    @Autowired
    private FindLobbyPort findLobbyPort;

    @Autowired
    private FindPlayerPort findPlayerPort;

    @Container
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

    private PlayerId playerId;
    private Lobby testLobby;

    @BeforeEach
    @Transactional
    void setup() {
        playerId = new PlayerId(UUID.fromString("f7c56b6e-d7dc-4bde-bdf9-e9b0ecec8bf5"));
        Player player = findPlayerPort.findPlayerById(playerId);
        Player player2 = new Player(new PlayerId(UUID.fromString("6a1d9b5a-e2e2-4060-9025-3175acd06e3c")), "Alice", 16, Player.Gender.FEMALE);

        Game game = findGamePort.findGameById(new GameId(UUID.fromString("203028d1-4ce6-426e-9b4b-de9ec03f12be")));
        testLobby = new Lobby(new LobbyId(UUID.randomUUID()), false, game, player, List.of(player, player2), false);
        testLobby.addPlayer(player);

        persistLobbyPort.save(testLobby);
    }

    @Test
    @Transactional
    void testRemovePlayerFromLobby() {
        // Act
        Lobby updatedLobby = leaveTheLobbyUseCase.removePlayerFromLobby(playerId);

        // Assert
        assertNotNull(updatedLobby);
        assertFalse(updatedLobby.getPlayers().stream().anyMatch(player -> player.getPlayerId().equals(playerId)), "Player should be removed from the lobby");

        if (updatedLobby.isEmpty()) {
            assertThrows(RuntimeException.class, () -> findLobbyPort.findLobbyById(updatedLobby.getLobbyId()), "Lobby should be deleted if empty");
        } else {
            Lobby persistedLobby = findLobbyPort.findLobbyById(updatedLobby.getLobbyId());
            assertNotNull(persistedLobby, "Lobby should still exist if not empty");
            assertNotEquals(0, persistedLobby.getPlayers().size(), "Lobby should have some players");
        }
    }
}
