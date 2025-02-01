package passoff.chess.phase0;

import chess.ChessGame;
import chess.ChessPiece;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessPieceTests {
    @Test
    @DisplayName("Piece Creation")
    public void pieceCreation () {
        chess.ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
    }

    @Test
    @DisplayName("Piece Color")
    public void pieceColor () {
        chess.ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        Assertions.assertEquals(ChessGame.TeamColor.WHITE, newPiece.getTeamColor(), "Assert team color is white");
    }

    @Test
    @DisplayName("Piece Color Fail")
    public void pieceColorFail () {
        chess.ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        Assertions.assertNotEquals(ChessGame.TeamColor.WHITE, newPiece.getTeamColor(), "Assert team color is white but color is black");
    }

    @Test
    @DisplayName("Piece Type")
    public void pieceType () {
        chess.ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        Assertions.assertEquals(ChessPiece.PieceType.KING, newPiece.getPieceType(), "Assert piece is king");
    }

    @Test
    @DisplayName("Piece Type Fail")
    public void pieceTypeFail () {
        chess.ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        Assertions.assertNotEquals(ChessPiece.PieceType.KING, newPiece.getPieceType(), "Assert piece is king but piece is queen");
    }
}
