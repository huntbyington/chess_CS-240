package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MySqlAuthDataAccessTests {

    @AfterEach
    void clearUserDatabase() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDataAccess();
        authDAO.clear();
    }

    @Test
    @DisplayName("SQL Create Auth Test")
    public void sqlCreateAuthTest() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDataAccess();

        authDAO.createAuth(new AuthData("authToken", "username"));

        assert true;
    }

    @Test
    @DisplayName("SQL Clear Auths Test")
    public void sqlClearAuthsTest() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDataAccess();

        authDAO.createAuth(new AuthData("authToken", "username"));

        authDAO.clear();

        assert true;
    }

    @Test
    @DisplayName("SQL Get Auth Test")
    public void sqlGetAuthTest() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDataAccess();

        AuthData authData = new AuthData("authToken", "username");
        authDAO.createAuth(authData);

        AuthData actual = authDAO.getAuth("authToken");

        assert authData.equals(actual);
    }

    @Test
    @DisplayName("SQL Get Nonexistent Auth Test")
    public void sqlGetNonexistentAuthTest() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDataAccess();

        AuthData authData = authDAO.getAuth("authToken");

        assert authData == null;
    }
}
