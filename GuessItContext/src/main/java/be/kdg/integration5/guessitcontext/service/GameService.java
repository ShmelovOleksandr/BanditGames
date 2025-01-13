package be.kdg.integration5.guessitcontext.service;

import be.kdg.integration5.guessitcontext.domain.MoveResult;
import be.kdg.integration5.guessitcontext.domain.PlayerId;
import be.kdg.integration5.guessitcontext.domain.Session;
import be.kdg.integration5.guessitcontext.domain.SessionId;
import jakarta.transaction.Transactional;

public interface GameService {
    MoveResult makeMove(PlayerId playerId, Integer guess);
    void notifyGameRegistration();
    void sendSessionInfo(SessionId sessionId);
    void notifyAllPlayersSession(Session session);
    void notifyPlayersAfterIncorrectMove(MoveResult moveResult, Session session);
}
