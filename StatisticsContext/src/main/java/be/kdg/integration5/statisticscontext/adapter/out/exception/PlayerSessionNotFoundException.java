package be.kdg.integration5.statisticscontext.adapter.out.exception;

import jakarta.persistence.EntityNotFoundException;

public class PlayerSessionNotFoundException extends EntityNotFoundException {
    public PlayerSessionNotFoundException(String message) {
        super(message);
    }
}
