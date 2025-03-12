package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class MySqlAuthDataAccess implements AuthDAO{

    public MySqlAuthDataAccess() {
        try {
            createDatabase();

            var conn = getConnection();
            var createTableCommand = """
                            CREATE TABLE IF NOT EXISTS auths (
                            authToken  VARCHAR(255) NOT NULL PRIMARY KEY,
                            username VARCHAR(255) NOT NULL
                            );
                            """;
            var createTableStatement = conn.prepareStatement(createTableCommand);
            createTableStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try {
            var conn = getConnection();
            var insertGameCommand = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            var insertGameStatement = conn.prepareStatement(insertGameCommand);

            insertGameStatement.setString(1, authData.authToken());
            insertGameStatement.setString(2, authData.username());

            insertGameStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
