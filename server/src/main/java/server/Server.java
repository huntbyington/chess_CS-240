package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

public class Server {

    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;

    UserService userService;
    UserHandler userHandler;

    GameService gameService;
    GameHandler gameHandler;


    public Server() {
        authDAO = new MemoryAuthDataAccess();

        userDAO = new MemoryUserDataAccess();
        userService = new UserService(userDAO, authDAO);
        userHandler = new UserHandler(userService);

        gameDAO = new MemoryGameDataAccess();
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

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

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
