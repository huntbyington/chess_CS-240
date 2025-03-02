package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
        int[][] moveLogic = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        //Creates a collection of spaces the king can't move to
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Collection<ChessMove> badMoves = new ArrayList<>();
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece newPiece = board.getPiece(newPosition);
                if(newPiece != null) {
                    int pieceTeam = (newPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
                    if (myTeam != pieceTeam) {
                        if (newPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            badMoves.addAll(kingSpecCase(newPosition));
                        } else {
                            badMoves.addAll(newPiece.pieceMoves(board, newPosition));
                        }
                    }
                    legalMove(board, myPosition, moveLogic, badMoves);
                }
            }
        }

        for (int[] ints : moveLogic) {
            if (ints[0] != -8) {
                ChessPosition newPosition = new ChessPosition((myPosition.getRow() + ints[0]), (myPosition.getColumn() + ints[1]));
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        return moves;
    }

    //Compares badMoves and possible moves, doesn't add the possible moves contained within bad moves
    private void legalMove(ChessBoard board, ChessPosition myPosition, int[][] moveLogic, Collection<ChessMove> badMoves){
        ChessPiece currPiece = board.getPiece(myPosition);
        for (int i = 0; i < moveLogic.length; i++) {
            ChessPosition newPosition = new ChessPosition((myPosition.getRow() + moveLogic[i][0]), (myPosition.getColumn() + moveLogic[i][1]));
            if ((newPosition.getRow() > 8) || (newPosition.getRow() < 1) || (newPosition.getColumn() > 8) || (newPosition.getColumn() < 1)) {
                moveLogic[i][0] = -8;
                moveLogic[i][1] = -8;
                continue;
            }
            for (ChessMove currMove : badMoves) {
                if (currMove.getEndPosition() == newPosition) {
                    //Sets movement to arbitrary out-of-bounds value
                    moveLogic[i][0] = -8;
                    moveLogic[i][1] = -8;
                }
            }
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() == currPiece.getTeamColor()) {
                    moveLogic[i][0] = -8;
                    moveLogic[i][1] = -8;
                }
            }
        }
    }

    private Collection<ChessMove> kingSpecCase(ChessPosition kingPos) {
        Collection<ChessMove> badMoves = new ArrayList<>();
        int[][] kingLogic = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] ints : kingLogic) {
            ChessPosition newPosition = new ChessPosition(ints[0], ints[1]);
            if ((newPosition.getRow() > 8) || (newPosition.getRow() < 1) || (newPosition.getColumn() > 8) || (newPosition.getColumn() < 1)) {
                badMoves.add(new ChessMove(kingPos, newPosition, null));
            }
        }
        return badMoves;
    }
}
