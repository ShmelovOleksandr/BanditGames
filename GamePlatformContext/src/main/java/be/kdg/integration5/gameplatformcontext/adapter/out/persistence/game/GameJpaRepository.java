package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameJpaRepository extends JpaRepository<GameJpaEntity, UUID> {
    Optional<GameJpaEntity> findByTitle(String title);
}
