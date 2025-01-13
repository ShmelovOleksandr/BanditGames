package be.kdg.integration5.checkersachievementcontext.port.out;


import be.kdg.integration5.checkersachievementcontext.domain.Game;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;

public interface FindGamePort {
    Game findById(GameId gameId);
}
