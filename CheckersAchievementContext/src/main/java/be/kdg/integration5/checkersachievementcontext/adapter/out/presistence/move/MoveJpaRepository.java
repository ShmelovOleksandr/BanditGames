package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MoveJpaRepository extends JpaRepository<MoveJpaEntity, MoveJpaEntityId> {

    @Query("select m from MoveJpaEntity m " +
            "join fetch m.mover " +
            "where m.game.gameId = :id")
    List<MoveJpaEntity> findAllByGameIdFetched(UUID id);
}
