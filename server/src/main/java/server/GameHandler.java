package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class GameHandler {

    static GameService gameService;

    public GameHandler(GameService gameService) {
        GameHandler.gameService = gameService;
    }

    public static Object listGames(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            Collection<GameData> gameList = gameService.listGames(authToken);

            String jsonReturn = new Gson().toJson(gameList);

            res.status(200);
            return "{\"games\": " + jsonReturn + "}";
        } catch (DataAccessException e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    public void clear() throws DataAccessException {
        gameService.clear();
    }
}
