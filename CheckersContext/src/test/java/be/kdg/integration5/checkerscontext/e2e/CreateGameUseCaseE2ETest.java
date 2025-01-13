package be.kdg.integration5.checkerscontext.e2e;

import be.kdg.integration5.checkerscontext.CheckersContextApplication;
import be.kdg.integration5.checkerscontext.domain.Game;
import be.kdg.integration5.checkerscontext.domain.GameId;
import be.kdg.integration5.checkerscontext.port.out.FindGamePort;
import be.kdg.integration5.common.events.LobbyCreatedEvent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = {CheckersContextApplication.class})
public class CreateGameUseCaseE2ETest {
    private static final String LOBBY_QUEUE = "lobby_queue";
    private static RabbitMQContainer rabbitMQContainer;

    private static RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private FindGamePort findGamePort;

    @Value("${game.name}")
    private String gameName;


    @BeforeAll
    static void startRabbitMQ() {
        // Start RabbitMQ TestContainer
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withEnv("RABBITMQ_DEFAULT_USER", "guest")
                .withEnv("RABBITMQ_DEFAULT_PASS", "guest");
        rabbitMQContainer.start();

        // Configure RabbitTemplate with TestContainer's RabbitMQ
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                rabbitMQContainer.getHost(),
                rabbitMQContainer.getMappedPort(5672)
        );
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // Declare required queue
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(new Queue(LOBBY_QUEUE, true));
    }


    @AfterAll
    static void stopRabbitMQ() {
        rabbitMQContainer.stop();
    }

    @DynamicPropertySource
    static void registerRabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @Test
    @Transactional
    public void testCreateGameFromLobbyListener() {
        // Arrange: Create a mock LobbyCreatedEvent
        UUID lobbyId = UUID.randomUUID();
        UUID firstPlayerId = UUID.randomUUID();
        List<LobbyCreatedEvent.PlayerEvent> playerEvents = List.of(
                new LobbyCreatedEvent.PlayerEvent(UUID.randomUUID(), "Anthony"),
                new LobbyCreatedEvent.PlayerEvent(firstPlayerId, "Nathaniel")
        );

        LobbyCreatedEvent testEvent = new LobbyCreatedEvent(
                gameName,
                lobbyId,
                firstPlayerId,
                playerEvents
        );

        // Publish the test event to the RabbitMQ queue
        rabbitTemplate.convertAndSend(LOBBY_QUEUE, testEvent);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {}
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Game game = findGamePort.findById(new GameId(lobbyId));
            assertThat(game).isNotNull();
            assertThat(game.getPlayers()).hasSize(2);

        });
    }
}
