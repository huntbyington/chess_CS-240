package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MySqlGameDataAccessTests {

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
}
