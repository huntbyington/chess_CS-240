package chess;

import chess.movecalculator.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
//    private final PieceMovesCalculator movementType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
//        switch (type) {
//            case PAWN:
//                movementType = new PawnMovesCalculator();
//                break;
//            case KNIGHT:
//                movementType = new KnightMovesCalculator();
//                break;
//            case BISHOP:
//                movementType = new BishopMovesCalculator();
//                break;
//            case ROOK:
//                movementType = new RookMovesCalculator();
//                break;
//            case QUEEN:
//                movementType = new QueenMovesCalculator();
//                break;
//            case KING:
//                movementType = new KingMovesCalculator();
//                break;
//            default:
//                throw new NullPointerException("Piece Type Doesn't Exist");
//        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case PAWN -> PawnMovesCalculator.checkMoves(board, myPosition);
            case KNIGHT -> KnightMovesCalculator.checkMoves(board, myPosition);
            case BISHOP -> BishopMovesCalculator.checkMoves(board, myPosition);
            case ROOK -> RookMovesCalculator.checkMoves(board, myPosition);
            case QUEEN -> QueenMovesCalculator.checkMoves(board, myPosition);
            case KING -> KingMovesCalculator.checkMoves(board, myPosition);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
