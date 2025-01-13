package be.kdg.integration5.checkerscontext.port.out;

public interface NotifyCheckersGameFinishedPort {
    void notifyCheckersGameFinished(CheckersGameFinishedCommand checkersGameFinishedCommand);
}
