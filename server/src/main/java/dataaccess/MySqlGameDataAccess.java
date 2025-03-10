package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class MySqlGameDataAccess implements GameDAO{

    public MySqlGameDataAccess() {
        try {
            createDatabase();

            var conn = getConnection();
            var createTableCommand = """
                            CREATE TABLE IF NOT EXISTS games (
                            gameID INT NOT NULL PRIMARY KEY,
                            whiteUsername VARCHAR(255) NOT NULL,
                            blackUsername VARCHAR(255) NOT NULL,
                            gameName VARCHAR(255) NOT NULL,
                            game longtext NOT NULL
                            );
                            """;
            var createTableStatement = conn.prepareStatement(createTableCommand);
            createTableStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames(String username) throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
