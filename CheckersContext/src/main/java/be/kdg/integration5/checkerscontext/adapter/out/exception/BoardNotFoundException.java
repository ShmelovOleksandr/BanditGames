package be.kdg.integration5.checkerscontext.adapter.out.exception;

import jakarta.persistence.EntityNotFoundException;

public class BoardNotFoundException extends EntityNotFoundException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
