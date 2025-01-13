package be.kdg.integration5.checkersachievementcontext.domain;

public record PiecePosition(int x, int y) {
    public PiecePosition {
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("X and Y cannot be negative.");
    }
}
