package dungeonmania.entities.enemies.movement;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;

public interface MovingStrategy {
    public void execute(GameMap map, Enemy e);
}
