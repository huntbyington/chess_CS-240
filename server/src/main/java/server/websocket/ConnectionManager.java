package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, int gameID, Session session) {
        var connection = new Connection(visitorName, gameID, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }
    public void remove(Session session) {
        for(var c : connections.values()) {
            if (c.session == session) {
                connections.remove(c.visitorName);
            }
        }
    }

    public void broadcast(String excludeVisitorName, int gameID, ServerMessage notification) throws IOException {
        var json = new Gson().toJson(notification);
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName) && c.gameID == gameID) {
                    System.out.println(json);
                    c.send(json);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void sendToUser(Session session, ServerMessage notification) throws IOException {
        if (session.isOpen()) {
            var json = new Gson().toJson(notification);
            session.getRemote().sendString(json);
        }
    }

    public void sendError(Session session, String errorMessage) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(errorMessage)));
        }
    }

    public void removeGameConnections(int gameID) {
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID == gameID) {
                    removeList.add(c);
                }
            } else {
                // Clean up any connections that were left open.
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}