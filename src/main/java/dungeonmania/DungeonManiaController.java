package dungeonmania;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.entities.JSONFactory;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    private Game game = null;
    private String config = null;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            config = configName;
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return null;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        return ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }

        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.interact(entityId));
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException  {
        JSONObject gameJson = JSONFactory.constructJSON(game);
        gameJson.put("config", config);
        gameJson.put("name", name);
        Path savesFolder;
        try {
            savesFolder = Paths.get(
                Paths.get(DungeonManiaController.class.getResource("").toURI()).toString() + "/saves");
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        if (!Files.exists(savesFolder)) {
            try {
                Files.createDirectories(savesFolder);
            } catch (Exception e) {
                System.out.println("Could not create directory " + savesFolder);
            }
        }

        Path saveName = Paths.get(savesFolder + "/" + name + ".json");

        // Write to folder
        try {
            Files.write(saveName, gameJson.toString().getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        return ResponseBuilder.getDungeonResponse(game);
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        String s;
        try {
            s = Paths.get(
                    DungeonManiaController.class.getResource("").toURI()).toString() + "/saves/" + name + ".json";
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        InputStream is;
        try {
            is = new FileInputStream(s);
        } catch (Exception e) {
            throw new IllegalArgumentException(name + " could not be found!");
        }
        byte[] bytes = null;
        try {
             bytes = is.readAllBytes();
             is.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(name + " could not be loaded!");
        }
        String contents = new String(bytes);
        JSONObject json = new JSONObject(contents);

        GameBuilder builder = new GameBuilder();
        config = json.getString("config");
        game = builder.setConfigName(json.getString("config"))
                        .setDungeonName(json.getString("name"))
                        .buildGameFromLoaded(json);
        return ResponseBuilder.getDungeonResponse(game);
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        String s;
        try {
            s = Paths.get(
                    DungeonManiaController.class.getResource("").toURI()).toString() + "/saves";
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        File saveFolder = new File(s);
        File[] listOfFiles = saveFolder.listFiles();
        List<String> filenames = new ArrayList<String>();
        for (File file: listOfFiles) {
            filenames.add(file.getName().substring(0, file.getName().length() - 5));
        }
        return filenames;
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(
            int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        return null;
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        if (game.getTick() < ticks || ticks <= 0) {
            throw new IllegalArgumentException("You cannot rewind that many ticks!");
        }
        GameBuilder builder = new GameBuilder();
        builder.setConfigName(config);
        Game gameRewinded = builder.buildGameFromState(game.getRewindedGameState(ticks), game, ticks);
        return ResponseBuilder.getDungeonResponse(gameRewinded);
    }

}
