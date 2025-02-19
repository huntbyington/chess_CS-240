package dataaccess;

import java.util.Collection;
import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    void createGame(GameData gameData) throws DataAccessException;;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;;

}
