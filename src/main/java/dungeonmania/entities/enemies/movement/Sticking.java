package dungeonmania.entities.enemies.movement;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;

public class Sticking implements MovingStrategy {
    public Sticking() {
    }

    public void execute(GameMap map, Enemy e) {
        map.moveTo(e, map.getPrevPlayerPosition());
    }
}
