package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{

    private static final int[][] moveLogic = {{1,0},{-1,0},{0,1},{0,-1}};

    public static Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        //Check Rook moves using moveLogic
        PieceMovesCalculator.checkMoveLogic(board, myPosition, moveLogic, myTeam, moves);

        return moves;
    }
}