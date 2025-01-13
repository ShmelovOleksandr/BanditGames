package be.kdg.integration5.checkersachievementcontext.domain;

import java.util.ArrayList;
import java.util.List;

public record Board(List<Move> movesHistory) {
    public Board(List<Move> movesHistory) {
        this.movesHistory = new ArrayList<>(movesHistory);
    }

    public void addMove(Move madeMove) {
        if (!movesHistory.contains(madeMove))
            movesHistory.add(madeMove);
    }

}
