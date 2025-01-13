package integration;


import be.kdg.integration5.statisticscontext.StatisticsContextApplication;
import be.kdg.integration5.statisticscontext.port.in.GeneratePlayerDataUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ActiveProfiles("test")
@ContextConfiguration(classes = { StatisticsContextApplication.class })
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class CollectPlayerStatisticsCsvIntegrationTest extends KeycloakTestContainers{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeneratePlayerDataUseCase generatePlayerDataUseCase;

    @Test
    void testExportSessionStatisticsCsv() throws Exception {
        String header = getAdminBearer();

        mockMvc.perform(get("/api/v1/players/csv")
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=")))
                .andExpect(content().string(containsString("PlayerId"))) // Validate headers
                .andExpect(content().string(containsString("TotalGamesPlayed")))
                .andExpect(content().string(containsString("TotalWins")))
                .andExpect(content().string(containsString("TotalWeekdaysPlayed")))
                .andExpect(content().string(containsString("AvgMoveDuration")));

    }

    @Test
    void testExportSessionStatisticsCsvAfterDataCreation() throws Exception {
        // Arrange
        int createdPlayersCount = generatePlayerDataUseCase.generatePlayerDataFromCsv();
        String header = getAdminBearer();

        // Act & assert
        assertThat("No players were created during the data generation", createdPlayersCount, is(not(equalTo(0))));
        mockMvc.perform(get("/api/v1/players/csv")
                        .header("Authorization", header))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=")))
                .andExpect(content().string(containsString("PlayerId"))) // Validate headers
                .andExpect(content().string(containsString("TotalGamesPlayed")))
                .andExpect(content().string(containsString("TotalWins")))
                .andExpect(content().string(containsString("TotalWeekdaysPlayed")))
                .andExpect(content().string(containsString("AvgMoveDuration")));
    }
}
