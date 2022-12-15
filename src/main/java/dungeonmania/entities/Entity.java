package dungeonmania.entities;

import java.util.UUID;

public class Entity {
    private String entityId;

    public Entity() {
        this.entityId = UUID.randomUUID().toString();
    }

    public String getId() {
        return entityId;
    }

    public String getType() {
        String type = this.getClass().getSimpleName().toLowerCase();
        switch (type) {
            case "zombietoast":
                return "zombie_toast";
            case "zombietoastspawner":
                return "zombie_toast_spawner";
            case "invisibilitypotion":
                return "invisibility_potion";
            case "invincibilitypotion":
                return "invincibility_potion";
            case "swamptile":
                return "swamp_tile";
            case "sunstone":
                return "sun_stone";
            case "timeturner":
                return "time_turner";
            case "timetravellingportal":
                return "time_travelling_portal";
            case "switchdoor":
                return "switch_door";
            case "olderplayer":
                return "older_player";
            case "standardportal":
                return "portal";
            case "midnightarmour":
                return "midnight_armour";
            default:
                return type;
        }
    }
}
