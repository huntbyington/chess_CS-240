package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDataAccessTests {

    GameData gameData;
    GameData gameData1;
    MemoryGameDataAccess myGames = new MemoryGameDataAccess();

    @BeforeEach
    public void setUp() throws DataAccessException {
        int gameID = 1234;
        String whiteUsername = "name0";
        String blackUsername = "name1";
        String gameName = "Game";
        ChessGame game = new ChessGame();
        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        myGames.createGame(gameData);

        gameID = 5678;
        whiteUsername = "name0";
        blackUsername = "name1";
        gameName = "Game";
        game = new ChessGame();
        gameData1 = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        myGames.createGame(gameData1);
    }

    @Test
    @DisplayName("Get Game Test")
    public void getGameTest() throws DataAccessException {
        GameData expected = gameData;
        GameData actual = myGames.getGame(gameData.gameID());

        assert (expected.equals(actual));
    }

    @Test
    @DisplayName("Get Game List Test")
    public void getGameListTest() throws DataAccessException {
        Collection<GameData> expected = new ArrayList<>();
        expected.add(gameData);
        expected.add(gameData1);

        Collection<GameData> actual = myGames.listGames();

        GameData[] expectedArray = expected.toArray(new GameData[0]);
        GameData[] actualArray = actual.toArray(new GameData[0]);

        for (int i = 0; i < expectedArray.length; i++) {
            assert (expectedArray[i].equals(actualArray[i]));
        }
    }

    @Test
    @DisplayName("Update GameData Test")
    public void updateGameDataTest() throws InvalidMoveException, DataAccessException {
        gameData.game().makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(3,1), null));
        ChessGame expected = gameData.game();

        myGames.updateGame(gameData);
        ChessGame actual = myGames.getGame(gameData.gameID()).game();

        assert (expected.equals(actual));

    }

    @Test
    @DisplayName("Clear GameData Test")
    public void clearGameDataTest() throws DataAccessException {
        myGames.clear();

        GameData expected = myGames.getGame(gameData.gameID());
        GameData expected1 = myGames.getGame(gameData1.gameID());

        assert expected == null;
        assert expected1 == null;
    }
}
