package be.kdg.integration5.checkersachievementcontext.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Game {
    private GameId gameId;
    private List<Player> players;

    @Setter(AccessLevel.NONE)
    private boolean isFinished;

    private Board board;

    public Game(GameId gameId, List<Player> players) {
        this.gameId = gameId;
        this.players = new ArrayList<>(players);
        this.board = new Board(new ArrayList<>());
    }

    public Game(GameId gameId, List<Player> players, boolean isFinished, Board board) {
        this.gameId = gameId;
        this.players = players;
        this.isFinished = isFinished;
        this.board = board;
    }

    public void finish() {
        this.isFinished = true;
    }
}
