package be.kdg.integration5.gameplatformcontext.port.out;

import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;

public interface PersistFriendsRequestPort {
    void saveFriends(FriendRequest friendRequest);
}
