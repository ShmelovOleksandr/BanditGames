package be.kdg.integration5.checkerscontext.adapter.out.persistence.game;

import be.kdg.integration5.checkerscontext.adapter.out.persistence.exception.GameNotFoundException;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.piece.PieceJpaEntity;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.piece.PieceJpaEntityId;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.piece.PieceJpaRepository;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.player.PlayerJpaEntity;
import be.kdg.integration5.checkerscontext.adapter.out.persistence.player.PlayerJpaRepository;
import be.kdg.integration5.checkerscontext.domain.*;
import be.kdg.integration5.checkerscontext.port.out.DeleteGamePort;
import be.kdg.integration5.checkerscontext.port.out.FindGamePort;
import be.kdg.integration5.checkerscontext.port.out.PersistGamePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class GameDatabaseAdapter implements PersistGamePort, FindGamePort {
    private final GameJpaRepository gameJpaRepository;
    private final PieceJpaRepository pieceJpaRepository;
    private final PlayerJpaRepository playerJparepository;
    private final GameJpaConverter gameJpaConverter;

    public GameDatabaseAdapter(GameJpaRepository gameJpaRepository, PieceJpaRepository pieceJpaRepository, PlayerJpaRepository playerJparepository, GameJpaConverter gameJpaConverter) {
        this.gameJpaRepository = gameJpaRepository;
        this.pieceJpaRepository = pieceJpaRepository;
        this.playerJparepository = playerJparepository;
        this.gameJpaConverter = gameJpaConverter;
    }

    @Override
    public Game save(Game game) {
        GameJpaEntity gameJpaEntity = gameJpaConverter.toJpa(game);
        playerJparepository.saveAll(gameJpaEntity.getPlayers());

        Set<PieceJpaEntity> pieceJpaEntities = new HashSet<>(gameJpaEntity.getPieces());
        gameJpaEntity.setPieces(null);
        GameJpaEntity savedGameJpaEntity = gameJpaRepository.save(gameJpaEntity);

        List<PieceJpaEntity> savedPieceJpaEntities = pieceJpaRepository.saveAll(pieceJpaEntities);
        savedGameJpaEntity.setPieces(new HashSet<>(savedPieceJpaEntities));
        return gameJpaConverter.toDomain(savedGameJpaEntity);
    }

    @Override
    public Game update(Game game) {
        // Update only Game Jpa fields
        GameJpaEntity gameJpaEntity = gameJpaRepository.getReferenceById(game.getPlayedMatchId().uuid());
        UUID currentPlayerId = game.getBoard().getCurrentPlayer().getPlayerId().uuid();
        gameJpaEntity.setCurrentPlayer(playerJparepository.getReferenceById(currentPlayerId));
        gameJpaEntity.setFinished(game.isFinished());

        if (game.getWinner() != null) {
            PlayerJpaEntity winnerJpaEntity = playerJparepository.getReferenceById(game.getWinner().getPlayerId().uuid());
            gameJpaEntity.setWinner(winnerJpaEntity);
        }

        gameJpaRepository.save(gameJpaEntity);

        // Update pieces
        Set<PieceJpaEntity> existingPieces = gameJpaEntity.getPieces();
        Map<PieceJpaEntityId, PieceJpaEntity> existingPieceMap = existingPieces.stream()
                .collect(Collectors.toMap(PieceJpaEntity::getPieceId, piece -> piece));

        for (Piece piece : game.getBoard().getPieces()) {
            PieceJpaEntityId pieceId = new PieceJpaEntityId(
                    game.getPlayedMatchId().uuid(),
                    piece.getPiecePosition().x(),
                    piece.getPiecePosition().y()
            );
            PieceJpaEntity pieceJpaEntity = existingPieceMap.remove(pieceId);
            if (pieceJpaEntity == null) {
                // Create and add a new piece
                pieceJpaEntity = new PieceJpaEntity(
                        pieceId,
                        gameJpaEntity,
                        piece.isKing(),
                        piece.getColor(),
                        playerJparepository.getReferenceById(piece.getOwner().getPlayerId().uuid())
                );
                existingPieces.add(pieceJpaEntity);
            } else {
                pieceJpaEntity.setKing(piece.isKing());
            }
        }
        existingPieces.removeAll(existingPieceMap.values());
        pieceJpaRepository.deleteAll(existingPieceMap.values());
        pieceJpaRepository.saveAll(existingPieces);

        GameJpaEntity updatedGame = gameJpaRepository.findByIdFetched(game.getPlayedMatchId().uuid())
                .orElseThrow(() -> new GameNotFoundException("Game with ID [%s] not found.".formatted(game.getPlayedMatchId().uuid())));

        return gameJpaConverter.toDomain(updatedGame);
    }

    @Override
    public Game findById(GameId gameId) {
        GameJpaEntity gameJpaEntity = gameJpaRepository.findByIdFetched(gameId.uuid()).orElseThrow(
                () -> new GameNotFoundException("Game with given id [%s] is not found".formatted(gameId.uuid()))
        );
        return gameJpaConverter.toDomain(gameJpaEntity);
    }

    @Override
    public Game findGameByPlayerAndGameEndNull(PlayerId playerId) {
        GameJpaEntity gameJpaEntity = gameJpaRepository.findByPlayerIdAndEndDateNullFetched(playerId.uuid()).orElseThrow(
                () -> new GameNotFoundException("Game for player [%s] not found.]".formatted(playerId.uuid()))
        );
        return gameJpaConverter.toDomain(gameJpaEntity);
    }


}
