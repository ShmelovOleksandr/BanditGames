package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.lobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LobbyJpaRepository extends JpaRepository<LobbyJpaEntity, UUID> {

    @Query("select l from LobbyJpaEntity l " +
    "left join fetch l.game g " +
    "left join fetch l.lobbyPlayers lp " +
    "left join fetch lp.player p " +
            "left join fetch p.friends f " +
    "where g.gameId = :gameId and " +
    "size(lp) < g.maxLobbyPlayerAmount and " +
    "l.isPrivate = :privacy")
    List<LobbyJpaEntity> findAllNotFilledNonPrivateLobbiesByGameIdCustom(UUID gameId, boolean privacy);

    @Query("select l from LobbyJpaEntity l " +
    "left join fetch l.game g " +
    "left join fetch l.lobbyPlayers lp " +
    "left join fetch lp.player p " +
            "left join fetch p.friends f " +
    "where l.lobbyId in ( " +
            "        select lInner.lobbyId from LobbyJpaEntity lInner " +
            "        join lInner.lobbyPlayers lpInner " +
            "        join lpInner.player pInner " +
            "        where pInner.playerId = :playerId " +
            "    ) and l.isReady = :isReady")
    Optional<LobbyJpaEntity>    findByPlayerIdAndIsReadyCustom(UUID playerId, boolean isReady);


    @Query("select l from LobbyJpaEntity l " +
    "left join fetch l.game g " +
    "left join fetch l.lobbyPlayers lp " +
    "left join fetch lp.player p " +
            "left join fetch p.friends f " +
    "where l.lobbyId = :lobbyId")
    Optional <LobbyJpaEntity> findByLobbyIdFetched(UUID lobbyId);


}
