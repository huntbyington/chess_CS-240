package chess.moveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        //Check Rook moves using moveLogic
        int[][] moveLogic = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int i = 0; i < moveLogic.length; i++) {
            //Set piece check false until piece or border is seen
            boolean pieceCheck = false;
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (!pieceCheck) {
                newPosition = new ChessPosition((newPosition.getRow() + moveLogic[i][0]), (newPosition.getColumn() + moveLogic[i][1]));
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

        return moves;
    }
    private boolean validMoveCheck (ChessPiece pieceAtNewPos) {
        return pieceAtNewPos == null;
    }
}