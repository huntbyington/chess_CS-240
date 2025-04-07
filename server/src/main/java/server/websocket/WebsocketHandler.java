package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
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
            case null, default -> {
                sendError(session, ("Error: " + eMessage));
            }
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            connections.add(authData.username(), command.getGameID(), session);

        } catch (DataAccessException e) {
            exceptionHandler(e.getMessage(), session);
        }

    }

    private static void sendError(Session session, String errorMessage) {
        try {
            if (session.isOpen()) {
                System.out.print("Flag: line 65");
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage(errorMessage)));
                session.close();
            }
        } catch (IOException ignored) {}
    }

}
