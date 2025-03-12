package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MySqlGameDataAccessTests {

    @AfterEach
    void clearGameDatabase() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();
        gameDAO.clear();
    }

    @Test
    @DisplayName("SQL Create Game Test")
    public void sqlCreateGameTest() throws DataAccessException {
        // Test used for initial testing of createGame function
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", new ChessGame());

        gameDAO.createGame(gameData);

        assert true;
    }

    @Test
    @DisplayName("SQL Clear Games Test")
    public void sqlClearGamesTest() throws DataAccessException {
        // Test used for initial testing of clear function
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = new GameData(2, "whiteUser", "blackUser", "newGame", new ChessGame());
        gameDAO.createGame(gameData);

        gameDAO.clear();

        assert true;
    }

    @Test
    @DisplayName("SQL Get Game Test")
    public void sqlGetGameTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", new ChessGame());
        gameDAO.createGame(gameData);

        GameData actual = gameDAO.getGame(1);

        assert gameData.gameID() == actual.gameID();
        assert Objects.equals(gameData.whiteUsername(), actual.whiteUsername());
        assert Objects.equals(gameData.blackUsername(), actual.blackUsername());
        assert Objects.equals(gameData.gameName(), actual.gameName());
        assert gameData.game().equals(actual.game());
    }

    @Test
    @DisplayName("SQL Get Multiple Games Test")
    public void sqlGetMultipleGamesTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", new ChessGame());
        gameDAO.createGame(gameData);
        gameData = new GameData(2, "whiteUser2", "blackUser2", "newGame2", new ChessGame());
        gameDAO.createGame(gameData);

        GameData actual = gameDAO.getGame(2);

        assert gameData.equals(actual);
    }

    @Test
    @DisplayName("SQL Get Nonexistent Game Test")
    public void sqlGetNonexistentGameTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = gameDAO.getGame(1);

        assert gameData == null;
    }

    @Test
    @DisplayName("SQL List Games Test")
    public void sqlListGamesTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        Collection<GameData> expected = new ArrayList<>();

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", new ChessGame());
        expected.add(gameData);
        gameDAO.createGame(gameData);

        gameData = new GameData(2, "whiteUser2", "blackUser2", "newGame2", new ChessGame());
        expected.add(gameData);
        gameDAO.createGame(gameData);

        Collection<GameData> actual = gameDAO.listGames("whiteUser");

        int correct = 0;
        for (GameData expectedGame : expected) {
            for (GameData actualGame : actual) {
                if (expectedGame.equals(actualGame)) {
                    correct++;
                }
            }
        }

        assert correct == 2;
    }

    @Test
    @DisplayName("SQL List Games Empty List Test")
    public void sqlListGamesEmptyListTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        Collection<GameData> expected = new ArrayList<>();

        assert expected.equals(gameDAO.listGames("username"));
    }

    @Test
    @DisplayName("SQL Update Games Test")
    public void sqlUpdateGamesTest() throws DataAccessException {
        try {
            GameDAO gameDAO = new MySqlGameDataAccess();

            ChessGame myGame = new ChessGame();

            GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", myGame);
            gameDAO.createGame(gameData);

            myGame.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(3,1), null));

            gameDAO.updateGame(new GameData(1, "whiteUser", "blackUser", "newGame", myGame));

            assert myGame.equals(gameDAO.getGame(1).game());
        } catch (InvalidMoveException e) {
            assert false : "Invalid Move";
        }
    }
}
