package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemoryUserDataAccessTests {
    UserData userData;
    UserData userData1;
    MemoryUserDataAccess myUsers = new MemoryUserDataAccess();

    @BeforeEach
    public void setUp() throws DataAccessException {
        String username = "user";
        String password = "pass";
        String email = "email";
        userData = new UserData(username, password, email);
        myUsers.createUser(userData);

        username = "name";
        password = "word";
        email = "bigmail";
        userData1 = new UserData(username, password, email);
        myUsers.createUser(userData1);
    }

    @Test
    @DisplayName("Get User Test")
    public void getUserTest() throws DataAccessException {
        UserData expected = userData;
        UserData actual = myUsers.getUser(userData.username());

        assert (expected.equals(actual));
    }

    @Test
    @DisplayName("Clear UserData Test")
    public void clearGameDataTest() throws DataAccessException {
        myUsers.clear();

        UserData expected = myUsers.getUser(userData.username());
        UserData expected1 = myUsers.getUser(userData1.username());

        assert expected == null;
        assert expected1 == null;
    }
}
