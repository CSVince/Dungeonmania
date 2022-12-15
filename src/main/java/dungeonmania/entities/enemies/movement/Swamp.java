package dungeonmania.entities.enemies.movement;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;

public class Swamp implements MovingStrategy {
    public Swamp() {
    }

    @Override
    public void execute(GameMap map, Enemy e) {
        e.setSwampTileDuration(e.getSwampTileDuration() - 1);
    }
}
