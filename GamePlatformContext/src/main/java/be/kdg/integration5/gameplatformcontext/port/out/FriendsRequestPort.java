package be.kdg.integration5.gameplatformcontext.port.out;

import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;

import java.util.List;
import java.util.UUID;

public interface FriendsRequestPort {
    FriendRequest findFriendRequestById(UUID requestId);
    List<FriendRequest> findPendingRequestsByReceiverFetched(PlayerId receiverId);
}
