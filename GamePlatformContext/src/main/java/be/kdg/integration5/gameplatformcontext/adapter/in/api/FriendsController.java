package be.kdg.integration5.gameplatformcontext.adapter.in.api;

import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import be.kdg.integration5.gameplatformcontext.port.in.FriendsMatchingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendsController {
    private final FriendsMatchingUseCase friendsMatchingUseCase;

    public FriendsController(FriendsMatchingUseCase friendsMatchingUseCase) {
        this.friendsMatchingUseCase = friendsMatchingUseCase;
    }

    @PostMapping("/send/{senderId}/{receiverId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable UUID senderId, @PathVariable UUID receiverId) {
        System.out.println("Sender: " + senderId + ", Receiver: " + receiverId);
        friendsMatchingUseCase.sendFriendsRequest(new PlayerId(senderId), new PlayerId(receiverId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/respond/{requestId}/{accepted}")
    public ResponseEntity<?> respondToFriendRequest(@PathVariable UUID requestId, @PathVariable boolean accepted) {
        friendsMatchingUseCase.respondToFriendRequest(requestId, accepted);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending/{playerId}")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(@PathVariable UUID playerId) {
        System.out.println("Fetching pending requests for: " + playerId);
        List<FriendRequest> requests = friendsMatchingUseCase.getPendingFriendRequests(new PlayerId(playerId));
        System.out.println("Pending Requests: " + requests);
        return ResponseEntity.ok(requests);
    }

}
