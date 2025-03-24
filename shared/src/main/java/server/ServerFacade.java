package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    /* UserHandler Requests */
    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        UserData user = new UserData(username, password, email);
        AuthData authData = this.makeRequest("POST", path, user, AuthData.class);
        authToken = authData.authToken();
        return authData;
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        UserData user = new UserData(username, password);
        AuthData authData = this.makeRequest("POST", path, user, AuthData.class);
        this.authToken = authData.authToken();
        return authData;
    }

    public void logout() throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(401, "Error: Not authenticated");
        }

        var path = "/session";
        this.makeRequest("DELETE", path, null, null);

        this.authToken = null;
    }

    /* GameHandler Requests */
    public record GameListObject(int gameID, String whiteUsername, String blackUsername, String gameName) {}
    record listGamesResponse(Collection<GameListObject> games) {}

    public Collection<GameListObject> listGames() throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, null, listGamesResponse.class);

        return response.games();
    }

    private record GameName(String gameName) {}

    public void createGame(String gameName) throws ResponseException {
        var path = "/game";
        this.makeRequest("POST", path, new GameName(gameName), Object.class);
    }

    public void joinGame(String playerColor, int gameID) throws ResponseException {
        var path = "/game";
        record JoinGameData(String playerColor, int gameID){}
        this.makeRequest("PUT", path, new JoinGameData(playerColor, gameID), null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
//        if (!isSuccessful(status)) {
//            try (InputStream respErr = http.getErrorStream()) {
//                if (respErr != null) {
//                    throw ResponseException.fromJson(respErr);
//                }
//            }
//
//            throw new ResponseException(status, "other failure: " + status);
//        }

        if (!isSuccessful(status)) {
            System.out.println(status);
            String message = "HTTP Error: " + status;
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null) {
                    // Parse error message safely
                    HashMap<String, Object> error = new Gson().fromJson(new InputStreamReader(errorStream), HashMap.class);
                    if (error.containsKey("message")) {
                        message = (String) error.get("message");
                    }
                }
            }
            throw new ResponseException(status, message);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
//                InputStreamReader reader = new InputStreamReader(respBody);
                String body = new String(respBody.readAllBytes());
                if (responseClass != null) {
                    response = new Gson().fromJson(body, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
