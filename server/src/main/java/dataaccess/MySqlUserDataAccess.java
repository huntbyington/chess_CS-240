package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static dataaccess.DatabaseManager.*;


public class MySqlUserDataAccess implements UserDAO{

    public MySqlUserDataAccess() {
        try {
            createDatabase();

            var conn = getConnection();
            var createTableCommand = """
                            CREATE TABLE IF NOT EXISTS users (
                            username VARCHAR(255) NOT NULL PRIMARY KEY,
                            password VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NULL
                            );
                            """;
            var createTableStatement = conn.prepareStatement(createTableCommand);
            createTableStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try {
            getUser(userData.username());

            throw new DataAccessException("Username already exists");
        } catch (DataAccessException ignored) {
        }

        try {
            var conn = getConnection();
            var insertUserCommand = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            var insertUserStatement = conn.prepareStatement(insertUserCommand);

            insertUserStatement.setString(1, userData.username());
            insertUserStatement.setString(2, hashPassword(userData.password()));
            insertUserStatement.setString(3, userData.email());

            insertUserStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        PreparedStatement getUserStatement = null;
        ResultSet results = null;

        try {
            var conn = getConnection();
            var getUserCommand = "SELECT username, password, email FROM users WHERE username=?";
            getUserStatement = conn.prepareStatement(getUserCommand);

            getUserStatement.setString(1, username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            results = getUserStatement.executeQuery();

            results.next();
            var password = results.getString("password");
            var email = results.getString("email");

            return new UserData(username, password, email);
        } catch (SQLException e) {
            throw new DataAccessException("Username doesn't exist");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var conn = getConnection();
            var clearUsersCommand = "DELETE FROM users";
            var clearUsersStatement = conn.prepareStatement(clearUsersCommand);

            clearUsersStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
