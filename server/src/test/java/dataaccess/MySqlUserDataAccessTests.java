package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
