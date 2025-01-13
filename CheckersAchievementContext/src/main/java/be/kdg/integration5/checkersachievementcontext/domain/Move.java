package be.kdg.integration5.checkersachievementcontext.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record Move(
        Player mover,
        PiecePosition oldPosition,
        PiecePosition newPosition,
        LocalDateTime madeAt
){
    public Move {
        Objects.requireNonNull(mover);
        Objects.requireNonNull(oldPosition);
        Objects.requireNonNull(newPosition);
        Objects.requireNonNull(madeAt);
    }
}
