package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.exception;

import jakarta.persistence.EntityNotFoundException;

public class PlayerNotFoundException extends EntityNotFoundException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
