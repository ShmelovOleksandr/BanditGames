package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.friends;

import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.player.PlayerJpaEntity;
import be.kdg.integration5.gameplatformcontext.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendsRequestJpaRepository extends JpaRepository<FriendsRequestJpaEntity, UUID> {
    @Query("select fr " +
            "from FriendsRequestJpaEntity fr " +
            "left join fetch fr.receiver r " +
            "left join fetch r.friends " +
            "left join fetch fr.sender s " +
            "left join fetch s.friends " +
            "where r.playerId = :receiverId " +
            "and fr.status = :status")
    List<FriendsRequestJpaEntity> findByReceiver_PlayerIdAndStatus(UUID receiverId, FriendRequest.Status status);
    @Query("select fr from FriendsRequestJpaEntity fr join fetch fr.receiver r join fetch r.friends join fetch fr.sender s join fetch s.friends where fr.receiver.playerId = :receiverId")
    FriendsRequestJpaEntity findByReceiver_PlayerId(UUID receiverId);

    @Query("select fr from FriendsRequestJpaEntity fr left join fetch fr.receiver r left join fetch r.friends left join fetch fr.sender s left join fetch s.friends where fr.requestId = :requestId")
    Optional<FriendsRequestJpaEntity> findByIdFetched(UUID requestId);
}
