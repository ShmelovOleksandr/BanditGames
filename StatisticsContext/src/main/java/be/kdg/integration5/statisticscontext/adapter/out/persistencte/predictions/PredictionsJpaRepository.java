package be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PredictionsJpaRepository extends JpaRepository<PredictionsJpaEntity, UUID> {
    @Query("select pr from PredictionsJpaEntity pr where pr.playerId in :playerIds")
    List<PredictionsJpaEntity> findPredictionsByPlayerIds(List<UUID> playerIds);
}
