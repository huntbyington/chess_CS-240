package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        userDAO.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData queryUser = userDAO.getUser(user.username());

        if (Objects.equals(queryUser.username(), user.username())) {
            if (Objects.equals(queryUser.password(), user.password())) {
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(authToken, user.username());
                authDAO.createAuth(authData);
                return authData;
            }
        }

        throw new DataAccessException("Incorrect Username or Password");
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }
}
