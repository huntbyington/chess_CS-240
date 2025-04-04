package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDataAccess implements UserDAO {

    final private HashMap<String, UserData> userDataMap = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("Username already exists");
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataMap.get(username);
    }

    @Override
    public boolean checkPassword(String userPassword, String dbPassword) {
        return userPassword.equals(dbPassword);
    }

    @Override
    public void clear() throws DataAccessException {
        userDataMap.clear();
    }
}
