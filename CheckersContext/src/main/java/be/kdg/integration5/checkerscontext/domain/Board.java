package be.kdg.integration5.checkerscontext.domain;

import be.kdg.integration5.checkerscontext.domain.exception.DirectionSearchException;
import be.kdg.integration5.checkerscontext.domain.exception.MoveNotValidException;
import be.kdg.integration5.checkerscontext.domain.exception.NoPiecesLeftException;
import be.kdg.integration5.checkerscontext.domain.exception.NotPossibleToPlacePieceException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class Board {
    public static final int BOARD_SIZE = 10;
    public static final int PIECES_PER_PLAYER = 20;

    private Player currentPlayer;

    private final Square[][] squares;
    private final List<Piece> pieces;
    private final List<Player> players;

    {
        this.pieces = new ArrayList<>();
        this.squares = new Square[BOARD_SIZE][BOARD_SIZE];
        generateBoard();
    }

    public Board(List<Player> players) {
        this.players = Collections.unmodifiableList(players);
    }

    public Board(List<Piece> pieces, List<Player> players, Player currentPlayer) {
        this.pieces.addAll(pieces);
        this.players = players;
        this.currentPlayer = currentPlayer;
        updateBoard();
    }

    public void setUpNewBoard(Player firstPlayer) {
        setCurrentPlayer(firstPlayer);
        createPieces(firstPlayer);
        placePieces();
    }

    private void createPieces(Player firstPlayer) {
        Player secondPlayer = players.stream().filter(player -> !player.equals(firstPlayer)).findFirst().orElseThrow(
                () -> new IllegalStateException("Second Player not found")
        );

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                if ((y + x) % 2 == 1) {
                    if (y * 10 + x < PIECES_PER_PLAYER * 2)
                        pieces.add(new Piece(new PiecePosition(x, y), Piece.PieceColor.BLACK, secondPlayer));
                    else if (y * 10 + x > BOARD_SIZE * BOARD_SIZE - PIECES_PER_PLAYER * 2)
                        pieces.add(new Piece(new PiecePosition(x, y), Piece.PieceColor.WHITE, firstPlayer));
                }
            }
        }
    }

    private void updateBoard() {
        removeAllPieces();
        placePieces();
    }

    public void addPiece(Piece piece) {
        if (piece == null)
            return;

        int x = piece.getPiecePosition().x();
        int y = piece.getPiecePosition().y();
        Square square = this.squares[y][x];

        if (!square.isEmpty())
            throw new NotPossibleToPlacePieceException("Target square is occupied. [x: %s, y: %s]".formatted(x, y));

        if (pieces.contains(piece))
            throw new NotPossibleToPlacePieceException("Piece is already placed on the board.");

        this.pieces.add(piece);
        square.setPlacedPiece(piece);
    }

    public void removePiece(int x, int y) {
        this.squares[y][x].removePiece();
        this.pieces.removeIf(piece -> piece.getPiecePosition().x() == x && piece.getPiecePosition().y() == y);
    }

    public void removeAllPieces() {
        for (Square[] row : squares)
            for (Square square : row)
                square.removePiece();
    }

    private void placePieces() {
        for (Piece piece : pieces) {
            int x = piece.getPiecePosition().x();
            int y = piece.getPiecePosition().y();
            squares[y][x].setPlacedPiece(piece);
        }
    }

    private void generateBoard() {
        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 0; x < BOARD_SIZE; x++)
                squares[y][x] = new Square(x, y);
    }

    public List<Move> getPossibleMoves(Piece piece) {
        List<Move> attackMoves = getAllAttackMoves(piece);
        if (!attackMoves.isEmpty())
            return attackMoves;

        return getAllGoMoves(piece);
    }

    private List<Move> getAllGoMoves(Piece piece) {
        List<Move> goMoves = new ArrayList<>();

        int currentX = piece.getPiecePosition().x();
        int currentY = piece.getPiecePosition().y();
        Piece.PieceColor pieceColor = piece.getColor();
        boolean isKing = piece.isKing();

        for (MoveDirection direction : MoveDirection.values()) {
            int targetX = currentX + direction.xShift;
            int targetY = currentY + direction.yShift;

            if (isKing) {
                for (int x = targetX, y = targetY; !isOutOfBounds(x, y); x += direction.xShift, y += direction.yShift) {
                    if (squares[y][x].isEmpty())
                        goMoves.add(new Move(
                                new PiecePosition(currentX, currentY),
                                new PiecePosition(x, y),
                                Move.MoveType.GO
                        ));
                    else
                        break;
                }
            } else {
                if (!isMovingForward(direction.yShift, pieceColor)) continue;

                if (isOutOfBounds(targetX, targetY)) continue;

                Square targetSquare = squares[targetY][targetX];

                if (targetSquare.isEmpty()) {
                    goMoves.add(new Move(
                            new PiecePosition(currentX, currentY),
                            new PiecePosition(targetX, targetY),
                            Move.MoveType.GO
                    ));
                }
            }
        }
        return goMoves;
    }

    private List<Move> getAllAttackMoves(Piece piece) {
        return getAttackMovesFromSquare(piece);
    }

    private List<Move> getAttackMovesFromSquare(Piece piece) {
        return getAttackMovesFromSquare(piece, new HashSet<>());
    }

    private List<Move> getAttackMovesFromSquare(Piece piece, Set<PiecePosition> markedForRemovalPositions) {
        PiecePosition piecePosition = piece.getPiecePosition();
        markedForRemovalPositions.add(piecePosition);

        Piece.PieceColor pieceColor = piece.getColor();
        boolean isKing = piece.isKing();

        List<Move> attackMoves = new ArrayList<>();
        for (MoveDirection direction : MoveDirection.values()) {
            List<PiecePosition> possibleLandingPositions = getPossibleLandingPositions(piecePosition, direction, pieceColor, isKing, markedForRemovalPositions);

            for (PiecePosition landingPosition : possibleLandingPositions) {
                PiecePosition enemyPosition = findPiecePositionInBetween(piecePosition, landingPosition, direction);
                markedForRemovalPositions.add(enemyPosition);
                Piece pieceOnLandedSquare = new Piece(landingPosition, isKing, pieceColor, piece.getOwner());
                List<Move> possibleAttackMovesFromLandingPosition = getAttackMovesFromSquare(pieceOnLandedSquare, markedForRemovalPositions);
                if (!possibleAttackMovesFromLandingPosition.isEmpty()) {
                    attackMoves.addAll(joinAttackMoves(piecePosition, possibleAttackMovesFromLandingPosition));
                } else {
                    attackMoves.add(new Move(piecePosition, landingPosition, Move.MoveType.ATTACK));
                }
            }
        }
        return selectLongestAttackMoves(attackMoves);
    }

    private PiecePosition findPiecePositionInBetween(PiecePosition startPosition, PiecePosition endPosition, MoveDirection direction) {
        for (int x = startPosition.x() + direction.xShift, y = startPosition.y() + direction.yShift; x != endPosition.x() && y != endPosition.y(); x += direction.xShift, y += direction.yShift) {
            if (!squares[y][x].isEmpty())
                return new PiecePosition(x, y);
        }
        throw new IllegalStateException("No pieces found in between two positions.");
    }

    private List<Move> selectLongestAttackMoves(List<Move> allAttackMoves) {
        if (allAttackMoves.isEmpty())
            return allAttackMoves;

        allAttackMoves.sort((Comparator.comparingInt(Move::getMoveLength)));
        int longestMoveLength = allAttackMoves.getFirst().getMoveLength();
        return allAttackMoves.stream().filter(move -> move.getMoveLength() == longestMoveLength).toList();
    }

    private List<PiecePosition> getPossibleLandingPositions(PiecePosition piecePosition, MoveDirection direction, Piece.PieceColor pieceColor, boolean isKing, Set<PiecePosition> markedForRemovalPositions) {
        List<PiecePosition> possibleLandingSquares = new ArrayList<>();
        int enemyX, enemyY, landingX, landingY;
        if (!isKing) {
            enemyX = piecePosition.x() + direction.xShift;
            enemyY = piecePosition.y() + direction.yShift;

            landingX = enemyX + direction.xShift;
            landingY = enemyY + direction.yShift;

            if (isOutOfBounds(enemyX, enemyY) || isOutOfBounds(landingX, landingY)) {
                return possibleLandingSquares;
            }

            Square enemySquare = squares[enemyY][enemyX];
            Piece enemyPiece = enemySquare.getPlacedPiece();
            if (enemyPiece == null)
                return possibleLandingSquares;

            Square landingSquare = squares[landingY][landingX];

            PiecePosition enemyPiecePosition = enemyPiece.getPiecePosition();

            if (!enemySquare.isEmpty() &&
                    enemyPiece.getColor() != pieceColor &&
                    landingSquare.isEmpty() &&
                    !markedForRemovalPositions.contains(enemyPiecePosition)) {
                possibleLandingSquares.add(new PiecePosition(landingX, landingY));
            }
        } else {
            boolean enemyFound = false;
            for (int x = piecePosition.x() + direction.xShift, y = piecePosition.y() + direction.yShift; !isOutOfBounds(x, y); x += direction.xShift, y += direction.yShift) {
                Square enemySquare = squares[y][x];
                if (!enemyFound) {
                    if (!enemySquare.isEmpty() && !markedForRemovalPositions.contains(new PiecePosition(x, y))) {
                        int nextX = x + direction.xShift;
                        int nextY = y + direction.yShift;
                        if (!isOutOfBounds(nextX, nextY)) {
                            Square nextSquare = squares[nextY][nextX];
                            if (nextSquare.isEmpty()) {
                                enemyFound = true;
                            }
                        }
                    }
                } else if (!enemySquare.isEmpty()) {
                    break;
                } else {
                    possibleLandingSquares.add(new PiecePosition(x, y));
                }
            }
        }

        return possibleLandingSquares;
    }

    private List<Move> joinAttackMoves(PiecePosition initialPosition, List<Move> finishingAttackMovesList) {
        for (Move finishingMove : finishingAttackMovesList) {
            finishingMove.addNewInitialPositionInSequence(initialPosition);
        }
        return finishingAttackMovesList;
    }

    private boolean isMovingForward(int yChange, Piece.PieceColor pieceColor) {
        return (pieceColor == Piece.PieceColor.WHITE && yChange < 0) || (pieceColor == Piece.PieceColor.BLACK && yChange > 0);
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= Board.BOARD_SIZE || y < 0 || y >= Board.BOARD_SIZE;
    }

    private int calculateDistance(int x, int y, int targetX, int targetY) {
        return Math.max(Math.abs(targetX - x), Math.abs(targetY - y));
    }

    public void movePiece(PlayerId moverId, Move move) {
        validateMove(moverId, move);

        changePieceLocation(move);
        if (move.getType() == Move.MoveType.ATTACK)
            removeCapturedPieces(move);

        checkForPieceUpgrade(move);

        updateBoard();
        switchCurrentPlayer();
    }

    private void checkForPieceUpgrade(Move move) {
        PiecePosition futurePosition = move.getFuturePosition();
        Piece piece = this.squares[futurePosition.y()][futurePosition.x()].getPlacedPiece();
        if (piece == null)
            return;

        if (shouldUpgradePiece(piece))
            piece.upgrade();
    }

    private boolean shouldUpgradePiece(Piece checkedPiece) {
        if (checkedPiece == null)
            return false;

        int y = checkedPiece.getPiecePosition().y();

        return (checkedPiece.getColor() == Piece.PieceColor.WHITE && y == 0) ||
                (checkedPiece.getColor() == Piece.PieceColor.BLACK && y == BOARD_SIZE - 1);
    }

    private void switchCurrentPlayer() {
        int nextPlayerIndex = players.indexOf(currentPlayer) + 1;
        if (nextPlayerIndex >= players.size())
            nextPlayerIndex = 0;

        this.currentPlayer = players.get(nextPlayerIndex);
    }


    private void changePieceLocation(Move move) {
        PiecePosition oldPosition = move.getInitialPosition();
        PiecePosition newPosition = move.getFuturePosition();
        Square oldSquare = squares[oldPosition.y()][oldPosition.x()];
        Square newSquare = squares[newPosition.y()][newPosition.x()];

        newSquare.setPlacedPiece(oldSquare.getPlacedPiece());
        oldSquare.removePiece();
    }

    private void removeCapturedPieces(Move move) {
        PiecePosition previousPosition = move.getInitialPosition();
        for (PiecePosition intermediateAttackPosition : move.getAllAttackSteps()) {
            removeAllPiecesInBetween(previousPosition, intermediateAttackPosition);
            previousPosition = intermediateAttackPosition;
        }
    }

    private void removeAllPiecesInBetween(PiecePosition startPosition, PiecePosition endPosition) {
        MoveDirection direction = findMoveDirection(startPosition, endPosition);
        for (int x = startPosition.x() + direction.xShift, y = startPosition.y() + direction.yShift; x != endPosition.x() && y != endPosition.y(); x += direction.xShift, y += direction.yShift) {
            Square targetSquare = squares[y][x];
            if (!targetSquare.isEmpty())
                removePiece(x, y);
        }
    }

    private void validateMove(PlayerId moverId, Move move) {
        int currentX = move.getInitialPosition().x();
        int currentY = move.getInitialPosition().y();
        int targetX = move.getFuturePosition().x();
        int targetY = move.getFuturePosition().y();

        if (currentX == targetX && currentY == targetY)
            throw new MoveNotValidException("Moving to the same location is not possible.", moverId);

        if (isOutOfBounds(targetX, targetY) || isOutOfBounds(currentX, currentY))
            throw new MoveNotValidException("Move positions can not be less than 0 and greater than " + BOARD_SIZE, moverId);


        Piece piece = squares[currentY][currentX].getPlacedPiece();
        if (piece == null)
            throw new MoveNotValidException("No piece is placed on this square.", moverId);

        PlayerId ownerId = piece.getOwner().getPlayerId();
        if (!moverId.equals(ownerId))
            throw new MoveNotValidException("Only owner is allowed to move the piece.", moverId);

        Square targetSquare = squares[targetY][targetX];
        if (!targetSquare.isEmpty())
            throw new MoveNotValidException("Moving to the occupied square is not allowed.", moverId);


        Move.MoveType moveType = move.getType();
        boolean isKing = piece.isKing();
        if (moveType == Move.MoveType.GO) {
            if (!isMovingForward(targetY - currentY, piece.getColor()) && !isKing)
                throw new MoveNotValidException("Non king piece can not move backwards.", moverId);

            int distance = calculateDistance(currentX, currentY, targetX, targetY);
            if (distance != 1 && !isKing)
                throw new MoveNotValidException("Non king piece can not step more than 1 square away.", moverId);

            if (calculateLongestDiagonalPiecesSequenceInBetween(move.getInitialPosition(), move.getFuturePosition()) != 0)
                throw new MoveNotValidException("GO move should be crossing other pieces.", moverId);

        } else if (moveType == Move.MoveType.ATTACK) {
            List<PiecePosition> attackPositions = move.getAllAttackSteps();
            PiecePosition previousPosition = move.getInitialPosition();
            for (PiecePosition intermediateAttackPosition : attackPositions) {
                int intermediateX = intermediateAttackPosition.x();
                int intermediateY = intermediateAttackPosition.y();

                if (!squares[intermediateY][intermediateX].isEmpty())
                    throw new MoveNotValidException("All attack steps should be empty.", moverId);

                int distance = calculateDistance(previousPosition.x(), previousPosition.y(), intermediateX, intermediateY);
                if (distance != 2 && !isKing)
                    throw new MoveNotValidException("Non king piece can attack only over one square.", moverId);

                int piecesSequenceInBetween = calculateLongestDiagonalPiecesSequenceInBetween(previousPosition, intermediateAttackPosition);
                if (piecesSequenceInBetween > 1)
                    throw new MoveNotValidException("ATTACK move can not take two or more piece next to each other.", moverId);
                else if (piecesSequenceInBetween < 1)
                    throw new MoveNotValidException("ATTACK move can be performed only if there is a piece to attack.", moverId);


                previousPosition = intermediateAttackPosition;
            }
        }
    }

    private int calculateLongestDiagonalPiecesSequenceInBetween(PiecePosition startPosition, PiecePosition endPosition) {
        MoveDirection direction = findMoveDirection(startPosition, endPosition);
        int piecesSequenceCounter = 0;
        for (int x = startPosition.x() + direction.xShift, y = startPosition.y() + direction.yShift;
             x != endPosition.x() && y != endPosition.y();
             x += direction.xShift, y += direction.yShift) {
            Square targetSquare = squares[y][x];
            if (!targetSquare.isEmpty())
                piecesSequenceCounter++;
            else
                piecesSequenceCounter = 0;
        }
        return piecesSequenceCounter;
    }

    private MoveDirection findMoveDirection(PiecePosition currentPosition, PiecePosition targetPosition) {
        int deltaX = targetPosition.x() - currentPosition.x();
        int deltaY = targetPosition.y() - currentPosition.y();

        if (!isMoveDiagonal(deltaY, deltaX))
            throw new IllegalArgumentException("Absolute delta Y does not match absolute delta X, move is not diagonal.");

        for (MoveDirection direction : MoveDirection.values())
            if (deltaX * direction.xShift > 0 && deltaY * direction.yShift > 0)
                return direction;

        throw new DirectionSearchException("Couldn't find corresponding move direction.");
    }

    private boolean isMoveDiagonal(int deltaY, int deltaX) {
        return Math.abs(deltaY) == Math.abs(deltaX);
    }

    public boolean piecesOfBothColorsArePresent() {
        return pieces.stream().anyMatch(piece -> piece.getColor() == Piece.PieceColor.BLACK) &&
                pieces.stream().anyMatch(piece -> piece.getColor() == Piece.PieceColor.WHITE);
    }

    public Optional<Player> getWinningPiecesOwner() {
        if (piecesOfBothColorsArePresent())
            return Optional.empty();

        return Optional.of(pieces.stream().findFirst().orElseThrow(
                () -> new NoPiecesLeftException("There are no pieces left on the board to find the winner.")
        ).getOwner());
    }


    private enum MoveDirection {
        UP_RIGHT(1, -1),
        UP_LEFT(-1, -1),
        DOWN_RIGHT(1, 1),
        DOWN_LEFT(-1, 1);

        final int xShift;
        final int yShift;

        MoveDirection(int xShift, int yShift) {
            this.xShift = xShift;
            this.yShift = yShift;
        }
    }
}
