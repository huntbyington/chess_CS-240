package server.websocket;

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

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebsocketHandler() {
        this.userDAO = DAOProvider.getUserDAO();
        this.gameDAO = DAOProvider.getGameDAO();
        this.authDAO = DAOProvider.getAuthDAO();
    }

    private static void exceptionHandler(String eMessage, Session session) {
        switch (eMessage) {
            case "Incorrect Authorization", "Incorrect Username or Password" -> {
                sendError(session, "Error: unauthorized");
            }
            case "Invalid Game ID", "Invalid Player Color", "Invalid Game Name" -> {
                sendError(session, "Error: bad request");
            }
            case "Invalid move" -> {
                sendError(session, "Error: Invalid move");
            }
            case null, default -> {
                sendError(session, ("Error: " + eMessage));
            }
        }
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


            var message = String.format("%s joined the game", authData.username());
            sendNotification(session, message);
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            System.out.println("Making move");
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new DataAccessException("Incorrect Authorization");
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new DataAccessException("Invalid Game ID");
            }

            System.out.println(command.getMove());
            gameData.game().makeMove(command.getMove());
            gameDAO.updateGame(gameData);
            System.out.println("Debug line 100");
            sendLoadGame(session, gameData.game());

            System.out.println("Debug line 103");
            var message = String.format("%s made a move.", authData.username());
            sendNotification(session, message);
        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        } catch (InvalidMoveException e) {
            exceptionHandler("Invalid Move", session);
        }
    }

    private static void sendError(Session session, String errorMessage) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage(errorMessage)));
                session.close();
            }
        } catch (IOException ignored) {}
    }

    private static void sendLoadGame(Session session, ChessGame game) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(new Gson().toJson(new Notification(game.toString())));
                session.close();
            }
        } catch (IOException ignored) {}
    }

    private static void sendNotification(Session session, String notifMessage) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(new Gson().toJson(new Notification(notifMessage)));
                session.close();
            }
        } catch (IOException ignored) {}
    }

}
