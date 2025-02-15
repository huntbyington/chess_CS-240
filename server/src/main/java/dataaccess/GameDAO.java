package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    GameData createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    GameData getGame(int gameID);
    GameData listGames();
    GameData updateGame(int gameID);
    GameData clear(int gameID);

}
