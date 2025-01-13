package be.kdg.integration5.statisticscontext.adapter.out.web.prediction;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.domain.Predictions;
import be.kdg.integration5.statisticscontext.port.out.FetchPredictionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PredictionWebAdapter implements FetchPredictionPort {

    private final WebClient webClient;
    private final PredictionsWebConverter predictionsWebConverter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${predictions.url}")
    private String predictionsUrl;

    public PredictionWebAdapter(WebClient.Builder webClientBuilder, PredictionsWebConverter predictionsWebConverter) {
        this.predictionsWebConverter = predictionsWebConverter;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Map<Player, Predictions> fetchPredictions(List<Player> players) {
        PredictionsRequest predictionsRequest = predictionsWebConverter.toRequest(players);
        String apiUrl = String.format("%s/predict", predictionsUrl);
        PredictionsResponse response = null;
        try {
            response =  webClient.post()
                    .uri(apiUrl)
                    .bodyValue(predictionsRequest)
                    .retrieve()
                    .bodyToMono(PredictionsResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.warn("Predictions API returned an error: {}", e.getMessage());
        } catch (WebClientRequestException e) {
            logger.warn("Predictions API is unreachable: {}", e.getMessage());
        }
        if (response == null) {
            logger.warn("Could not convert predictions response to DTO.");
            return players.stream()
                    .collect(Collectors.toMap(player -> player, Player::getPredictions));
        }

        return predictionsWebConverter.toMap(response, players);
    }
}
