package be.kdg.integration5.gameplatformcontext.core;

import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import be.kdg.integration5.gameplatformcontext.port.in.FriendsMatchingUseCase;
import be.kdg.integration5.gameplatformcontext.port.out.FindPlayerPort;
import be.kdg.integration5.gameplatformcontext.port.out.FriendsRequestPort;
import be.kdg.integration5.gameplatformcontext.port.out.PersistFriendsRequestPort;
import be.kdg.integration5.gameplatformcontext.port.out.PersistPlayerPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FriendsMatchingUseCaseImpl implements FriendsMatchingUseCase {
    private final FriendsRequestPort friendRequestPort;
    private final FindPlayerPort findPlayerPort;
    private final PersistPlayerPort persistPlayerPort;
    private final PersistFriendsRequestPort persistFriendsRequestPort;

    public FriendsMatchingUseCaseImpl(FriendsRequestPort friendRequestPort, FindPlayerPort findPlayerPort, PersistPlayerPort persistPlayerPort, PersistFriendsRequestPort persistFriendsRequestPort) {
        this.friendRequestPort = friendRequestPort;
        this.findPlayerPort = findPlayerPort;
        this.persistPlayerPort = persistPlayerPort;
        this.persistFriendsRequestPort = persistFriendsRequestPort;
    }


    @Override
    public void sendFriendsRequest(PlayerId senderId, PlayerId receiverId) {
        Player sender = findPlayerPort.findPlayerById(senderId);
        Player receiver = findPlayerPort.findPlayerById(receiverId);

        if (!findPlayerPort.playerExists(receiverId)) {
            throw new IllegalArgumentException("Player could not be found to send friends request");
        }else if (sender.isFriendsWithPlayer(receiver)) {
            throw new IllegalArgumentException("Already friends!");
        }else{
            FriendRequest friendsRequest = new FriendRequest(UUID.randomUUID(), sender, receiver, FriendRequest.Status.PENDING);
            persistFriendsRequestPort.saveFriends(friendsRequest);
        }
    }

    @Override
    public void respondToFriendRequest(UUID requestId, boolean accepted) {
        FriendRequest request = friendRequestPort.findFriendRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Friend request not found with ID: " + requestId);
        }

        if (accepted) {
            request.setStatus(FriendRequest.Status.ACCEPTED);

            // Add friends
            Player sender = request.getSender();
            Player receiver = request.getReceiver();

            if (!sender.getFriends().contains(receiver)) {
                sender.getFriends().add(receiver);
            }
            if (!receiver.getFriends().contains(sender)) {
                receiver.getFriends().add(sender);
            }

            // Persist changes
            persistPlayerPort.save(sender);
            persistPlayerPort.save(receiver);
        } else {
            request.setStatus(FriendRequest.Status.DECLINED);
        }

        persistFriendsRequestPort.saveFriends(request);
    }


    @Override
    public List<FriendRequest> getPendingFriendRequests(PlayerId playerId) {
        List<FriendRequest> requests = friendRequestPort.findPendingRequestsByReceiverFetched(playerId);
        System.out.println("IMPL Fetched Requests: " + requests);
        return requests;
    }
}
