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
import java.util.Objects;

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

    @Test
    @DisplayName("Create Game Test")
    public void createGameTest() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        int gameId = gameService.createGame("authToken", "myGame");

        assert gameId == 0;
    }

    @Test
    @DisplayName("Create Game ID Incrementation Test")
    public void createGameIdIncTest() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        int gameId = gameService.createGame("authToken", "myGame");

        assert gameId == 0;

        gameId = gameService.createGame("authToken", "myNewGame");

        assert gameId == 1;
    }

    @Test
    @DisplayName("Create Game ID Incrementation with Multiple Users Test")
    public void createGameIdIncMultUsersTest() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        int gameId = gameService.createGame("authToken", "myGame");

        assert gameId == 0;

        memoryAuthDataAccess.createAuth(new AuthData("authToken2", "username2"));
        gameId = gameService.createGame("authToken2", "myNewGame");

        assert gameId == 1;
    }

    @Test
    @DisplayName("Create Game Invalid Auth Token")
    public void createGameInvalidAuthToken() {
        try {
            gameService.createGame("badToken", "myGame");

            assert false;
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Incorrect Authorization")) {
                assert true;
            } else {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Create Game Invalid Game Name")
    public void createGameInvalidGameName() {
        try {
            memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
            gameService.createGame("authToken", "");

            assert false;
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Invalid Game Name")) {
                assert true;
            } else {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Create and List Games Single User")
    public void createAndListGamesSingleUser() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        gameService.createGame("authToken", "myGame");
        gameService.createGame("authToken", "myNewGame");

        Collection<GameData> gameList = gameService.listGames("authToken");

        for (GameData game : gameList) {
            // Assert returns false if expected game names are not in gameList
            assert Objects.equals(game.gameName(), "myGame") || Objects.equals(game.gameName(), "myNewGame");
        }
    }

    @Test
    @DisplayName("Create and List Games Multiple Users")
    public void createAndListGamesMultUsers() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        memoryAuthDataAccess.createAuth(new AuthData("authToken2", "username2"));
        gameService.createGame("authToken", "myGame");
        gameService.createGame("authToken2", "myGame2");

        Collection<GameData> gameList = gameService.listGames("authToken");

        for (GameData game : gameList) {
            // Assert returns false if expected game names are not in gameList
            assert Objects.equals(game.gameName(), "myGame");
        }
    }

    @Test
    @DisplayName("Join Game Correct User")
    public void joinGameCorrectUser() throws DataAccessException {
        memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
        memoryAuthDataAccess.createAuth(new AuthData("authToken2", "username2"));
        int gameID_0 = gameService.createGame("authToken", "myGame");
        int gameID_1 = gameService.createGame("authToken", "myGame2");

        if (Objects.equals(memoryGameDataAccess.getGame(gameID_0).whiteUsername(), "")) {
            gameService.joinGame("authToken2", "WHITE", gameID_0);
            assert Objects.equals(memoryGameDataAccess.getGame(gameID_0).whiteUsername(), "username2");
        } else {
            gameService.joinGame("authToken2", "BLACK", gameID_0);
            assert  Objects.equals(memoryGameDataAccess.getGame(gameID_0).blackUsername(), "username2");
        }
    }

    @Test
    @DisplayName("Join Game Incorrect User")
    public void joinGameIncorrectUser() {
        try {
            memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
            memoryAuthDataAccess.createAuth(new AuthData("authToken2", "username2"));
            int gameID_0 = gameService.createGame("authToken", "myGame");
            int gameID_1 = gameService.createGame("authToken", "myGame2");

            if (Objects.equals(memoryGameDataAccess.getGame(gameID_0).whiteUsername(), "")) {
                gameService.joinGame("authToken2", "BLACK", gameID_0);
            } else {
                gameService.joinGame("authToken2", "WHITE", gameID_0);
            }

            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("Join Game No Color")
    public void joinGameNoColor() {
        try {
            memoryAuthDataAccess.createAuth(new AuthData("authToken", "username"));
            memoryAuthDataAccess.createAuth(new AuthData("authToken2", "username2"));
            int gameID_0 = gameService.createGame("authToken", "myGame");
            int gameID_1 = gameService.createGame("authToken", "myGame2");

            gameService.joinGame("authToken2", "", gameID_0);

            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }
}
