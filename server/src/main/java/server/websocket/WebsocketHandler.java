package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

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
        switch (command.getCommandType()) {
            case CONNECT -> {
                handleConnect(session, command);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                handleMakeMove(session, makeMoveCommand);
            }
            case RESIGN -> {
                handleResign(session, command);
            }
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new DataAccessException("Incorrect Authorization");
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new DataAccessException("Invalid Game ID");
            }

            connections.add(authData.username(), command.getGameID(), session);

            connections.broadcast(authData.username(), gameData.gameID(), new LoadGame(gameData.game()));

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
            if (authData == null) {
                throw new DataAccessException("Incorrect Authorization");
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new DataAccessException("Invalid Game ID");
            }

            gameData.game().makeMove(command.getMove());
            gameDAO.updateGame(gameData);
            connections.broadcast(authData.username(), gameData.gameID(), new LoadGame(gameData.game()));

            var message = String.format("%s made a move.", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (InvalidMoveException e) {
            exceptionHandler("Invalid Move", session);
        } catch (IOException ignore) {
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new DataAccessException("Incorrect Authorization");
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new DataAccessException("Invalid Game ID");
            }

            var message = String.format("%s resigned\nThanks for playing!", authData.username());
            connections.broadcast(authData.username(), gameData.gameID(), new Notification(message));

            // Broadcast and empty board to indicate that the game is over
            connections.broadcast(authData.username(), gameData.gameID(), new LoadGame(null));

            gameDAO.deleteGame(gameData.gameID());
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (IOException ignored) {}
    }
}