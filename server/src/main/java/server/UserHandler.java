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

    public static Object register(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData;
            authData = userService.register(user);

            if (Objects.equals(user.username(), "") || Objects.equals(user.password(), "")) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            res.status(200);
            return "{ \"username\":\"" + user.username() + "\", \"authToken\":\"" + authData.authToken() + "\" }";
        } catch (DataAccessException e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
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
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    public void clear() throws DataAccessException {
        userService.clear();
    }

}
