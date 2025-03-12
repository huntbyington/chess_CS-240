package dataaccess;

import chess.ChessGame;
import model.GameData;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class MySqlGameDataAccess implements GameDAO{

    public MySqlGameDataAccess() {
        try {
            createDatabase();

            var conn = getConnection();
            var createTableCommand = """
                            CREATE TABLE IF NOT EXISTS games (
                            gameID INT NOT NULL PRIMARY KEY,
                            whiteUsername VARCHAR(255) NOT NULL,
                            blackUsername VARCHAR(255) NOT NULL,
                            gameName VARCHAR(255) NOT NULL,
                            game longtext NOT NULL
                            );
                            """;
            var createTableStatement = conn.prepareStatement(createTableCommand);
            createTableStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        try {
            var conn = getConnection();
            var insertGameCommand = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            var insertGameStatement = conn.prepareStatement(insertGameCommand);

            var json = new Gson().toJson(gameData.game());

            insertGameStatement.setInt(1, gameData.gameID());
            insertGameStatement.setString(2, gameData.whiteUsername());
            insertGameStatement.setString(3, gameData.blackUsername());
            insertGameStatement.setString(4, gameData.gameName());
            insertGameStatement.setString(5, json);

            insertGameStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        PreparedStatement getGameStatement = null;
        ResultSet results = null;

        try {
            var conn = getConnection();
            var getGameCommand = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            getGameStatement = conn.prepareStatement(getGameCommand);

            getGameStatement.setInt(1, gameID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            results = getGameStatement.executeQuery();

            results.next();
            var whiteUsername = results.getString("whiteUsername");
            var blackUsername = results.getString("blackUsername");
            var gameName = results.getString("gameName");
            var json = results.getString("game");

           var game = new Gson().fromJson(json, ChessGame.class);

            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        } catch (SQLException e) {
            throw new DataAccessException("Game ID doesn't exist");
        }
    }

    @Override
    public Collection<GameData> listGames(String username) throws DataAccessException {
        PreparedStatement listGamesStatement = null;
        ResultSet results = null;

        try {
            var conn = getConnection();
            var listGamesCommand = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            listGamesStatement = conn.prepareStatement(listGamesCommand);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            results = listGamesStatement.executeQuery();

            Collection<GameData> gameList = new ArrayList<>();

            while(results.next()) {
                var gameID = results.getInt("gameID");
                var whiteUsername = results.getString("whiteUsername");
                var blackUsername = results.getString("blackUsername");
                var gameName = results.getString("gameName");
                var json = results.getString("game");

                var game = new Gson().fromJson(json, ChessGame.class);

                gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }

            return gameList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try {
            var conn = getConnection();
            var insertGameCommand = "UPDATE games set whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?;";
            var insertGameStatement = conn.prepareStatement(insertGameCommand);

            var json = new Gson().toJson(gameData.game());

            insertGameStatement.setString(1, gameData.whiteUsername());
            insertGameStatement.setString(2, gameData.blackUsername());
            insertGameStatement.setString(3, gameData.gameName());
            insertGameStatement.setString(4, json);
            insertGameStatement.setInt(5, gameData.gameID());

            insertGameStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var conn = getConnection();
            var clearGamesCommand = "DELETE FROM games";
            var clearGamesStatement = conn.prepareStatement(clearGamesCommand);

            clearGamesStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
