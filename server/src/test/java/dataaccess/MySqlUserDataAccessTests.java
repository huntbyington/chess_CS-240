package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class MySqlUserDataAccessTests {

    @AfterAll
    static void clearUserDatabase() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDataAccess();
        userDAO.clear();
    }

    @Test
    @DisplayName("SQL Create User Test")
    public void sqlCreateUserTest() throws DataAccessException {
        // Test used for initial testing of createUser function
        UserDAO userDAO = new MySqlUserDataAccess();

        userDAO.createUser(new UserData("username", "password", "email"));

        assert true;
    }

    @Test
    @DisplayName("SQL Clear Users Test")
    public void sqlClearUsersTest() throws DataAccessException {
        // Test used for initial testing of clearUser function
        UserDAO userDAO = new MySqlUserDataAccess();

        userDAO.clear();

        assert true;
    }

    @Test
    @DisplayName("SQL Get User Test")
    public void sqlGetUserTest() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDataAccess();
        UserData userData = new UserData("username", "password", "email");

        userDAO.createUser(userData);

        UserData actual = userDAO.getUser(userData.username());

        assert Objects.equals(userData.username(), actual.username());
        assert Objects.equals(userData.email(), actual.email());
    }

    @Test
    @DisplayName("SQL Get Multiple Users Test")
    public void sqlGetMultipleUsersTest() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDataAccess();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        userData = new UserData("username2", "password2", "email2");
        userDAO.createUser(userData);

        UserData actual = userDAO.getUser("username");
        assert Objects.equals("username", actual.username());
        assert Objects.equals("email", actual.email());

        actual = userDAO.getUser("username2");
        assert Objects.equals("username2", actual.username());
        assert Objects.equals("email2", actual.email());
    }

    @Test
    @DisplayName("SQL Get Nonexistent User Test")
    public void sqlGetNonexistentUserTest() {
        try {
            UserDAO userDAO = new MySqlUserDataAccess();

            userDAO.getUser("fakeUser");

            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }
}
