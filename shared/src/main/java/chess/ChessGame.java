package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return new ArrayList<>();
        }
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Iterator<ChessMove> movesIter = moves.iterator();
        ChessBoard ogBoard = board.deepCopy();
        ChessGame.TeamColor ogTurn = turn;
        while (movesIter.hasNext()) {
            ChessMove move = movesIter.next();
            try {
                makeMove(move);
            } catch (InvalidMoveException e) {
                if (!e.getMessage().equals("Move made out of turn")) {
                    movesIter.remove();
                }
            } finally {
                setBoard(ogBoard);
                setTeamTurn(ogTurn);
            }
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
       ChessPiece piece = board.getPiece(move.getStartPosition());
       if (piece == null) {
           throw new InvalidMoveException("No Piece at Given location");
       }
       if (!legalMove(move)) {
           throw new InvalidMoveException("Illegal move");
       }
       board.addPiece(move.getStartPosition(), null);
       if (move.getPromotionPiece() != null) {
           piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
       }
       board.addPiece(move.getEndPosition(), piece);
       if (isInCheck(piece.getTeamColor())) {
           throw new InvalidMoveException("Can't move into check");
       }
       if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Move made out of turn");
       }
       turn = (turn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    // Checks if current move is a part of the given pieces move set
    private boolean legalMove (ChessMove checkMove) {
        ChessPiece piece = board.getPiece(checkMove.getStartPosition());
        Collection<ChessMove> moves = piece.pieceMoves(board, checkMove.getStartPosition());
        for (ChessMove move : moves) {
            if (move.equals(checkMove)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> badMoves = new ArrayList<>();
        ChessPosition kingPosition = null;

        for (int i = 1; i < 9; i++) {
            for (int j =1; j < 9; j++) {
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    continue;
                }
                if (newPiece.getTeamColor() != teamColor) {
                    badMoves.addAll(newPiece.pieceMoves(board, newPosition));
                } else if (newPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = newPosition;
                }
            }
        }

        for (ChessMove move : badMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkPossibleKingMoves(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition newPosition = new ChessPosition(i,j);
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = board.getPiece(newPosition).pieceMoves(board, newPosition);
                    if(doesEndCheck(moves, teamColor)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return checkPossibleKingMoves(teamColor);
    }

    // Returns true if there is a move contained within moves that will make the given team not in check
    private boolean doesEndCheck (Collection<ChessMove> moves, ChessGame.TeamColor teamColor) {
        ChessBoard ogBoard = board.deepCopy();
        for (ChessMove move : moves) {
            try {
                makeMove(move);
                if (!isInCheck(teamColor)) {
                    return true;
                }
            } catch (InvalidMoveException ignored) {
                if (!isInCheck(teamColor)) {
                    return true;
                }
            } finally {
                setBoard(ogBoard);
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return checkPossibleKingMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board.deepCopy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
