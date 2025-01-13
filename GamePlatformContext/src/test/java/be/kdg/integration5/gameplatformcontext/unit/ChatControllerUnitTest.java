package be.kdg.integration5.gameplatformcontext.unit;
import be.kdg.integration5.gameplatformcontext.GamePlatformContextApplication;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ContextConfiguration(classes = { GamePlatformContextApplication.class})
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
public class ChatControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @WithMockUser
    void testChatbotQueryReturnsBadGatewayWhenServiceFails() throws Exception {
        // Act & Assert: Send a POST request and expect 502 status
        mockMvc.perform(post("/api/v1/chat/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": \"Hello chatbot!\"}")) // JSON payload
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502)) // Check HTTP status
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Chatbot is unreachable"))) // Check if detail contains text
                .andExpect(jsonPath("$.title").value("Bad Gateway")); // Match the title
    }
}
