package dungeonmania.entities.enemies.movement;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Dijkstra implements MovingStrategy {
    public Dijkstra() {
    }

    public void execute(GameMap map, Enemy e) {
        Position nextPos = map.dijkstraPathFind(e.getPosition(), map.getPlayerPosition(), e);
        map.moveTo(e, nextPos);
    }
}
