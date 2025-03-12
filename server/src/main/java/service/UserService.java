package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    final UserDAO userDAO;
    final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if ((Objects.equals(user.username(), "") || Objects.equals(user.password(), "")) ||
                (Objects.equals(user.username(), null) || Objects.equals(user.password(), null))) {
            throw new DataAccessException("Invalid Username or Password");
        }

        userDAO.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData queryUser = userDAO.getUser(user.username());

        if (queryUser == null) {
            throw new DataAccessException("Incorrect Username or Password");
        }

        if (Objects.equals(user.username(), queryUser.username())) {
            if (checkPassword(user.password(), queryUser.password())) {
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(authToken, user.username());
                authDAO.createAuth(authData);
                return authData;
            }
        }

        throw new DataAccessException("Incorrect Username or Password");
    }

    public void logout(String logoutToken) throws DataAccessException {
        authDAO.deleteAuth(logoutToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    private boolean checkPassword(String userPassword, String dbPassword) {
        return BCrypt.checkpw(userPassword, dbPassword);
    }
}
