package be.kdg.integration5.gameplatformcontext.port.out;

import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;

import java.util.List;

public interface FindPlayerPort {
    Player findPlayerById(PlayerId playerId);
    boolean playerExists(PlayerId playerId);
    List<Player> findByUsername(String username);
    List<Player> findFriends(PlayerId playerId);
}
