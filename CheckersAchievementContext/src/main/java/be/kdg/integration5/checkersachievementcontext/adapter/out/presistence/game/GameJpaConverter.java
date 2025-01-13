package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaEntityId;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaConverter;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import be.kdg.integration5.checkersachievementcontext.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GameJpaConverter {
    private final PlayerJpaConverter playerJpaConverter;

    @Autowired
    public GameJpaConverter(PlayerJpaConverter playerJpaConverter) {
        this.playerJpaConverter = playerJpaConverter;
    }

    public GameJpaEntity toJpa(Game game) {
        UUID gameId = game.getGameId().uuid();
        GameJpaEntity gameJpaEntity = new GameJpaEntity(
                gameId,
                game.isFinished(),
                game.getPlayers().stream().map(playerJpaConverter::toJpa).collect(Collectors.toSet())
        );
        gameJpaEntity.setMoves(game.getBoard().movesHistory().stream().map(move -> {
            Player mover = move.mover();
            return new MoveJpaEntity(
                    new MoveJpaEntityId(gameId, mover.getPlayerId().uuid(), move.madeAt()),
                    gameJpaEntity,
                    new PlayerJpaEntity(mover.getPlayerId().uuid()),
                    move.oldPosition().x(),
                    move.oldPosition().y(),
                    move.newPosition().x(),
                    move.newPosition().y()
            );
        }).toList());
        return gameJpaEntity;
    }

    public Game toDomain(GameJpaEntity gameJpaEntity) {
        return new Game(
                new GameId(gameJpaEntity.getGameId()),
                gameJpaEntity.getPlayers().stream().map(playerJpaConverter::toDomain).toList(),
                gameJpaEntity.isFinished(),
                new Board(gameJpaEntity.getMoves().stream().map(moveJpaEntity -> new Move(
                                new Player(new PlayerId(moveJpaEntity.getMover().getPlayerId())),
                                new PiecePosition(moveJpaEntity.getOldX(), moveJpaEntity.getOldY()),
                                new PiecePosition(moveJpaEntity.getNewX(), moveJpaEntity.getNewY()),
                                moveJpaEntity.getMoveId().getMovedAt()
                        )).toList())
        );
    }
}
