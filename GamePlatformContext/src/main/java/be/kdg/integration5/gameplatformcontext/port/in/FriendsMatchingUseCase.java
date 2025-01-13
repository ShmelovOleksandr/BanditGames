package be.kdg.integration5.gameplatformcontext.port.in;


import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;

import java.util.List;
import java.util.UUID;

public interface FriendsMatchingUseCase {
    void sendFriendsRequest (PlayerId sender, PlayerId receiver);
    void respondToFriendRequest(UUID requestId, boolean accepted);
    List<FriendRequest> getPendingFriendRequests(PlayerId playerId);
}
