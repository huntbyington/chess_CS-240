package server;

import dataaccess.DataAccessException;
import service.GameService;

public class GameHandler {

    GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void clear() throws DataAccessException {
        gameService.clear();
    }
}
