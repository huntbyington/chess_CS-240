package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostloginUI {

    private ServerFacade serverFacade;
    private boolean signedIn = true;
    private boolean inGame = false;
    private int gameNum = 0;
    private String team = "WHITE";

    public PostloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (signedIn) {
            if(inGame) {
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                System.out.print(new PrintChessBoard(board, team).print());
                if (Objects.equals(result, "quit")) {
                    signedIn = false;
                }
                inGame = false; // Set to false until GameUI is implemented
                continue;
            }
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
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String create(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <NAME>");
        }

        serverFacade.createGame(params[0]);

        return String.format("You created the game: %s.", params[0]);
    }

    private String list() throws ResponseException {
        var games = serverFacade.listGames();
        var result = new StringBuilder();
        if (games == null) {
            return "No current games";
        }
        for (var game : games) {
            String whiteUsername = (game.whiteUsername() == null) ? "[VACANT]" : game.whiteUsername();
            String blackUsername = (game.blackUsername() == null) ? "[VACANT]" : game.blackUsername();
            result.append("GAME: " + game.gameID() + "\n\tWhite User: " + whiteUsername + "\n\tBlack User: " + blackUsername + "\n\tGame Name: " + game.gameName() + "\n");
        }
        return result.toString();
    }

    private String join(String ... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }

        serverFacade.joinGame(params[1], Integer.parseInt(params[0]));
        inGame = true;
        gameNum = Integer.parseInt(params[0]);
        team = params[1].toUpperCase();

        return "";
    }

    private String observe(String ... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }

        inGame = true;
        gameNum = Integer.parseInt(params[0]);
        team = "WHITE";

        return "";
    }

    private String logout() throws ResponseException {
        serverFacade.logout();

        signedIn = false;
        return "";
    }

    private String quit() throws ResponseException {
        serverFacade.logout();

        signedIn = false;
        return "quit";
    }

    private String help() {
        return (SET_TEXT_COLOR_BLUE + "create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "list " + SET_TEXT_COLOR_MAGENTA + "- games\n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] " + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "observe <ID> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "logout " + SET_TEXT_COLOR_MAGENTA + "- when you are done\n" +
                SET_TEXT_COLOR_BLUE + "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n");

    }

}
