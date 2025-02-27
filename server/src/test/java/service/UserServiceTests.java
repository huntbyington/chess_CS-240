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

    @Test
    @DisplayName("Login Test")
    public void loginTest() {
        try {
            UserData user = new UserData("username", "password", "email");
            userService.register(user);

            AuthData authData = userService.login(user);

            assert Objects.equals(authData.username(), user.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Login Test Wrong Password")
    public void loginTestWrongPass() {
        try {
            UserData user = new UserData("username", "password", "email");
            userService.register(user);

            UserData newLogin = new UserData("username", "wrongPass", "email");
            AuthData authData = userService.login(newLogin);

            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }

    @Test
    @DisplayName("Login Test No Email")
    public void loginTestNoEmail() {
        try {
            UserData user = new UserData("username", "password", "email");
            userService.register(user);

            UserData newLogin = new UserData("username", "password", "");
            AuthData authData = userService.login(newLogin);

            assert Objects.equals(authData.username(), user.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Logout Test")
    public void logoutTest() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        AuthData authData = userService.register(user);

        userService.logout(authData.authToken());

        AuthData actual = memoryAuthDataAccess.getAuth(authData.authToken());
        assert actual == null;
    }

    @Test
    @DisplayName("Logout Test Invalid AuthToken")
    public void logoutTestInvalidAuthToken() {
        try {
            UserData user = new UserData("username", "password", "email");
            AuthData authData = userService.register(user);

            userService.logout(authData.authToken());

            userService.logout(authData.authToken());

            assert false;
        } catch (DataAccessException e) {
            assert true;
        }
    }
}
