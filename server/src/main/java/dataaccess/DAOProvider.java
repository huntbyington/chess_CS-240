package dataaccess;

public class DAOProvider {
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    public static void init(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        DAOProvider.userDAO = userDAO;
        DAOProvider.gameDAO = gameDAO;
        DAOProvider.authDAO = authDAO;
    }

    public static UserDAO getUserDAO() { return userDAO; }
    public static GameDAO getGameDAO() { return gameDAO; }
    public static AuthDAO getAuthDAO() { return authDAO; }
}