package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.exception;

import jakarta.persistence.EntityNotFoundException;

public class GameNotFoundException extends EntityNotFoundException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
