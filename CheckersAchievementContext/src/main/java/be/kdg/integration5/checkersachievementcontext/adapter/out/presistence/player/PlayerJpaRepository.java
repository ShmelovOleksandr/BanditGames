package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerJpaRepository extends JpaRepository<PlayerJpaEntity, UUID> {

    @Query("select p from PlayerJpaEntity p " +
            "join fetch p.achievements " +
            "where p.playerId = :uuid")
    Optional<PlayerJpaEntity> findByIdFetched(UUID uuid);
}
