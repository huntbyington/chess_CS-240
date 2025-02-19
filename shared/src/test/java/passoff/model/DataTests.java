package passoff.model;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class DataTests {

    @Test
    @DisplayName("Check AuthData")
    public void checkAuthData() {
        String authToken = "1234";
        String username = "name";
        AuthData actual = new AuthData(authToken, username);

        assert (Objects.equals(actual.username(), username));
        assert (Objects.equals(actual.authToken(), authToken));
    }

    @Test
    @DisplayName("Check GameData")
    public void checkGameData() {
        int gameID = 1234;
        String whiteUsername = "name0";
        String blackUsername = "name1";
        String gameName = "Game";
        ChessGame game = new ChessGame();
        GameData actual = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        assert (Objects.equals(actual.gameID(), gameID));
        assert (Objects.equals(actual.whiteUsername(), whiteUsername));
        assert (Objects.equals(actual.blackUsername(), blackUsername));
        assert (Objects.equals(actual.gameName(), gameName));
        assert (Objects.equals(actual.game(), game));
    }

    @Test
    @DisplayName("Check UserData")
    public void checkUserData() {
        String username = "name";
        String password = "pass";
        String email = "email";
        UserData actual = new UserData(username, password, email);

        assert (Objects.equals(actual.username(), username));
        assert (Objects.equals(actual.password(), password));
        assert (Objects.equals(actual.email(), email));
    }
}
