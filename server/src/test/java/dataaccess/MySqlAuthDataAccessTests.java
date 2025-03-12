package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MySqlAuthDataAccessTests {

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
}
