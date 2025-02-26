package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    static UserService userService;

    public static Object register(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData;
            authData = userService.register(user);

            res.status(200);
            return "{ \"username\":" + user.username() + ", \"authToken\":" + authData.authToken() + " }";
        } catch (DataAccessException e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }

}
