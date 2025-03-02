package chess.movecalculator;

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
        for (int[] ints : moveLogic) {
            ChessPosition newPosition = new ChessPosition((myPosition.getRow() + ints[0]), (myPosition.getColumn() + ints[1]));
            validMove(board, myPosition, newPosition, myTeam, moves);
        }
        return moves;
    }

    private void validMove (ChessBoard board, ChessPosition myPosition, ChessPosition newPosition,
                            int myTeam, Collection<ChessMove> moves) {
        if (newPosition.getRow() > 8 || newPosition.getRow() < 1 || newPosition.getColumn() > 8 || newPosition.getColumn() < 1) {
            return;
        }
        if (pieceAtPos(board.getPiece(newPosition), myTeam)) {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    //returns true if the knight can move here
    private boolean pieceAtPos(ChessPiece pieceAtNewPos, int myTeam) {
        if (pieceAtNewPos == null) {
            return true;
        }
        int pieceTeam = (pieceAtNewPos.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
        return !(pieceTeam == myTeam);
    }
}
