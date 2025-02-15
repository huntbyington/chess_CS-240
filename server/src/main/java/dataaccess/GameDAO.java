package dataaccess;

import java.util.Collection;
import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(int gameID) throws DataAccessException;
    void clear();

}
