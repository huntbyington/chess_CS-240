package passoff.chess;

import chess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGameTests {
    private ChessGame foolsMate;

    @BeforeEach
    public void setFoolsMate() {
        try {
            foolsMate = new ChessGame();
            foolsMate.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3,6), null));
            foolsMate.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(7,5), null));
            foolsMate.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(4,7), null));
            foolsMate.makeMove(new ChessMove(new ChessPosition(1, 4), new ChessPosition(4,8), null));
        } catch (InvalidMoveException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    @DisplayName("Get Team Test")
    public void getTeamTest() {
        ChessGame myGame = new ChessGame();
        ChessGame.TeamColor expected = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor actual = myGame.getTeamTurn();

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Set Team Test")
    public void setTeamTest() {
        ChessGame myGame = new ChessGame();
        ChessGame.TeamColor expected = ChessGame.TeamColor.BLACK;
        myGame.setTeamTurn(ChessGame.TeamColor.BLACK);
        ChessGame.TeamColor actual = myGame.getTeamTurn();

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Copy Board Test")
    public void copyBoardTest() {
        ChessGame myGame = new ChessGame();
        ChessBoard expected = foolsMate.getBoard();
        myGame.setBoard(foolsMate.getBoard());
        ChessBoard actual = myGame.getBoard();

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Valid Moves Null Test")
    public void validMovesNullTest() {
        ChessGame myGame = new ChessGame();
        Collection<ChessMove> expected = new ArrayList<>();
        Collection<ChessMove> actual = myGame.validMoves(new ChessPosition(3, 1));

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Valid Moves Pawn Test")
    public void validMovesPawnTest() {
        ChessGame myGame = new ChessGame();
        Collection<ChessMove> expected = new ArrayList<>();

        ChessPosition start = new ChessPosition(2,1);
        ChessPosition end = new ChessPosition(3,1);
        expected.add(new ChessMove(start, end, null));
        end = new ChessPosition(4,1);
        expected.add(new ChessMove(start, end, null));

        Collection<ChessMove> actual = myGame.validMoves(new ChessPosition(2, 1));

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Valid Moves Rook Test")
    public void validMovesRookTest() {
        ChessGame myGame = new ChessGame();
        Collection<ChessMove> expected = new ArrayList<>();

        Collection<ChessMove> actual = myGame.validMoves(new ChessPosition(1, 1));

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("Make Moves Pawn Test")
    public void makeMovesPawnTest() {
        ChessGame myGame = new ChessGame();
        Collection<ChessMove> expected = new ArrayList<>();

        Collection<ChessMove> actual = myGame.validMoves(new ChessPosition(1, 1));

        assert expected.equals(actual);
    }

    @Test
    @DisplayName("White Not Check Test")
    public void whiteNotCheckTest() {
        ChessGame myGame = new ChessGame();
        boolean expected = false;

        boolean actual = myGame.isInCheck(ChessGame.TeamColor.WHITE);

        assert expected == actual;
    }

    @Test
    @DisplayName("Black Not Check Test")
    public void blackNotCheckTest() {
        ChessGame myGame = new ChessGame();
        boolean expected = false;

        boolean actual = myGame.isInCheck(ChessGame.TeamColor.BLACK);

        assert expected == actual;
    }
    @Test
    @DisplayName("White Fools Mate Check Test")
    public void whiteFoolsMateCheckTest() {
        ChessGame myGame = new ChessGame();
        boolean expected = true;

        boolean actual = myGame.isInCheck(ChessGame.TeamColor.WHITE);

        assert expected == actual;
    }
}
