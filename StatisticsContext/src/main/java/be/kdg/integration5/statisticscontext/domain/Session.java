package be.kdg.integration5.statisticscontext.domain;


import be.kdg.integration5.statisticscontext.domain.exception.PlayerNotPartOfSessionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Session {
    private SessionId sessionId;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private boolean isDraw;
    private List<PlayerActivity> activities;
    private Game game;
    private Player winner;

    public Session(SessionId sessionId, Game game) {
        this.sessionId = sessionId;
        this.startTime = null;
        this.finishTime = null;
        this.isDraw = false;
        this.activities = new ArrayList<>();
        this.game = game;
    }

    public void start(LocalDateTime startTime, List<Player> players, PlayerId firstPlayerId) {
        this.startTime = startTime;
        this.activities = players.stream().map(PlayerActivity::new).collect(Collectors.toList());

        PlayerActivity firstPlayerActivity = getPlayerActivity(firstPlayerId);
        firstPlayerActivity.startMove(startTime);
    }

    public void processMove(PlayerId playerId, PlayerId nextPlayerId, LocalDateTime moveDateTime) {
        PlayerActivity playerActivity = this.getPlayerActivity(playerId);
        playerActivity.endMove(moveDateTime);

        PlayerActivity nextPlayerActivity = this.getPlayerActivity(nextPlayerId);
        nextPlayerActivity.startMove(moveDateTime);
    }
    public void gameFinish() {
        // update all metrics of all players
    }

    private PlayerActivity getPlayerActivity(PlayerId playerId) {
        return activities
                .stream()
                .filter(activity -> activity.getPlayer().getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotPartOfSessionException(
                        "Player %s not part of session %s".formatted(playerId.uuid(), this.getSessionId().uuid())));

    }
}
