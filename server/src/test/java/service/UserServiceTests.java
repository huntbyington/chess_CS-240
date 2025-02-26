package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class UserServiceTests {

    MemoryUserDataAccess memoryUserDataAccess = new MemoryUserDataAccess();
    MemoryAuthDataAccess memoryAuthDataAccess = new MemoryAuthDataAccess();
    UserService userService = new UserService(memoryUserDataAccess, memoryAuthDataAccess);

    @Test
    @DisplayName("Register Test")
    public void registerTest() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        AuthData authData = userService.register(user);

        assert Objects.equals(authData.username(), user.username());
    }

    @Test
    @DisplayName("Register Test Existent User")
    public void registerTestExUser() {
        try {
            UserData user = new UserData("username", "password", "email");
            AuthData authData = userService.register(user);

            userService.register(user);
            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }

}
