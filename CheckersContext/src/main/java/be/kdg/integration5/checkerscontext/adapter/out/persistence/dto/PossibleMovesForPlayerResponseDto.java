package be.kdg.integration5.checkerscontext.adapter.out.persistence.dto;

import java.util.List;
import java.util.UUID;

public record PossibleMovesForPlayerResponseDto(UUID playerId, List<MoveGetDto> moves) {
}
