package be.kdg.integration5.checkerscontext.adapter.out.persistence.dto;

import be.kdg.integration5.checkerscontext.adapter.in.dto.PieceGetDto;
import be.kdg.integration5.checkerscontext.domain.Game;

import java.util.*;
import java.util.stream.Collectors;

public record GameStateResponseDto(Boolean isFinished, UUID winnerId, boolean isDraw, UUID currentPlayer, List<PlayerResponseDto> players, List<PieceGetDto> pieces) {
    public GameStateResponseDto {
        Objects.requireNonNull(isFinished);
        Objects.requireNonNull(currentPlayer);
        Objects.requireNonNull(pieces);
    }
    public static GameStateResponseDto of(Game game) {
        List<PieceGetDto> pieces = Arrays.stream(game.getBoard().getSquares())
                .flatMap(Arrays::stream)
                .filter(square -> !square.isEmpty())
                .map(square -> PieceGetDto.of(square.getPlacedPiece()))
                .collect(Collectors.toList());

        Map<UUID, PlayerResponseDto> uniquePlayers = game.getBoard().getPieces().stream()
                .collect(Collectors.toMap(
                        piece -> piece.getOwner().getPlayerId().uuid(), // Key: Player ID
                        piece -> new PlayerResponseDto(
                                piece.getOwner().getPlayerId().uuid(),
                                piece.getOwner().getName(),
                                piece.getColor().name()
                        ), // Value: PlayerResponseDto
                        (existing, duplicate) -> existing // Resolves duplicates
                ));

        // Convert unique players to a list
        List<PlayerResponseDto> players = new ArrayList<>(uniquePlayers.values());


        return new GameStateResponseDto(
                game.isFinished(),
                game.getWinner() == null ? null : game.getWinner().getPlayerId().uuid(),
                game.isDraw(),
                game.getBoard().getCurrentPlayer().getPlayerId().uuid(),
                players,
                pieces
        );
    }
}
