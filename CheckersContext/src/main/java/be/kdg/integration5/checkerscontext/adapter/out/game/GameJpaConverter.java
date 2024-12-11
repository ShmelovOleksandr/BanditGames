package be.kdg.integration5.checkerscontext.adapter.out.game;

import be.kdg.integration5.checkerscontext.adapter.out.exception.GameConversionException;
import be.kdg.integration5.checkerscontext.adapter.out.piece.PieceJpaEntity;
import be.kdg.integration5.checkerscontext.adapter.out.piece.PieceJpaEntityId;
import be.kdg.integration5.checkerscontext.adapter.out.player.PlayerJpaEntity;
import be.kdg.integration5.checkerscontext.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GameJpaConverter {
    public GameJpaEntity toJpa(Game game) {
        UUID gameId = game.getPlayedMatchId().uuid();
        GameJpaEntity gameJpaEntity = new GameJpaEntity(
                gameId,
                game.isFinished(),
                game.getPlayers().stream().map(PlayerJpaEntity::of).toList(),
                PlayerJpaEntity.of(game.getCurrentPlayer())
        );

        List<Piece> pieces = game.getBoard().getPieces();
        List<PieceJpaEntity> pieceJpaEntities = pieces.stream().map(piece -> new PieceJpaEntity(
                new PieceJpaEntityId(gameId, piece.getCurrentX(), piece.getCurrentY()),
                gameJpaEntity,
                piece.isKing(),
                piece.getColor(),
                PlayerJpaEntity.of(piece.getOwner())
        )).toList();

        gameJpaEntity.setPieces(pieceJpaEntities);
        return gameJpaEntity;
    }

    public Game toDomain(GameJpaEntity gameJpaEntity) {
        List<PieceJpaEntity> pieceJpaEntities = gameJpaEntity.getPieces();
        if (pieceJpaEntities == null || pieceJpaEntities.isEmpty())
            throw new GameConversionException("PieceJpaEntities is null or empty.");

        List<PlayerJpaEntity> playerJpaEntities = gameJpaEntity.getPlayers();
        if (playerJpaEntities == null || playerJpaEntities.isEmpty())
            throw new GameConversionException("PlayerJpaEntities is null or empty.");


        List<Piece> pieces = pieceJpaEntities.stream().map(pieceJpaEntity -> {
                    PieceJpaEntityId pieceId = pieceJpaEntity.getPieceId();
                    return new Piece(
                            pieceId.getCurrentX(),
                            pieceId.getCurrentY(),
                            pieceJpaEntity.isKing(),
                            pieceJpaEntity.getPieceColor(),
                            pieceJpaEntity.getOwner().toDomain()
                    );
                }
        ).toList();

        List<Player> players = playerJpaEntities.stream().map(PlayerJpaEntity::toDomain).toList();

        Board board = new Board();
        board.setPieces(pieces);

        UUID gameUUID = pieceJpaEntities.getFirst().getPieceId().getGameId();
        GameId gameId = new GameId(gameUUID);

        return new Game(gameId, board, players);
    }
}
