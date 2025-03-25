package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

import static ui.EscapeSequences.*;

public class PrintChessBoard {

    private ChessBoard board;
    private String team;

    public PrintChessBoard(ChessBoard board, String team) {
        this.board = board;
        this.team = team.toUpperCase();
    }

    public StringBuilder print() {
        StringBuilder ret = new StringBuilder();

        String letters = team.equals("WHITE") ? "    a  b  c  d  e  f  g  h    " : "    h  g  f  e  d  c  b  a    ";

        ret.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + letters + RESET_BG_COLOR + "\n");

        if (team.equals("WHITE")) {
            for (int i = 8; i > 0; i--) {
                appendRow(ret, i);
            }
        } else {
            for (int i = 1; i <= 8; i++) {
                appendRow(ret, i);
            }
        }

        ret.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + letters + RESET_BG_COLOR + "\n");

        return ret;
    }

    private void appendRow(StringBuilder row, int rowNum) {
        row.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + " " + rowNum + " ");
        row.append(getRow(rowNum));
        row.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + " " + rowNum + " " + RESET_BG_COLOR + "\n");
    }

    private StringBuilder getRow(int rowNum) {
        StringBuilder ret = new StringBuilder();

        if (Objects.equals(team, "WHITE")) {
            for (int i = 1; i < 9; i++) {
                ret.append(getBG(rowNum, i));
                ret.append(getPiece(rowNum, i));
            }
        } else {
            for (int i = 8; i > 0; i--) {
                ret.append(getBG(rowNum, i));
                ret.append(getPiece(rowNum, i));
            }
        }

        return ret;
    }

    private String getBG(int row, int col) {
        return ((row + col) % 2 == 0) ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
    }

    private StringBuilder getPiece(int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        StringBuilder ret = new StringBuilder();

        if (piece == null) {
            return ret.append("   ");
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ret.append(SET_TEXT_COLOR_BLUE);
        } else {
            ret.append(SET_TEXT_COLOR_RED);
        }

        switch (piece.getPieceType()) {
            case KING -> ret.append(" K ");
            case QUEEN -> ret.append(" Q ");
            case BISHOP -> ret.append(" B ");
            case KNIGHT -> ret.append(" N ");
            case ROOK -> ret.append(" R ");
            case PAWN -> ret.append(" P ");
            default -> ret.append("   ");
        }

        return ret;
    }
}
