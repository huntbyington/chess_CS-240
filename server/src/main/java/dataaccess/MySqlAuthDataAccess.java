package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            var insertAuthCommand = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            var insertAuthStatement = conn.prepareStatement(insertAuthCommand);

            insertAuthStatement.setString(1, authData.authToken());
            insertAuthStatement.setString(2, authData.username());

            insertAuthStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        PreparedStatement getAuthStatement = null;
        ResultSet results = null;

        try {
            var conn = getConnection();
            var getUserCommand = "SELECT authToken, username FROM auths WHERE authToken=?";
            getAuthStatement = conn.prepareStatement(getUserCommand);

            getAuthStatement.setString(1, authToken);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            results = getAuthStatement.executeQuery();

            results.next();
            var username = results.getString("username");

            return new AuthData(authToken, username);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            var conn = getConnection();
            var getAuthCommand = "DELETE FROM auths WHERE authToken=?";
            PreparedStatement getAuthStatement = conn.prepareStatement(getAuthCommand);

            getAuthStatement.setString(1, authToken);

            int results = getAuthStatement.executeUpdate();;

            if (results == 0) {
                throw new DataAccessException("Incorrect Authorization");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var conn = getConnection();
            var clearAuthsCommand = "DELETE FROM auths";
            var clearAuthsStatement = conn.prepareStatement(clearAuthsCommand);

            clearAuthsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
