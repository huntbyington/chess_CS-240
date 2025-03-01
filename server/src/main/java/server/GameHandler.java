package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;

public class GameHandler {

    static GameService gameService;

    public GameHandler(GameService gameService) {
        GameHandler.gameService = gameService;
    }

    private static String exceptionHandler(String eMessage, Response res) {
        switch (eMessage) {
            case "Invalid Game ID", "Invalid Player Color", "Invalid Game Name" -> {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
            case "Incorrect Authorization" -> {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }
            case "Player Color Already Taken" -> {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }
            case null, default -> {
                res.status(500);
                return "{ \"message\": \"Error: " + eMessage + "\" ";
            }
        }
    }

    private record GameListObject(int gamID, String whiteUsername, String blackUsername, String gameName) {}

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
            return exceptionHandler(e.getMessage(), res);
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
            return exceptionHandler(e.getMessage(), res);
        }
    }

    private record JoinGameData(String playerColor, int gameID) {}

    public static Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            JoinGameData joinGameData = new Gson().fromJson(req.body(), JoinGameData.class);
            String playerColor = joinGameData.playerColor();
            int gameID = joinGameData.gameID();

            gameService.joinGame(authToken, playerColor, gameID);

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            return exceptionHandler(e.getMessage(), res);
        }
    }

    public void clear() throws DataAccessException {
        gameService.clear();
    }
}
