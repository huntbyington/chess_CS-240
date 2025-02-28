package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;
    private int nextGameId;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        nextGameId = 0;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }

        return gameDAO.listGames(authData.username());
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData;

        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }
        if (Objects.equals(gameName, "")) {
            throw new DataAccessException("Invalid Game Name");
        }

        if (new java.util.Random().nextBoolean()) {
            gameData = new GameData(nextGameId++, authData.username(), "", gameName, new ChessGame());
        } else {
            gameData = new GameData(nextGameId++, "", authData.username(), gameName, new ChessGame());
        }
        gameDAO.createGame(gameData);

        return gameData.gameID();
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
    }

}
