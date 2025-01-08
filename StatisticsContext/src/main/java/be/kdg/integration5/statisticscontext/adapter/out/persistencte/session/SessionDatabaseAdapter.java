package be.kdg.integration5.statisticscontext.adapter.out.persistencte.session;

import be.kdg.integration5.statisticscontext.adapter.out.persistencte.exception.PlayerSessionNotFoundException;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.exception.SessionNotFoundException;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.game.GameJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.move.MoveJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaConverter;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_metrics.PlayerMetricsJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player.PlayerJpaRepository;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_session.PlayerSessionId;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_session.PlayerSessionJpaEntity;
import be.kdg.integration5.statisticscontext.adapter.out.persistencte.player_session.PlayerSessionJpaRepository;
import be.kdg.integration5.statisticscontext.domain.Move;
import be.kdg.integration5.statisticscontext.domain.PlayerActivity;
import be.kdg.integration5.statisticscontext.domain.Session;
import be.kdg.integration5.statisticscontext.domain.SessionId;
import be.kdg.integration5.statisticscontext.port.out.FindSessionPort;
import be.kdg.integration5.statisticscontext.port.out.PersistSessionPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SessionDatabaseAdapter implements PersistSessionPort, FindSessionPort {

    private final SessionJpaRepository sessionJpaRepository;
    private final PlayerJpaRepository playerJpaRepository;
    private final GameJpaRepository gameJpaRepository;
    private final PlayerSessionJpaRepository playerSessionJpaRepository;
    private final MoveJpaRepository moveJpaRepository;
    private final PlayerMetricsJpaConverter playerMetricsJpaConverter;
    private final MoveJpaConverter moveJpaConverter;
    private final SessionJpaConverter sessionJpaConverter;

    public SessionDatabaseAdapter(SessionJpaRepository sessionJpaRepository, PlayerJpaRepository playerJpaRepository, GameJpaRepository gameJpaRepository, PlayerSessionJpaRepository playerSessionJpaRepository, MoveJpaRepository moveJpaRepository, PlayerMetricsJpaConverter playerMetricsJpaConverter, MoveJpaConverter moveJpaConverter, SessionJpaConverter sessionJpaConverter) {
        this.sessionJpaRepository = sessionJpaRepository;
        this.playerJpaRepository = playerJpaRepository;
        this.gameJpaRepository = gameJpaRepository;
        this.playerSessionJpaRepository = playerSessionJpaRepository;
        this.moveJpaRepository = moveJpaRepository;
        this.playerMetricsJpaConverter = playerMetricsJpaConverter;
        this.moveJpaConverter = moveJpaConverter;
        this.sessionJpaConverter = sessionJpaConverter;
    }

    @Override
    public Session save(Session session) {
        GameJpaEntity gameJpaEntity = gameJpaRepository.getReferenceById(session.getGame().gameId().uuid());

        SessionJpaEntity sessionJpaEntity = sessionJpaConverter.toJpa(session);
        sessionJpaEntity.setGame(gameJpaEntity);

        SessionJpaEntity savedSessionJpaEntity = sessionJpaRepository.save(sessionJpaEntity);

        // Process and save PlayerSession entities


        Set<PlayerSessionJpaEntity> playerSessionJpaEntities = session.getActivities().stream().map(playerActivity -> {
            PlayerJpaEntity playerJpaEntity = playerJpaRepository.getReferenceById(playerActivity.getPlayer().getPlayerId().uuid());
            PlayerSessionId playerSessionId = new PlayerSessionId(savedSessionJpaEntity.getSessionId(), playerJpaEntity.getPlayerId());

            PlayerSessionJpaEntity playerSessionJpaEntity = new PlayerSessionJpaEntity(playerSessionId);
            playerSessionJpaEntity.setPlayer(playerJpaEntity);
            playerSessionJpaEntity.setSession(savedSessionJpaEntity);

            PlayerSessionJpaEntity savedPlayerSessionJpaEntity = playerSessionJpaRepository.save(playerSessionJpaEntity);

            Set<MoveJpaEntity> moveJpaEntities = playerActivity.getMoves().stream().map(move -> {
                MoveJpaEntity moveJpaEntity = new MoveJpaEntity(
                        move.getMoveId().uuid(),
                        move.getMoveNumber(),
                        move.getEndTime(),
                        move.getStartTime()
                );
                moveJpaEntity.setPlayerSession(savedPlayerSessionJpaEntity);
                return moveJpaRepository.save(moveJpaEntity);
            }).collect(Collectors.toSet());

            savedPlayerSessionJpaEntity.setMoves(moveJpaEntities);

            return savedPlayerSessionJpaEntity;
        }).collect(Collectors.toSet());

        // Link PlayerSession entities to the Session entity
        sessionJpaEntity.setPlayers(playerSessionJpaEntities);

        return sessionJpaConverter.toDomain(sessionJpaEntity);
    }

    @Override
    public Session update(Session session) {
        SessionJpaEntity sessionJpaEntity = sessionJpaRepository.findBySessionIdFetched(session.getSessionId().uuid())
                .orElseThrow(() -> new SessionNotFoundException("Session %s not found".formatted(session.getSessionId().uuid())));

        for (PlayerSessionJpaEntity playerSessionJpaEntity : sessionJpaEntity.getPlayers()) {
            PlayerActivity playerActivity = session.getActivities()
                    .stream()
                    .filter(pa -> pa.getPlayer().getPlayerId().uuid().equals(playerSessionJpaEntity.getPlayer().getPlayerId()))
                    .findFirst()
                    .orElseThrow(() -> new PlayerSessionNotFoundException("Player session linking entity not found."));
            Set<MoveJpaEntity> playerMovesJpa = playerSessionJpaEntity.getMoves();

            // Create or update last move of every player in current session
            Move lastMove = playerActivity.getLastMove();
            MoveJpaEntity moveJpaEntity = moveJpaConverter.toJpa(lastMove);
            moveJpaEntity.setPlayerSession(playerSessionJpaEntity);
            MoveJpaEntity savedMoveJpaEntity = moveJpaRepository.save(moveJpaEntity);

            // Ensure mapping consistency if a new move is added
            if (playerMovesJpa.stream()
                    .noneMatch(moveJpa -> moveJpa.getMoveId().equals(lastMove.getMoveId().uuid()))) {
                playerMovesJpa.add(savedMoveJpaEntity);
                playerSessionJpaEntity.setMoves(playerMovesJpa);
            }

            // Update player metrics
            PlayerMetricsJpaEntity playerMetricsJpaEntity = playerSessionJpaEntity.getPlayer().getPlayerMetrics();
            playerMetricsJpaConverter.updateFields(playerMetricsJpaEntity, playerActivity.getPlayer().getMetrics());
        }

        // Update SessionJpaEntity fields
        sessionJpaConverter.updateValues(sessionJpaEntity, session);

        if (session.getWinner() != null) {
            PlayerJpaEntity winnerJpa = playerJpaRepository.getReferenceById(session.getWinner().getPlayerId().uuid());
            sessionJpaEntity.setWinner(winnerJpa);
        }

        return sessionJpaConverter.toDomain(sessionJpaRepository.save(sessionJpaEntity));
    }

    @Override
    public Session findById(SessionId id) {
        SessionJpaEntity sessionJpaEntity = sessionJpaRepository.findBySessionIdFetched(id.uuid())
                .orElseThrow(() -> new SessionNotFoundException("Session %s not found".formatted(id.uuid())));

        return sessionJpaConverter.toDomain(sessionJpaEntity);
    }

    @Override
    public List<Session> findAllByNotFinishTime(LocalDateTime finishTime) {
        return sessionJpaRepository.findAllFetchedByNotFinishTime(finishTime)
                .stream()
                .map(sessionJpaConverter::toDomain)
                .collect(Collectors.toList());
    }
}
