package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.game;

import be.kdg.integration5.gameplatformcontext.domain.Game;
import be.kdg.integration5.gameplatformcontext.domain.GameId;
import be.kdg.integration5.gameplatformcontext.domain.Price;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class GameJpaConverter {

    public GameJpaEntity toJpa(Game game) {
        return new GameJpaEntity(
                game.getGameId().uuid(),
                game.getTitle(),
                game.getDescription(),
                game.getPrice().amount(),
                game.getPrice().currency().getCurrencyCode(),
                game.getMaxLobbyPlayersAmount(),
                game.getFrontendUrl(),
                game.getBackendUrl(),
                game.getGameImageUrl()
        );
    }







    public Game toDomain(GameJpaEntity gameJpaEntity) {
        return new Game(
                new GameId(gameJpaEntity.getGameId()),
                gameJpaEntity.getTitle(),
                gameJpaEntity.getDescription(),
                new Price(gameJpaEntity.getPriceAmount(), Currency.getInstance(gameJpaEntity.getCurrencyCode())),
                gameJpaEntity.getMaxLobbyPlayerAmount(),
                gameJpaEntity.getFrontendUrl(),
                gameJpaEntity.getBackendUrl(),
                gameJpaEntity.getGameImageUrl()

        );
    }
}
