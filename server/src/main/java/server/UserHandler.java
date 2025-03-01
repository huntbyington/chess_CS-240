package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class UserHandler {

    static UserService userService;

    public UserHandler(UserService userService) {
        UserHandler.userService = userService;
    }

    private static String exceptionHandler(String eMessage, Response res) {
        switch (eMessage) {
            case "Invalid Username or Password" -> {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
            case "Incorrect Authorization", "Incorrect Username or Password" -> {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }
            case "Username already exists" -> {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }
            case null, default -> {
                res.status(500);
                return "{ \"message\": \"Error: " + eMessage + "\" ";
            }
        }
    }

    public static Object register(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData;
            authData = userService.register(user);

            res.status(200);
            return "{ \"username\":\"" + user.username() + "\", \"authToken\":\"" + authData.authToken() + "\" }";
        } catch (DataAccessException e) {
            return exceptionHandler(e.getMessage(), res);
        }
    }

    public static Object login(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData;
            authData = userService.login(user);

            res.status(200);
            return "{ \"username\":\"" + user.username() + "\", \"authToken\":\"" + authData.authToken() + "\" }";
        } catch (DataAccessException e) {
            return exceptionHandler(e.getMessage(), res);
        }
    }

    public static Object logout(Request req, Response res) {
        try {
            String logoutToken = req.headers("authorization");
            userService.logout(logoutToken);

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            return exceptionHandler(e.getMessage(), res);
        }
    }

    public void clear() throws DataAccessException {
        userService.clear();
    }

}
