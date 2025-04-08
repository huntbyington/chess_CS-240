package server.websocket;

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

    private static final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebsocketHandler() {
        this.userDAO = DAOProvider.getUserDAO();
        this.gameDAO = DAOProvider.getGameDAO();
        this.authDAO = DAOProvider.getAuthDAO();
    }

    private static void exceptionHandler(String eMessage, Session session) {
        try {
            switch (eMessage) {
                case "Incorrect Authorization", "Incorrect Username or Password" -> {
                    connections.sendError(session, "Error: unauthorized");
                }
                case "Invalid Game ID", "Invalid Player Color", "Invalid Game Name" -> {
                    connections.sendError(session, "Error: bad request");
                }
                case "Invalid move" -> {
                    connections.sendError(session, "Error: Invalid move");
                }
                case null, default -> {
                    connections.sendError(session, ("Error: " + eMessage));
                }
            }
        } catch (IOException ignored) {}
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received");
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println(command.getCommandType());
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
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            validateAuthAndGame(authData, gameData);

            connections.add(authData.username(), command.getGameID(), session);

            connections.sendToUser(session, new LoadGame(gameData.game()));

            var message = String.format("%s joined the game", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException ignored) {
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            validateAuthAndGame(authData, gameData);

            gameData.game().makeMove(command.getMove());
            gameDAO.updateGame(gameData);
            connections.broadcast("", gameData.gameID(), new LoadGame(gameData.game()));

            var message = String.format("%s made a move.", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (InvalidMoveException e) {
            exceptionHandler("Invalid Move", session);
        } catch (IOException e) {
            try {
                connections.sendError(session, "Error: Internal Server Error");
            } catch (IOException ignored) {}
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            validateAuthAndGame(authData, gameData);

            var message = String.format("%s has left the game", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));

            GameData updateGame;
            if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                updateGame = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
                updateGame = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            } else {
                updateGame = gameData;
            }
            gameDAO.updateGame(updateGame);
            connections.broadcast("", gameData.gameID(), new LoadGame(updateGame.game()));

            connections.remove(authData.username());
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException e) {
            try {
                connections.sendError(session, "Error: Internal Server Error");
            } catch (IOException ignored) {}
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());
            validateAuthAndGame(authData, gameData);

            var message = String.format("%s resigned\nThanks for playing!", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));

            // Broadcast and empty board to indicate that the game is over
            connections.broadcast(authData.username(), gameData.gameID(), new LoadGame(null));

            gameDAO.deleteGame(gameData.gameID());
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException e) {
            try {
                connections.sendError(session, "Error: Internal Server Error");
            } catch (IOException ignored) {}
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
        System.out.println("Closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error:");
        error.printStackTrace();
        connections.remove(session);
    }
}