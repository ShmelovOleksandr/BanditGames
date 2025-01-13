package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.friends;

import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.player.PlayerJpaEntity;
import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "friend_requests")
@Getter
@Setter
public class FriendsRequestJpaEntity {
    @Id
    private UUID requestId;

    @ManyToOne
    private PlayerJpaEntity sender;

    @ManyToOne
    private PlayerJpaEntity receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequest.Status status;

    public FriendRequest toDomain() {
        return new FriendRequest(requestId, sender.toDomain(), receiver.toDomain(), status);
    }

    public static FriendsRequestJpaEntity of(FriendRequest request) {
        FriendsRequestJpaEntity entity = new FriendsRequestJpaEntity();
        entity.setRequestId(request.getRequestUUID());
        entity.setSender(PlayerJpaEntity.of(request.getSender()));
        entity.setReceiver(PlayerJpaEntity.of(request.getReceiver()));
        entity.setStatus(request.getStatus());
        return entity;
    }
}
