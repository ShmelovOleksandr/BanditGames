package be.kdg.integration5.gameplatformcontext.port.in;

import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;

import java.util.List;

public interface GetFriendsListUseCase {
    List<Player> getFriends(PlayerId playerId);
}
