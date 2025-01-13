package be.kdg.integration5.checkersachievementcontext.port.out;


import be.kdg.integration5.checkersachievementcontext.domain.Game;

public interface PersistGamePort {
    Game save(Game game);
//    Game update(Game game);
}
