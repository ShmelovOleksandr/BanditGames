package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.exception.PlayerNotFoundException;
import be.kdg.integration5.checkersachievementcontext.domain.Player;
import be.kdg.integration5.checkersachievementcontext.domain.PlayerId;
import be.kdg.integration5.checkersachievementcontext.port.out.FindPlayerPort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistPlayerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PlayerDatabaseAdapter implements FindPlayerPort, PersistPlayerPort {
    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerJpaConverter playerJpaConverter;
    private final AchievementJpaRepository achievementJpaRepository;

    @Autowired
    public PlayerDatabaseAdapter(PlayerJpaRepository playerJpaRepository, AchievementJpaRepository achievementJpaRepository, PlayerJpaConverter playerJpaConverter) {
        this.playerJpaRepository = playerJpaRepository;
        this.achievementJpaRepository = achievementJpaRepository;
        this.playerJpaConverter = playerJpaConverter;
    }

    @Override
    public Player findById(PlayerId playerId) {
        return playerJpaConverter.toDomain(playerJpaRepository.findByIdFetched(playerId.uuid()).orElseThrow(
                () -> new PlayerNotFoundException("Player with given id [%s] is not found".formatted(playerId.uuid()))
        ));
    }

    @Override
    public List<Player> findAllById(List<PlayerId> playerIds) {
        List<Player> players = new ArrayList<>();

        for (PlayerId playerId : playerIds)
            players.add(findById(playerId));

        return players;
    }

    @Override
    public List<Player> saveAll(List<Player> players) {
        List<PlayerJpaEntity> playerJpaEntities = players.stream().map(playerJpaConverter::toJpa).toList();
        List<PlayerJpaEntity> savedPlayerJpaEntities = new ArrayList<>();

        playerJpaEntities.forEach(playerJpaEntity -> {
            Set<AchievementJpaEntity> achievements = playerJpaEntity.getAchievements();
            achievements.forEach(achievement -> achievement.setPerformer(playerJpaEntity));

            playerJpaEntity.setAchievements(null);
            PlayerJpaEntity savedPlayerJpaEntity = playerJpaRepository.save(playerJpaEntity);

            savedPlayerJpaEntity.setAchievements(new HashSet<>(achievementJpaRepository.saveAll(achievements)));

            savedPlayerJpaEntities.add(savedPlayerJpaEntity);
        });

        return savedPlayerJpaEntities.stream().map(playerJpaConverter::toDomain).toList();
    }
}
