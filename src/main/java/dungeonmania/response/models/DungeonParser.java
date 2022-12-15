package dungeonmania.response.models;

import dungeonmania.Game;

public class DungeonParser {

    public static Game buildGame(String name) {
        Game game = new Game(name);
        return game;
    }
}
