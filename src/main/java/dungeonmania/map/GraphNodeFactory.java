package dungeonmania.map;

import org.json.JSONObject;

import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.SwampTile;

public class GraphNodeFactory {
    public static GraphNode createEntity(JSONObject jsonEntity, EntityFactory factory) {
        return constructEntity(jsonEntity, factory);
    }

    private static GraphNode constructEntity(JSONObject jsonEntity, EntityFactory factory) {
        switch (jsonEntity.getString("type")) {
        case "player":
        case "zombie_toast":
        case "zombie_toast_spawner":
        case "mercenary":
        case "hydra":
        case "wall":
        case "boulder":
        case "switch":
        case "exit":
        case "treasure":
        case "wood":
        case "arrow":
        case "bomb":
        case "invisibility_potion":
        case "invincibility_potion":
        case "portal":
        case "sword":
        case "spider":
        case "door":
        case "sun_stone":
        case "key":
        case "wire":
        case "light_bulb_off":
        case "switch_door":
        case "time_turner":
        case "time_travelling_portal":
        case "older_player":
            return new GraphNode(factory.createEntity(jsonEntity));
        case "swamp_tile":
            SwampTile st = (SwampTile) factory.createEntity(jsonEntity);
            return new GraphNode(factory.createEntity(jsonEntity), 1 + st.getMovementFactor());
        default:
            return null;
        }
    }
}
