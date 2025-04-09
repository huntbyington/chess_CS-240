package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {

    private static final ConnectionManager CONNECTIONS = new ConnectionManager();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    int makeMoveCnt = 0;

    public WebsocketHandler() {
        this.userDAO = DAOProvider.getUserDAO();
        this.gameDAO = DAOProvider.getGameDAO();
        this.authDAO = DAOProvider.getAuthDAO();
    }

    private static void exceptionHandler(String eMessage, Session session) {
        try {
            switch (eMessage) {
                case "Incorrect Authorization", "Incorrect Username or Password" -> {
                    CONNECTIONS.sendToUser(session, new ErrorMessage("Error: unauthorized"));
                }
                case "Invalid Game ID", "Invalid Player Color", "Invalid Game Name" -> {
                    CONNECTIONS.sendToUser(session, new ErrorMessage("Error: bad request"));
                }
                case "Invalid move" -> {
                    CONNECTIONS.sendToUser(session, new ErrorMessage("Error: Invalid move"));
                }
                case null, default -> {
                    CONNECTIONS.sendToUser(session, new ErrorMessage(("Error: " + eMessage)));
                }
            }
        } catch (IOException ignored) {}
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> {
                handleConnect(session, command);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                handleMakeMove(session, makeMoveCommand);
            }
            case LEAVE -> {
                handleLeave(session, command);
            }
            case RESIGN -> {
                handleResign(session, command);
            }
        }
    }

    private void validateAuthAndGame(AuthData authData, GameData gameData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }
        if (gameData == null) {
            throw new DataAccessException("Invalid Game ID");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            Object gameLock = CONNECTIONS.getGameLock(gameID);

            synchronized (gameLock) {
                AuthData authData = authDAO.getAuth(command.getAuthToken());
                GameData gameData = gameDAO.getGame(command.getGameID());
                validateAuthAndGame(authData, gameData);

                CONNECTIONS.add(authData.username(), command.getGameID(), session);

                CONNECTIONS.sendToUser(session, new LoadGame(gameData.game()));

                var message = String.format("%s joined the game", authData.username());
                CONNECTIONS.broadcast(authData.username(), gameData.gameID(), new Notification(message));
            }
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException ignored) {
        }
    }

    private boolean checkObserver(String username, GameData gameData) {
        return !(Objects.equals(username, gameData.whiteUsername()) || Objects.equals(username, gameData.blackUsername()));
    }

    private boolean checkPieceColor(ChessPosition position, String username, GameData gameData) {
        ChessGame.TeamColor team = (Objects.equals(username, gameData.whiteUsername())) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        return gameData.game().getBoard().getPiece(position).getTeamColor() == team;
    }

    private int checkGameOver(ChessGame game) {
        if ((game.isInCheckmate(ChessGame.TeamColor.WHITE)) || (game.isInCheckmate(ChessGame.TeamColor.BLACK))) {
            return 1;
        }
        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            return 2;
        }
        return 0;
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            int gameID = command.getGameID();
            Object gameLock = CONNECTIONS.getGameLock(gameID);

            synchronized (gameLock) {
                AuthData authData = authDAO.getAuth(command.getAuthToken());
                GameData gameData = gameDAO.getGame(command.getGameID());
                validateAuthAndGame(authData, gameData);

                if (gameData.game().isGameOver()) {
                    throw new DataAccessException("Game has ended");
                }

                if (checkObserver(authData.username(), gameData)) {
                    throw new DataAccessException("Invalid Move");
                }

                if (!checkPieceColor(command.getMove().getStartPosition(), authData.username(), gameData)) {
                    throw new DataAccessException("Invalid Move");
                }

                gameData.game().makeMove(command.getMove());
                gameDAO.updateGame(gameData);
                CONNECTIONS.broadcast("", gameData.gameID(), new LoadGame(gameData.game()));

                var message = String.format("%s made a move.", authData.username());
                CONNECTIONS.broadcast(authData.username(), gameData.gameID(), new Notification(message));

                if (checkGameOver(gameData.game()) == 1) {
                    message = String.format("%s won the game!", authData.username());
                    CONNECTIONS.broadcast("", gameData.gameID(), new Notification(message));
                }
                if (checkGameOver(gameData.game()) == 2) {
                    CONNECTIONS.broadcast("", gameData.gameID(), new Notification("It's a standoff\\uD83E\\uDD20"));
                }
            }
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (InvalidMoveException e) {
            exceptionHandler("Invalid Move", session);
        } catch (IOException e) {
            try {
                CONNECTIONS.sendToUser(session, new ErrorMessage("Error: Internal Server Error"));
            } catch (IOException ignored) {}
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            Object gameLock = CONNECTIONS.getGameLock(gameID);

            synchronized (gameLock) {
                AuthData authData = authDAO.getAuth(command.getAuthToken());
                GameData gameData = gameDAO.getGame(command.getGameID());
                validateAuthAndGame(authData, gameData);

                var message = String.format("%s has left the game", authData.username());
                CONNECTIONS.broadcast(authData.username(), gameData.gameID(), new Notification(message));

                GameData updateGame;
                if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                    updateGame = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
                    updateGame = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                } else {
                    updateGame = gameData;
                }
                gameDAO.updateGame(updateGame);

                CONNECTIONS.remove(authData.username());
            }
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException e) {
            try {
                CONNECTIONS.sendToUser(session, new ErrorMessage("Error: Internal Server Error"));
            } catch (IOException ignored) {}
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            Object gameLock = CONNECTIONS.getGameLock(gameID);

            synchronized (gameLock) {
                AuthData authData = authDAO.getAuth(command.getAuthToken());
                GameData gameData = gameDAO.getGame(command.getGameID());
                validateAuthAndGame(authData, gameData);

                if (gameData.game().isGameOver()) {
                    throw new DataAccessException("Game Has Ended");
                }

                if (checkObserver(authData.username(), gameData)) {
                    throw new DataAccessException("Invalid Observer Action");
                }

                var message = String.format("%s resigned\nThanks for playing!", authData.username());
                CONNECTIONS.broadcast("", gameData.gameID(), new Notification(message));

                gameData.game().setGameOver(true);
                gameDAO.updateGame(gameData);
            }
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException e) {
            try {
                CONNECTIONS.sendToUser(session, new ErrorMessage("Error: Internal Server Error"));
            } catch (IOException ignored) {}
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        CONNECTIONS.remove(session);
        System.out.println("Closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error:");
        error.printStackTrace();
        CONNECTIONS.remove(session);
    }
}