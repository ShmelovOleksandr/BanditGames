package be.kdg.integration5.statisticscontext.adapter.out.persistencte.player;

import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.predictions.PredictionsJpaRepository;
import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.domain.PlayerId;
import be.kdg.integration5.statisticscontext.domain.Predictions;
import be.kdg.integration5.statisticscontext.port.out.FindPlayerPort;
import be.kdg.integration5.statisticscontext.port.out.PersistPlayerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PlayerDatabaseAdapter implements FindPlayerPort, PersistPlayerPort {

    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerJpaConverter playerJpaConverter;
    private final PlayerMetricsJpaConverter playerMetricsJpaConverter;
    private final PredictionsJpaConverter predictionsJpaConverter;
    private final PlayerMetricsJpaRepository playerMetricsJpaRepository;
    private final PredictionsJpaRepository predictionsJpaRepository;

    public PlayerDatabaseAdapter(PlayerJpaRepository playerJpaRepository, PlayerJpaConverter playerJpaConverter, PlayerMetricsJpaConverter playerMetricsJpaConverter, PredictionsJpaConverter predictionsJpaConverter, PlayerMetricsJpaRepository playerMetricsJpaRepository, PredictionsJpaRepository predictionsJpaRepository) {
        this.playerJpaRepository = playerJpaRepository;
        this.playerJpaConverter = playerJpaConverter;
        this.playerMetricsJpaConverter = playerMetricsJpaConverter;
        this.predictionsJpaConverter = predictionsJpaConverter;
        this.playerMetricsJpaRepository = playerMetricsJpaRepository;
        this.predictionsJpaRepository = predictionsJpaRepository;
    }

    @Override
    public List<Player> findPlayersByIds(List<PlayerId> playerIds) {
        List<UUID> playerUuids = playerIds.stream().map(PlayerId::uuid).toList();
        List<PlayerJpaEntity> playerJpaEntities = playerJpaRepository.findAllByIdsFetched(playerUuids);

        return playerJpaEntities.stream().map(playerJpaConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Player> findAllFetched() {
        return playerJpaRepository.findAllFetched()
                .stream()
                .map(playerJpaConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Player> findAllFetched(Pageable pageable) {
        Page<PlayerJpaEntity> paginatedEntities = playerJpaRepository.findAllPlayers(pageable);
        List<PlayerJpaEntity> playerJpaEntities = paginatedEntities.getContent();
        List<UUID> playerIds = playerJpaEntities.stream().map(PlayerJpaEntity::getPlayerId).toList();

        List<PredictionsJpaEntity> predictions = predictionsJpaRepository.findPredictionsByPlayerIds(playerIds);
        Map<UUID, PredictionsJpaEntity> predictionsMap = predictions.stream()
                .collect(Collectors.toMap(PredictionsJpaEntity::getPlayerId, Function.identity()));

        List<Player> players = playerJpaEntities.stream()
                .map(player -> {
                    player.setPredictions(predictionsMap.get(player.getPlayerId()));
                    return playerJpaConverter.toDomain(player);
                }).collect(Collectors.toList());

        return new PageImpl<>(players, pageable, paginatedEntities.getTotalElements());
    }

    @Override
    public Player save(Player player) {
        PlayerJpaEntity playerJpaEntity = playerJpaConverter.toJpa(player);
        PlayerJpaEntity savedPlayerJpaEntity = playerJpaRepository.save(playerJpaEntity);

        PlayerMetricsJpaEntity metricsJpaEntity = playerMetricsJpaConverter.toJpa(player.getMetrics());
        metricsJpaEntity.setPlayerId(savedPlayerJpaEntity.getPlayerId());
        metricsJpaEntity.setPlayer(savedPlayerJpaEntity);
        PlayerMetricsJpaEntity savedMetrics = playerMetricsJpaRepository.save(metricsJpaEntity);

        PredictionsJpaEntity predictionsJpaEntity = predictionsJpaConverter.toJpa(player.getPredictions());
        predictionsJpaEntity.setPlayerId(savedPlayerJpaEntity.getPlayerId());
        predictionsJpaEntity.setPlayer(savedPlayerJpaEntity);
        PredictionsJpaEntity savedPredictions = predictionsJpaRepository.save(predictionsJpaEntity);

        savedPlayerJpaEntity.setPlayerMetrics(savedMetrics);
        savedPlayerJpaEntity.setPredictions(savedPredictions);

        return playerJpaConverter.toDomain(savedPlayerJpaEntity);
    }

    @Override
    public List<Player> saveAll(List<Player> players) {
        List<PlayerJpaEntity> savedPlayerJpaEntities = playerJpaRepository.saveAll(
                players.stream().map(playerJpaConverter::toJpa).collect(Collectors.toList())
        );
        Map<UUID, PlayerJpaEntity> playerEntityMap = savedPlayerJpaEntities.stream()
                .collect(Collectors.toMap(PlayerJpaEntity::getPlayerId, entity -> entity));

        List<PlayerMetricsJpaEntity> metricsJpaEntities = players.stream()
                .map(player -> {
                    PlayerJpaEntity savedPlayerJpaEntity = playerEntityMap.get(player.getPlayerId().uuid());
                    PlayerMetricsJpaEntity metricsJpaEntity = playerMetricsJpaConverter.toJpa(player.getMetrics());
                    metricsJpaEntity.setPlayerId(savedPlayerJpaEntity.getPlayerId());
                    metricsJpaEntity.setPlayer(savedPlayerJpaEntity);
                    return metricsJpaEntity;
                })
                .collect(Collectors.toList());

        List<PredictionsJpaEntity> predictionsJpaEntities = players.stream()
                .map(player -> {
                    PlayerJpaEntity savedPlayerJpaEntity = playerEntityMap.get(player.getPlayerId().uuid());
                    PredictionsJpaEntity predictionsJpaEntity = predictionsJpaConverter.toJpa(player.getPredictions());
                    predictionsJpaEntity.setPlayerId(savedPlayerJpaEntity.getPlayerId());
                    predictionsJpaEntity.setPlayer(savedPlayerJpaEntity);
                    return predictionsJpaEntity;
                })
                .collect(Collectors.toList());

        playerMetricsJpaRepository.saveAll(metricsJpaEntities);
        predictionsJpaRepository.saveAll(predictionsJpaEntities);

        return savedPlayerJpaEntities.stream().map(playerJpaConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public void updateAll(List<Player> players) {
        List<UUID> playerUuids = players.stream().map(p -> p.getPlayerId().uuid()).toList();
        List<PlayerJpaEntity> playerJpaEntities = playerJpaRepository.findAllByIdsFetched(playerUuids);
        Map<UUID, Player> playerMap = players.stream()
                .collect(Collectors.toMap(p -> p.getPlayerId().uuid(), p -> p));

        playerJpaEntities.forEach(playerJpaEntity -> {
            Player domainPlayer = playerMap.get(playerJpaEntity.getPlayerId());
            Predictions domainPredictions = domainPlayer.getPredictions();
            PredictionsJpaEntity predictionsJpaEntity = playerJpaEntity.getPredictions();

            predictionsJpaEntity.setChurn(domainPredictions.getChurn());
            predictionsJpaEntity.setFirstMoveWinProbability(domainPredictions.getFirstMoveWinProbability());
            predictionsJpaEntity.setPlayerClass(domainPredictions.getPlayerClass());
        });

        playerJpaRepository.saveAll(playerJpaEntities);
    }
}
