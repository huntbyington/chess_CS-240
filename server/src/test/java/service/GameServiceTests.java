package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryGameDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class GameServiceTests {

    MemoryGameDataAccess memoryGameDataAccess = new MemoryGameDataAccess();
    MemoryAuthDataAccess memoryAuthDataAccess = new MemoryAuthDataAccess();
    GameService gameService = new GameService(memoryGameDataAccess, memoryAuthDataAccess);

    @Test
    @DisplayName("Game List Test Empty List")
    public void gameListTestEmptyList() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        Collection<GameData> actualGameList = gameService.listGames("authToken");

        Collection<GameData> expectedGameList = new ArrayList<>();
        
        assert expectedGameList.equals(actualGameList);
    }

}
