package dungeonmania.entities.enemies.movement;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Randomly implements MovingStrategy {
    private Random randGen = new Random();
    public Randomly() {
    }

    public void execute(GameMap map, Enemy e) {
        Position nextPos;
        List<Position> pos = e.getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> map.canMoveTo(e, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = e.getPosition();
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
            }
        map.moveTo(e, nextPos);
    }
}
