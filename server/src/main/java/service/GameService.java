package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class GameService {

    final GameDAO gameDAO;
    final AuthDAO authDAO;
    private int nextGameId;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        nextGameId = 1;
    }

    public HashSet<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }

        return gameDAO.listGames(authData.username());
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }
        if (Objects.equals(gameName, "")) {
            throw new DataAccessException("Invalid Game Name");
        }

        GameData gameData = new GameData(nextGameId++, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);

        return gameData.gameID();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("Incorrect Authorization");
        }

        GameData gameData = gameDAO.getGame(gameID);

        if (gameData == null) {
            throw new DataAccessException("Invalid Game ID");
        }

        GameData newGameData;
        if (Objects.equals(playerColor, "WHITE")) {
            if (Objects.equals(gameData.whiteUsername(), null)
                    && !Objects.equals(gameData.blackUsername(), playerColor)) {
                newGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                                            gameData.gameName(), gameData.game());
            } else {
                throw new DataAccessException("Player Color Already Taken");
            }
        } else if (Objects.equals(playerColor, "BLACK")) {
            if (Objects.equals(gameData.blackUsername(), null)
                    && !Objects.equals(gameData.whiteUsername(), playerColor)) {
                newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                                            gameData.gameName(), gameData.game());
            } else {
                throw new DataAccessException("Player Color Already Taken");
            }
        } else {
            throw new DataAccessException("Invalid Player Color");
        }

        gameDAO.updateGame(newGameData);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
    }

}
