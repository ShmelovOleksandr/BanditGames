package be.kdg.integration5.gameplatformcontext.adapter.out.exception;

import jakarta.persistence.EntityNotFoundException;

public class LobbyNotFoundException extends EntityNotFoundException {
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
