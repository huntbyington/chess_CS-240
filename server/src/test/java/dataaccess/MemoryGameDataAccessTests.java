package passoff.dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemoryGameDataAccessTests {

    @BeforeEach
    public void setUp() throws DataAccessException {
        MemoryGameDataAccess myGames = new MemoryGameDataAccess();

        int gameID = 1234;
        String whiteUsername = "name0";
        String blackUsername = "name1";
        String gameName = "Game";
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        myGames.createGame(gameData);

        gameID = 5678;
        whiteUsername = "name0";
        blackUsername = "name1";
        gameName = "Game";
        game = new ChessGame();
        GameData gameData1 = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        myGames.createGame(gameData1);
    }

    @Test
    @DisplayName("Get Game Test")
    public void getGameTest() {
        GameData expected = gameData;
    }

}
