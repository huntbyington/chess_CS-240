package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class GameHandler {

    static GameService gameService;

    public GameHandler(GameService gameService) {
        GameHandler.gameService = gameService;
    }

    public record GameListObject(int gamID, String whiteUsername, String blackUsername, String gameName) {}

    public static Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            Collection<GameData> gameList = gameService.listGames(authToken);

            Collection<GameListObject> formatGameList = new ArrayList<>();
            for (GameData game : gameList) {
                formatGameList.add(new GameListObject(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }

            String jsonReturn = new Gson().toJson(formatGameList);

            res.status(200);
            return "{\"games\": " + jsonReturn + "}";
        } catch (DataAccessException e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    private record GameName(String gameName) {}

    public static Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            String gameName = new Gson().fromJson(req.body(), GameName.class).gameName;
            int gameId = gameService.createGame(authToken, gameName);

            res.status(200);
            return "{ \"gameID\": " + gameId + " }";
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Incorrect Authorization")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            } else {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
        }
    }

    public void clear() throws DataAccessException {
        gameService.clear();
    }
}
