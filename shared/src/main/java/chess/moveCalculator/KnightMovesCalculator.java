package chess.moveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        //Checks each position with difference in the moveLogic array
        int[][] moveLogic = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};
        for (int i = 0; i < moveLogic.length; i++) {
            ChessPosition newPosition = new ChessPosition((myPosition.getRow() + moveLogic[i][0]), (myPosition.getColumn() + moveLogic[i][1]));
            if(newPosition.getRow() > 8 || newPosition.getRow() < 1 || newPosition.getColumn() > 8 || newPosition.getColumn() < 1) {
                continue;
            }
            if (validMoveCheck(board.getPiece(newPosition), myTeam)) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }
    //returns true if the knight can move here
    private boolean validMoveCheck (ChessPiece pieceAtNewPos, int myTeam) {
        if (pieceAtNewPos == null) {
            return true;
        }
        int pieceTeam = (pieceAtNewPos.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
        return !(pieceTeam == myTeam);
    }
}
