package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.exception.GameNotFoundException;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaRepository;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaRepository;
import be.kdg.integration5.checkersachievementcontext.domain.GameId;
import be.kdg.integration5.checkersachievementcontext.port.out.FindGamePort;
import be.kdg.integration5.checkersachievementcontext.port.out.PersistGamePort;
import be.kdg.integration5.checkersachievementcontext.domain.Game;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
public class GameDatabaseAdapter implements PersistGamePort, FindGamePort {
    private final GameJpaRepository gameJpaRepository;
    private final GameJpaConverter gameJpaConverter;

    private final MoveJpaRepository moveJpaRepository;
    private final AchievementJpaRepository achievementJpaRepository;

    public GameDatabaseAdapter(GameJpaRepository gameJpaRepository, GameJpaConverter gameJpaConverter, MoveJpaRepository moveJpaRepository, AchievementJpaRepository achievementJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
        this.gameJpaConverter = gameJpaConverter;
        this.moveJpaRepository = moveJpaRepository;
        this.achievementJpaRepository = achievementJpaRepository;
    }

    @Override
    public Game save(Game game) {
        GameJpaEntity gameJpaEntity = gameJpaConverter.toJpa(game);
        Collection<MoveJpaEntity> moves = gameJpaEntity.getMoves();

        gameJpaEntity.setMoves(null);

        gameJpaRepository.save(gameJpaEntity);
        List<MoveJpaEntity> moveJpaEntities = moveJpaRepository.saveAll(moves);
        gameJpaEntity.setMoves(moveJpaEntities);

        return gameJpaConverter.toDomain(gameJpaEntity);
    }

//    @Override
//    public Game update(Game game) {
//        GameJpaEntity gameJpaEntity = gameJpaConverter.toJpa(game);
//        GameJpaEntity referencedGame = gameJpaRepository.getReferenceById(gameJpaEntity.getGameId());
//
//        referencedGame.setMoves(gameJpaEntity.getMoves());
//        referencedGame.setPlayers(gameJpaEntity.getPlayers());
//
//        return gameJpaConverter.toDomain(referencedGame);
//    }


    @Override
    public Game findById(GameId gameId) {
        GameJpaEntity gameJpaEntity = gameJpaRepository.findByIdFetched(gameId.uuid()).orElseThrow(
                () -> new GameNotFoundException("Game with given id [%s] is not found".formatted(gameId.uuid()))
        );
        List<MoveJpaEntity> moveJpaEntities = moveJpaRepository.findAllByGameIdFetched(gameId.uuid());
        gameJpaEntity.setMoves(moveJpaEntities);
        return gameJpaConverter.toDomain(gameJpaEntity);
    }
}
