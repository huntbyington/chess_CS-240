package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDataAccess implements GameDAO{

    final private HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataMap.get(gameID);
    }

    @Override
    public Collection<GameData> listGames(String username) throws DataAccessException {
        return new ArrayList<>(gameDataMap.values());
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataMap.clear();
    }
}
