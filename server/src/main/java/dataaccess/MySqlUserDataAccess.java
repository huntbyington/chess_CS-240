package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static dataaccess.DatabaseManager.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


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
        return null;
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
