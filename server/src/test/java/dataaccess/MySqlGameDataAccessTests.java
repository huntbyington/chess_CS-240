package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class MySqlGameDataAccessTests {

    @AfterAll
    static void clearGameDatabase() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();
        gameDAO.clear();
    }

    @Test
    @DisplayName("SQL Create Game Test")
    public void sqlCreateGameTest() throws DataAccessException {
        GameDAO gameDAO = new MySqlGameDataAccess();

        GameData gameData = new GameData(1, "whiteUser", "blackUser", "newGame", new ChessGame());

        gameDAO.createGame(gameData);

        assert true;
    }

    @Test
    @DisplayName("SQL Clear Games Test")
    public void sqlClearGamesTest() throws DataAccessException {
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

        assert gameData.gameID() == actual.gameID();
        assert Objects.equals(gameData.whiteUsername(), actual.whiteUsername());
        assert Objects.equals(gameData.blackUsername(), actual.blackUsername());
        assert Objects.equals(gameData.gameName(), actual.gameName());
        assert gameData.game().equals(actual.game());
    }
}
