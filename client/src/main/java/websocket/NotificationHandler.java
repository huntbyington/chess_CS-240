package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void loadGame(LoadGame loadGame);
    void error(ErrorMessage message);
    void notify(Notification notification);
}
