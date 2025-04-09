package websocket;

import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void loadGame(LoadGame loadGame);
    void notify(ServerMessage notification);
}
