package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
    boolean checkPassword(String userPassword, String dbPassword);
    void clear() throws DataAccessException;

}
