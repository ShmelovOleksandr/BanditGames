package be.kdg.integration5.statisticscontext.adapter.out.persistencte.player;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerJpaRepository extends JpaRepository<PlayerJpaEntity, UUID> {

    @Query("select p from PlayerJpaEntity p " +
    "left join fetch p.playerMetrics m " +
    "left join fetch p.predictions pr " +
    "where p.playerId in :uuids")
    List<PlayerJpaEntity> findAllByIdsFetched(List<UUID> uuids);

    @Query("select p from PlayerJpaEntity p " +
    "left join fetch p.playerMetrics m " +
    "left join fetch p.predictions pr")
    List<PlayerJpaEntity> findAllFetched();

    @Query("select p from PlayerJpaEntity p")
    Page<PlayerJpaEntity> findAllPlayers(Pageable pageable);
}
