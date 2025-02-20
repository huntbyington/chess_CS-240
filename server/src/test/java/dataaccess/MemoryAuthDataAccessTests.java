package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class MemoryAuthDataAccessTests {

    @Test
    @DisplayName("Check Memory Get One AuthData")
    public void checkMemoryGetOneAuthData() throws DataAccessException {
        String authToken = "1234";
        String username = "name";
        AuthData expected = new AuthData(authToken, username);

        MemoryAuthDataAccess myAuths = new MemoryAuthDataAccess();
        myAuths.createAuth(expected);
        AuthData actual = myAuths.getAuth(authToken);

        assert (Objects.equals(actual, expected));

    }

    @Test
    @DisplayName("Check Memory Get Two AuthData")
    public void checkMemoryGetTwoAuthData() throws DataAccessException {
        String authToken1 = "1234";
        String username1 = "name";
        AuthData expected1 = new AuthData(authToken1, username1);

        String authToken2 = "5678";
        String username2 = "hunter";
        AuthData expected2 = new AuthData(authToken2, username2);

        MemoryAuthDataAccess myAuths = new MemoryAuthDataAccess();
        myAuths.createAuth(expected1);
        myAuths.createAuth(expected2);

        AuthData actual1 = myAuths.getAuth(authToken1);
        AuthData actual2 = myAuths.getAuth(authToken2);

        assert (Objects.equals(actual1, expected1));
        assert (Objects.equals(actual2, expected2));

    }

    @Test
    @DisplayName("Check Memory Get Two AuthData Fail")
    public void checkMemoryGetTwoAuthDataFail() throws DataAccessException {
        String authToken1 = "1234";
        String username1 = "name";
        AuthData expected1 = new AuthData(authToken1, username1);

        String authToken2 = "5678";
        String username2 = "hunter";
        AuthData expected2 = new AuthData(authToken2, username2);

        MemoryAuthDataAccess myAuths = new MemoryAuthDataAccess();
        myAuths.createAuth(expected1);
        myAuths.createAuth(expected2);

        AuthData actual1 = myAuths.getAuth(authToken1);
        AuthData actual2 = myAuths.getAuth(authToken2);

        boolean case1 = (Objects.equals(actual1, expected2));
        boolean case2 = (Objects.equals(actual2, expected1));

        assert !(case1 || case2);

    }

    @Test
    @DisplayName("Check Memory Delete AuthData")
    public void checkMemoryDeleteAuthData() throws DataAccessException {
        String authToken1 = "1234";
        String username1 = "name";
        AuthData expected1 = new AuthData(authToken1, username1);

        String authToken2 = "5678";
        String username2 = "hunter";
        AuthData expected2 = new AuthData(authToken2, username2);

        MemoryAuthDataAccess myAuths = new MemoryAuthDataAccess();
        myAuths.createAuth(expected1);
        myAuths.createAuth(expected2);

        myAuths.deleteAuth(authToken1);
        AuthData actual = myAuths.getAuth(authToken1);

        assert (Objects.equals(actual, null));

    }

    @Test
    @DisplayName("Check Memory Clear AuthData")
    public void checkMemoryClearAuthData() throws DataAccessException {
        String authToken1 = "1234";
        String username1 = "name";
        AuthData expected1 = new AuthData(authToken1, username1);

        String authToken2 = "5678";
        String username2 = "hunter";
        AuthData expected2 = new AuthData(authToken2, username2);

        MemoryAuthDataAccess myAuths = new MemoryAuthDataAccess();
        myAuths.createAuth(expected1);
        myAuths.createAuth(expected2);

        myAuths.clear();
        AuthData actual1 = myAuths.getAuth(authToken1);
        AuthData actual2 = myAuths.getAuth(authToken2);

        assert (Objects.equals(actual1, null));
        assert (Objects.equals(actual2, null));

    }
}
