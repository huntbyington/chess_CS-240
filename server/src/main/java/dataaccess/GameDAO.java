package dataaccess;

import java.util.Collection;
import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;

}
