package chess.movecalculator;

import chess.*;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition);

    default void checkMoveLogic(ChessBoard board, ChessPosition myPosition, int[][] moveLogic, int myTeam, Collection<ChessMove> moves) {
        for (int[] ints : moveLogic) {
            //Set piece check false until piece or border is seen
            boolean pieceCheck = false;
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (!pieceCheck) {
                newPosition = new ChessPosition((newPosition.getRow() + ints[0]), (newPosition.getColumn() + ints[1]));
                if (newPosition.getRow() > 8 || newPosition.getRow() < 1 || newPosition.getColumn() > 8 || newPosition.getColumn() < 1) {
                    pieceCheck = true;
                    continue;
                }
                if (validMoveCheck(board.getPiece(newPosition))) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    continue;
                }
                pieceCheck = true;
                int currPosTeam = (board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
                if (myTeam != currPosTeam) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    default boolean validMoveCheck (ChessPiece pieceAtNewPos) {
        return pieceAtNewPos == null;
    }
}
