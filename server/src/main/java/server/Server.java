package server;

import dataaccess.*;
import service.*;
import spark.*;

public class Server {

    final UserDAO userDAO;
    final GameDAO gameDAO;
    final AuthDAO authDAO;

    final UserService userService;
    final UserHandler userHandler;

    final GameService gameService;
    final GameHandler gameHandler;


    public Server() {
        authDAO = new MySqlAuthDataAccess();

        userDAO = new MySqlUserDataAccess();
        userService = new UserService(userDAO, authDAO);
        userHandler = new UserHandler(userService);

        gameDAO = new MySqlGameDataAccess();
        gameService = new GameService(gameDAO, authDAO);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);

        Spark.post("/user", UserHandler::register);
        Spark.post("/session", UserHandler::login);
        Spark.delete("/session", UserHandler::logout);

        Spark.get("/game", GameHandler::listGames);
        Spark.post("/game", GameHandler::createGame);
        Spark.put("/game", GameHandler::joinGame);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        userHandler.clear();
        gameHandler.clear();

        res.status(200);
        return "{}";
    }
}
