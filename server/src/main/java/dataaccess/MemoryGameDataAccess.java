package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.*;

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
    public HashSet<GameData> listGames(String username) throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        for (GameData gameData : gameDataMap.values()) {
            if (Objects.equals(gameData.blackUsername(), username) || Objects.equals(gameData.whiteUsername(), username)) {
                gameList.add(gameData);
            }
        }
        return gameList;
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
