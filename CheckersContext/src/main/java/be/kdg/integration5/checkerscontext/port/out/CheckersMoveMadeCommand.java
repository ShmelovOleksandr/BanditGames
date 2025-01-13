package be.kdg.integration5.checkerscontext.port.out;

import be.kdg.integration5.checkerscontext.domain.GameId;
import be.kdg.integration5.checkerscontext.domain.Move;
import be.kdg.integration5.checkerscontext.domain.PlayerId;

import java.util.Objects;

public record CheckersMoveMadeCommand(GameId gameId, PlayerId moverId, Move move) {
    public CheckersMoveMadeCommand {
        Objects.requireNonNull(moverId);
        Objects.requireNonNull(move);
    }
}
