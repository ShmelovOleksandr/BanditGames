package be.kdg.integration5.gameplatformcontext.adapter.in.api.dto;

import be.kdg.integration5.gameplatformcontext.domain.Game;

import java.util.Objects;
import java.util.UUID;

public record GameGetDto(
        UUID gameId,
        String title,
        String description,
        Double priceAmount,
        String currencyCode,
        String backendUrl,
        String gameImageUrl
) {

    public GameGetDto {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(priceAmount);
        Objects.requireNonNull(currencyCode);
    }

    public static GameGetDto of(Game game) {
        return new GameGetDto(
                game.getGameId().uuid(),
                game.getTitle(),
                game.getDescription(),
                game.getPrice().amount(),
                game.getPrice().currency().getCurrencyCode(),
                game.getBackendUrl(),
                game.getGameImageUrl()
        );
    }
}
