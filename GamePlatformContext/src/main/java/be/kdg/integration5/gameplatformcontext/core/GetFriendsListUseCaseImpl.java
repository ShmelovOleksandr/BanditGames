package be.kdg.integration5.gameplatformcontext.core;

import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import be.kdg.integration5.gameplatformcontext.port.in.GetFriendsListUseCase;
import be.kdg.integration5.gameplatformcontext.port.out.FindPlayerPort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GetFriendsListUseCaseImpl implements GetFriendsListUseCase {
    private final FindPlayerPort findPlayerPort;

    public GetFriendsListUseCaseImpl(FindPlayerPort findPlayerPort) {
        this.findPlayerPort = findPlayerPort;
    }


    @Override
    public List<Player> getFriends(PlayerId playerId) {
        return findPlayerPort.findFriends(playerId)
                .stream()
                .toList();
    }
}
