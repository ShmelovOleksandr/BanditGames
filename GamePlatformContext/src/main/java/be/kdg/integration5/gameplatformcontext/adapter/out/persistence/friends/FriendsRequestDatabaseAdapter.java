package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.friends;

import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import be.kdg.integration5.gameplatformcontext.port.out.FriendsRequestPort;
import be.kdg.integration5.gameplatformcontext.port.out.PersistFriendsRequestPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FriendsRequestDatabaseAdapter implements FriendsRequestPort, PersistFriendsRequestPort {
    private final FriendsRequestJpaRepository repository;

    public FriendsRequestDatabaseAdapter(FriendsRequestJpaRepository repository) {
        this.repository = repository;
    }


    @Override
    public void saveFriends(FriendRequest friendRequest) {
        repository.save(FriendsRequestJpaEntity.of(friendRequest));
    }

    @Override
    public FriendRequest findFriendRequestById(UUID requestId) {
        return repository.findByIdFetched(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found")).toDomain();
    }

    @Override
    public List<FriendRequest> findPendingRequestsByReceiverFetched(PlayerId receiverId) {
        return repository.findByReceiver_PlayerIdAndStatus(receiverId.uuid(), FriendRequest.Status.PENDING)
                .stream()
                .map(FriendsRequestJpaEntity::toDomain)
                .toList();
    }
}
