package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void prepareDb() throws ResponseException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        serverFacade.clear();
        server.stop();
    }


    @Test
    public void registerUserTest() {
        try {
            serverFacade.register("username", "password", "email");

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void registerDuplicateUserTest() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.register("username", "password", "email");

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }

    @Test
    public void loginUserTest() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.login("username", "password");

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void loginUserWrongPassTest() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.login("username", "wrong");

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }

    @Test
    public void loginNonExistentUserTest() {
        try {
            serverFacade.login("username", "password");

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }

    @Test
    public void logoutUserTest() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.logout();

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void logoutNonexistentUserTest() {
        try {
            serverFacade.logout();

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }

    @Test
    public void createGame() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.createGame("myGame");

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void createMultGames() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.createGame("myGame");
            serverFacade.createGame("myGame2");

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void listGames() {
        try {
            serverFacade.register("username", "password", "email");
            serverFacade.createGame("myGame");
            serverFacade.createGame("myGame2");

            serverFacade.listGames();

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void listGamesNoGames() {
        try {
            serverFacade.register("username", "password", "email");

            serverFacade.listGames();

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void joinGame() {
        try {
            serverFacade.register("username", "password", "email");
            serverFacade.createGame("myGame");

            serverFacade.joinGame("WHITE", 1);

            assert true; // Asserts true if no error is thrown
        } catch (ResponseException e) {
            assert false;
        }
    }

    @Test
    public void joinGameNonexistentGame() {
        try {
            serverFacade.register("username", "password", "email");
            serverFacade.createGame("myGame");

            serverFacade.joinGame("WHITE", 999);

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }

    @Test
    public void joinGameNonexistentColor() {
        try {
            serverFacade.register("username", "password", "email");
            serverFacade.createGame("myGame");

            serverFacade.joinGame("Orange", 1);

            assert false; // Asserts false if no error is thrown
        } catch (ResponseException e) {
            assert true;
        }
    }
}
