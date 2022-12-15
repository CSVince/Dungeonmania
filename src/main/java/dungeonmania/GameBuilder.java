package dungeonmania;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.entities.collectables.*;
import dungeonmania.entities.*;
import dungeonmania.entities.enemies.*;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.JSONFactory;
import dungeonmania.entities.OlderPlayer;
import dungeonmania.entities.Player;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalFactory;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;
import dungeonmania.map.GraphNodeFactory;
import dungeonmania.util.FileLoader;

/**
 * GameBuilder -- A builder to build up the whole game
 * @author      Webster Zhang
 * @author      Tina Ji
 */
public class GameBuilder {
    private String configName;
    private String dungeonName;

    private JSONObject config;
    private JSONObject dungeon;

    public GameBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public GameBuilder setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
        return this;
    }

    public Game buildGameFromLoaded(JSONObject json) {
        loadConfig();
        dungeon = json;
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }
        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);

        game.initFromLoad(json);
        return game;
    }

    public Game buildGame() {
        loadConfig();
        loadDungeon();
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();

        return game;
    }

    private void loadConfig() {
        String configFile = String.format("/configs/%s.json", configName);
        try {
            config = new JSONObject(FileLoader.loadResourceFile(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }

    private void loadDungeon() {
        String dungeonFile = String.format("/dungeons/%s.json", dungeonName);
        try {
            dungeon = new JSONObject(FileLoader.loadResourceFile(dungeonFile));
        } catch (IOException e) {
            dungeon = null;
        }
    }

    private void buildMap(Game game) {
        GameMap map = new GameMap();
        map.setGame(game);

        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());
            Entity entity = newNode.getEntities().get(0);

            if (newNode != null)
                map.addNode(newNode);

            if (entity instanceof Player && !(entity instanceof OlderPlayer))
                map.setPlayer((Player) entity);
        });
        game.setMap(map);
    }

    private void buildRewindedMap(Game game, int ticks) {
        GameMap map = game.getMap();
        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            if (jsonEntity.getString("type").equals("player")) {
                jsonEntity.remove("type");
                jsonEntity.put("type", "older_player");
                jsonEntity.put("ticks", ticks);
                jsonEntity.put("actions", game.getPlayerActions(ticks));
            }
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());

            if (newNode != null)
                map.addNode(newNode);
        });
        game.setMap(map);
    }

    public void buildGoals(Game game) {
        if (!dungeon.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(dungeon.getJSONObject("goal-condition"), config);
            game.setGoals(goal);
        }
    }

    public Game buildGameFromState(JSONObject gameState, Game currentGame, int ticks) {
        dungeon = gameState;
        currentGame.removeAllEntitiesBesidesPlayer();
        buildRewindedMap(currentGame, ticks);
        currentGame.resetSubs();
        currentGame.initFromRewind(dungeon);
        return currentGame;
    }

    public static void updateEntities(JSONArray entities, Game game) {
        entities.forEach(e -> {
            switch (((JSONObject) e).getString("type")) {
                case "player":
                    loadInPlayer(((JSONObject) e), game);
                    break;
                case "spider":
                    updateSpider(((JSONObject) e), game);
                    break;
                case "mercenary":
                    updateMercenary(((JSONObject) e), game);
                    break;
                case "door":
                    updateDoor(((JSONObject) e), game);
                    break;
                case "switch":
                    updateSwitch(((JSONObject) e), game);
                    break;
                case "bomb":
                    updateBomb(((JSONObject) e), game);
                    break;
                case "hydra":
                case "zombie_toast":
                    updateHydraZombie(((JSONObject) e), game);
                    break;
                case "older_player":
                    loadInOldPlayer(((JSONObject) e), game);
                    break;
                default:
                    break;
            }
        });
    }

    public static void loadInOldPlayer(JSONObject json, Game game) {
        OlderPlayer player = game.getEntities(OlderPlayer.class).get(0);
        player.repopulateInventory(json.getJSONArray("inventory"), game.getEntityFactory());
        player.repopulateQueue(json.getJSONArray("queue"), game.getEntityFactory());
        try {
            player.repopulateInEffective(json.getJSONObject("inEffective"), game.getEntityFactory());
        } catch (Exception e) {
            System.out.println("No potion in current use");
        }
        if (json.getInt("nextTrigger") != 0) {
            player.reloadNextTrigger(json.getInt("nextTrigger"));
        }
        player.setKillCount(json.getInt("killCount"));
        player.updateState(json.getString("state"));
        player.setHealth(json.getJSONObject("battleStatistics").getDouble("health"));
    }

    public static void loadInPlayer(JSONObject json, Game game) {
        Player player = game.getMapPlayer();
        player.repopulateInventory(json.getJSONArray("inventory"), game.getEntityFactory());
        player.repopulateQueue(json.getJSONArray("queue"), game.getEntityFactory());
        try {
            player.repopulateInEffective(json.getJSONObject("inEffective"), game.getEntityFactory());
        } catch (Exception e) {
            System.out.println("No potion in current use");
        }
        if (json.getInt("nextTrigger") != 0) {
            player.reloadNextTrigger(json.getInt("nextTrigger"));
        }
        player.setKillCount(json.getInt("killCount"));
        player.updateState(json.getString("state"));
        player.setPreviousPosition(JSONFactory.jsonToPosition(json.getJSONObject("previousPosition")));
        JSONObject previousDistinctPosition = null;
        try {
            previousDistinctPosition = json.getJSONObject("previousDistinctPosition");
            player.setPreviousDistinctPosition(JSONFactory.jsonToPosition(previousDistinctPosition));
        } catch (Exception e) {
            System.out.println("No previous distinct position");
        }
        game.setPlayer(player);
        game.setMapPlayer(player);
    }

    public static void updateSpider(JSONObject spider, Game game) {
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == spider.getInt("x")
                        && e.getPosition().getY() == spider.getInt("y")
                        && e.getType().equals("spider")))
                    .forEach(e -> {
                        ((Spider) e).setMovementTrajectory(
                            JSONFactory.jsonToMovementPattern(spider.getJSONArray("movementTrajectory")));
                        ((Spider) e).setForward(spider.getBoolean("forward"));
                        ((Spider) e).setNextPositionElement(spider.getInt("nextPositionElement"));
                        ((Spider) e).setSwampTileDuration(spider.getInt("swampTileDuration"));
                    });
    }

    public static void updateMercenary(JSONObject mercenary, Game game) {
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == mercenary.getInt("x")
                        && e.getPosition().getY() == mercenary.getInt("y")
                        && e.getType().equals("mercenary")
                    ))
                    .forEach(e -> {
                        ((Mercenary) e).setRemainingSceptreEffect(mercenary.getInt("remainingSceptreEffect"));
                        ((Mercenary) e).setAllied(mercenary.getBoolean("allied"));
                        ((Mercenary) e).setReachedAdjacency(mercenary.getBoolean("reachedAdjacency"));
                        ((Mercenary) e).setSwampTileDuration(mercenary.getInt("swampTileDuration"));
                    });
    }

    public static void updateDoor(JSONObject door, Game game) {
        if (!door.getBoolean("open")) {
            return;
        }
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == door.getInt("x")
                        && e.getPosition().getY() == door.getInt("y")
                        && e.getType().equals("door")
                    ))
                    .forEach(e -> ((Door) e).open());
    }

    public static void updateSwitch(JSONObject switchJson, Game game) {
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == switchJson.getInt("x")
                        && e.getPosition().getY() == switchJson.getInt("y")
                        && e.getType().equals("switch")
                    ))
                    .forEach(e -> ((Switch) e).setActivated(switchJson.getBoolean("activated")));
    }

    public static void updateBomb(JSONObject bomb, Game game) {
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == bomb.getInt("x")
                        && e.getPosition().getY() == bomb.getInt("y")
                        && e.getType().equals("bomb")
                    ))
                    .forEach(e -> ((Bomb) e).setState(bomb.getString("state")));
    }

    public static void updateHydraZombie(JSONObject enemy, Game game) {
        game.getEntities().stream()
                    .filter(e -> (
                        e.getPosition().getX() == enemy.getInt("x")
                        && e.getPosition().getY() == enemy.getInt("y")
                        && (e.getType().equals("hydra") || e.getType().equals("zombie_toast"))
                    ))
                    .forEach(e -> ((Enemy) e).setSwampTileDuration(enemy.getInt("swampTileDuration")));
    }
}
