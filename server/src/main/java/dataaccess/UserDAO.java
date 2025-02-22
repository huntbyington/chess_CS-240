package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    void createUser(UserData userData) throws DataAccessException;;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;;

}
