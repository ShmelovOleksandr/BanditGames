package be.kdg.integration5.checkerscontext.integration;

import be.kdg.integration5.checkerscontext.adapter.in.dto.GetMovesRequestDto;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.dto.PossibleMovesForPlayerResponseDto;

import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
public class FindAllPossibleMovesUseCaseIntegrationTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtDecoder jwtDecoder;

    private WebSocketStompClient stompClient;

    private BlockingQueue<PossibleMovesForPlayerResponseDto> receivedMessages;

    private static final String WS_URI = "ws://localhost:8042/ws";
    private static final String SUBSCRIBE_DESTINATION = "/queue/user/";
    private static final String SEND_DESTINATION = "/app/get-moves";

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        receivedMessages = new LinkedBlockingQueue<>();
    }

    @Test
    void testPieceMustHaveNoMovesAvailable() throws Exception {
        int x = 1;
        int y = 0;

        StompSession stompSession = stompClient
                .connect(WS_URI, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        UUID gameId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID playerId = UUID.fromString("7701935d-4efc-4f53-81f1-1010dce8a943");

        stompSession.subscribe(SUBSCRIBE_DESTINATION + playerId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PossibleMovesForPlayerResponseDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessages.add((PossibleMovesForPlayerResponseDto) payload);
            }
        });

        GetMovesRequestDto requestDTO = new GetMovesRequestDto(gameId, x, y);
        stompSession.send(SEND_DESTINATION, requestDTO);

        PossibleMovesForPlayerResponseDto response = receivedMessages.poll(5, TimeUnit.SECONDS);
        assertThat(response).isNotNull();
        assertThat(response.playerId()).isEqualTo(playerId);
        assertThat(response.moves().size()).isEqualTo(0);
    }

    @Test
    void testPieceShouldHaveMovesAvailable() throws Exception {
        int x = 1;
        int y = 6;

        // Connect to WebSocket
        StompSession stompSession = stompClient
                .connect(WS_URI, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        UUID gameId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID playerId = UUID.fromString("7701935d-4efc-4f53-81f1-1010dce8a943");
        // Subscribe to the player's queue
        stompSession.subscribe(SUBSCRIBE_DESTINATION + playerId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PossibleMovesForPlayerResponseDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessages.add((PossibleMovesForPlayerResponseDto) payload);
            }
        });

        // Send a request to the "get-moves" mapping
        GetMovesRequestDto requestDTO = new GetMovesRequestDto(gameId, x, y);
        stompSession.send(SEND_DESTINATION, requestDTO);

        // Wait and verify the response
        PossibleMovesForPlayerResponseDto response = receivedMessages.poll(5, TimeUnit.SECONDS);
    assertThat(response).isNotNull();
        assertThat(response.playerId()).isEqualTo(playerId);
        assertThat(response.moves().size()).isEqualTo(2);
    }
}
