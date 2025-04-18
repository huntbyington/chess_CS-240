package chess.movecalculator;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public static Collection<ChessMove> checkMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        int myTeam = (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        //Checks for promotion if not possible returns an array containing a null piece
        ChessPiece.PieceType[] promotion = getPromotionPieces(myPosition, myTeam);

        //Check forward move
        ChessPosition newPosition = new ChessPosition((myPosition.getRow() + myTeam), myPosition.getColumn());
        if (board.getPiece(newPosition) == null) {
            for(ChessPiece.PieceType currProm : promotion) {
                moves.add(new ChessMove(myPosition, newPosition, currProm));
            }

            //Check for initial moves
            newPosition = new ChessPosition((myPosition.getRow() + (2 * myTeam)), myPosition.getColumn());
            if(myTeam == -1 && myPosition.getRow() == 7 && board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            if(myTeam == 1 && myPosition.getRow() == 2 && board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        
        //Check diagonal capture
        if (myPosition.getColumn() != 8) {
            newPosition = new ChessPosition((myPosition.getRow() + myTeam), (myPosition.getColumn() + 1));
            if ((board.getPiece(newPosition) != null) &&
                    (((board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1) != myTeam)) {
                for (ChessPiece.PieceType currProm : promotion) {
                    moves.add(new ChessMove(myPosition, newPosition, currProm));
                }
            }
        }
        //Check diagonal capture
        if (myPosition.getColumn() != 1) {
            newPosition = new ChessPosition((myPosition.getRow() + myTeam), (myPosition.getColumn() - 1));
            if ((board.getPiece(newPosition) != null) &&
                    (((board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1) != myTeam)) {
                for (ChessPiece.PieceType currProm : promotion) {
                    moves.add(new ChessMove(myPosition, newPosition, currProm));
                }
            }
        }
        return moves;
    }

    private static ChessPiece.PieceType[] getPromotionPieces(ChessPosition myPosition, int direction) {
        ChessPiece.PieceType[] promotion;
        if (direction == -1 && myPosition.getRow() == 2) {
            promotion = new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK,
                                                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
        } else if (direction == 1 && myPosition.getRow() == 7) {
            promotion = new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK,
                                                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
        } else {
            promotion = new ChessPiece.PieceType[]{null};
        }
        return promotion;
    }
}
