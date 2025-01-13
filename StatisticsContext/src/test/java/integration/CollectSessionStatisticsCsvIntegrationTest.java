package integration;


import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.containsString;

@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class CollectSessionStatisticsCsvIntegrationTest extends KeycloakTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testExportSessionStatisticsCsv() throws Exception {
        String header = getAdminBearer();
        mockMvc.perform(get("/api/v1/sessions/csv")
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=")))
                .andExpect(content().string(containsString("SessionDuration")));
    }
}
