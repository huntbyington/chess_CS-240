package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameUI implements NotificationHandler{

    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private boolean inGame = true;
    private ChessBoard board;
    private String team;

    public GameUI(ServerFacade serverFacade, ChessBoard board, String team) throws ResponseException {
        this.serverFacade = serverFacade;
        webSocketFacade = new WebSocketFacade(serverFacade.getUrl(), this);
        this.board = board;
        this.team = team;
    }

    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.getServerMessage());
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
                case "leave" -> leave();
                case "move" -> move(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redraw() {
        return new PrintChessBoard(board, team).print().toString();
    }

    private String leave() {
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

        // Make move

        return "";
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
