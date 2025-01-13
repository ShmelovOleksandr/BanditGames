package be.kdg.integration5.checkerscontext.port.out;

public interface NotifyCheckersGameStartedPort {
    void notifyCheckersGameStarted(CheckersGameStartedCommand checkersGameStartedCommand);
}
