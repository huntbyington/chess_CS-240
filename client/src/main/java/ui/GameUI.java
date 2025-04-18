package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameUI implements NotificationHandler{

//    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private boolean inGame = true;
    private AuthData authData;
    private int gameID;
    private ChessGame game;
    private String team;

    public GameUI(ServerFacade serverFacade, AuthData authData, int gameID, String team) throws ResponseException {
//        this.serverFacade = serverFacade;
        webSocketFacade = new WebSocketFacade(serverFacade.getUrl(), this);
        this.authData = authData;
        this.gameID = gameID;
        game = new ChessGame();
        this.team = team;

        webSocketFacade.connect(authData.authToken(), gameID);
    }

    public void loadGame(LoadGame loadGame) {
        game = loadGame.getGame();
        System.out.println("\n" + redraw());
        printPrompt();
    }

    public void error(ErrorMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.getMessage());
        printPrompt();
    }

    public void notify(Notification notification) {
        System.out.println(SET_TEXT_COLOR_GREEN + notification.getMessage());
        printPrompt();
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
                System.out.print(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + result);
            } catch (Throwable e) {
                var eMsg = e.toString();
                System.out.print(eMsg);
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
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String redraw() {
        return new PrintChessBoard(game.getBoard(), team).print(null).toString();
    }

    private String leave() throws ResponseException {
        webSocketFacade.leave(authData.authToken(), gameID);
        inGame = false;
        return "";
    }

    private String move(String ... params) throws ResponseException {
        if (!(params.length > 1 && params.length < 4)) {
            throw new ResponseException(400, "Expected: <FROM> <TO> opt:<PROMOTION PIECE>");
        }
        if (!(params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]"))) {
            throw new ResponseException(400, "Expected Coordinate Format: [a-h][1-8] (i.e. c2 or h5)");
        }

        ChessPosition from = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a'-1));
        ChessPosition to = new ChessPosition(params[1].charAt(1) - '0',params[1].charAt(0) - ('a'-1));

        ChessPiece.PieceType promotionPiece = null;
        if (params.length == 3) {
            promotionPiece = matchPiece(params[2]);
            if (promotionPiece == null) {
                throw new ResponseException(400, "Expected: <PROMOTION PIECE> must be valid chess piece");
            }
        }

        webSocketFacade.makeMove(authData.authToken(), gameID, new ChessMove(from, to, promotionPiece));

        return "";
    }

    private String resign() throws ResponseException {
        webSocketFacade.resign(authData.authToken(), gameID);
        inGame = false;
        return "";
    }

    private String highlight(String ... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <FROM> <TO> opt:<PROMOTION PIECE>");
        }
        if (!(params[0].matches("[a-h][1-8]"))) {
            throw new ResponseException(400, "Expected Coordinate Format: [a-h][1-8] (i.e. c2 or h5)");
        }

        ChessPosition position = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a'-1));
        Collection<ChessMove> moves = game.getBoard().getPiece(position).pieceMoves(game.getBoard(), position);
        StringBuilder ret = new PrintChessBoard(game.getBoard(), team).print(moves);

        return ret.toString();
    }

    private String help() {
        return (SET_TEXT_COLOR_BLUE + "redraw " + SET_TEXT_COLOR_MAGENTA + "- the chess board\n" +
                SET_TEXT_COLOR_BLUE + "leave " + SET_TEXT_COLOR_MAGENTA + "- the current game\n" +
                SET_TEXT_COLOR_BLUE + "move <FROM> <TO> opt:<PROMOTION PIECE> " + SET_TEXT_COLOR_MAGENTA
                                    + "- make a move, promotion piece is only required when promoting a pawn\n" +
                SET_TEXT_COLOR_BLUE + "resign " + SET_TEXT_COLOR_MAGENTA + "- from the game\n" +
                SET_TEXT_COLOR_BLUE + "highlight <POSITION> " + SET_TEXT_COLOR_MAGENTA + "- highlight all possible moves at a given position\n" +
                SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n");

    }

    private ChessPiece.PieceType matchPiece(String pieceName) {
        return switch (pieceName.toUpperCase()) {
            case "PAWN" -> ChessPiece.PieceType.PAWN;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "KING" -> ChessPiece.PieceType.KING;
            default -> null;
        };
    }
}
