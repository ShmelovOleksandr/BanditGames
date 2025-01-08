package be.kdg.integration5.gameplatformcontext.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Game {
    private GameId gameId;
    private String title;
    private String description;
    private Price price;
    private int maxLobbyPlayersAmount;
    private String frontendUrl;
    private String backendUrl;
    private String gameImageUrl;


    public Game(String title, String gameImageUrl, String backendUrl, String frontendUrl, Price price, int maxLobbyPlayersAmount, String description) {
        this.gameId = new GameId(UUID.randomUUID());
        this.title = title;
        this.gameImageUrl = gameImageUrl;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
        this.price = price;
        this.maxLobbyPlayersAmount = maxLobbyPlayersAmount;
        this.description = description;
    }

    public int getMaxPlayers() {
        return maxLobbyPlayersAmount;
    }
}
