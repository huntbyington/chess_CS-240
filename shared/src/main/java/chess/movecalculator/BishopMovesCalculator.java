package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{

    private final int[][] moveLogic = {{1,1},{1,-1},{-1,1},{-1,-1}};

    public Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        //Check Bishop moves using moveLogic
        checkMoveLogic(board, myPosition, moveLogic, myTeam, moves);

        return moves;
    }
}
