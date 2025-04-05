package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameUI {

    private ServerFacade serverFacade;
    private boolean inGame = true;
    private ChessBoard board;
    private String team;

    public GameUI(ServerFacade serverFacade, ChessBoard board, String team) {
        this.serverFacade = serverFacade;
        this.board = board;
        this.team = team;
    }

    public String run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (inGame) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

        return result;
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redraw() throws ResponseException{
        return null;
    }

    private String help() {
        return (SET_TEXT_COLOR_BLUE + "redraw " + SET_TEXT_COLOR_MAGENTA + "- the chess board\n" +
                SET_TEXT_COLOR_BLUE + "leave " + SET_TEXT_COLOR_MAGENTA + "- the current game\n" +
                SET_TEXT_COLOR_BLUE + "move <FROM> <TO> <PROMOTION PIECE> " + SET_TEXT_COLOR_MAGENTA
                                    + "- make a move, promotion piece is only required when promoting a pawn\n" +
                SET_TEXT_COLOR_BLUE + "resign " + SET_TEXT_COLOR_MAGENTA + "- from the game\n" +
                SET_TEXT_COLOR_BLUE + "highlight <POSITION> " + SET_TEXT_COLOR_MAGENTA + "- highlight all possible moves at a given position\n" +
                SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n");

    }
}
