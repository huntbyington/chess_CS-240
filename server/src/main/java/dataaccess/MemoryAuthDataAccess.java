package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDataAccess implements AuthDAO {

    final private HashMap<String, AuthData> authTokenMap = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authTokenMap.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokenMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokenMap.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authTokenMap.clear();
    }
}
