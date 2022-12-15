package dungeonmania.entities.enemies.movement;

import java.util.List;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Circular implements MovingStrategy {
    public Circular() {
    }

    public void execute(GameMap map, Enemy enemy) {
        if (!(enemy instanceof Spider)) {
            return;
        }

        Spider s = (Spider) enemy;
        Position nextPos = s.getMovementTrajectory().get(s.getNextPositionElement());
        List<PositionalEntity> entities = map.getEntities(nextPos);
        if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            s.setForward(!s.getForward());
            s.updateNextPosition();
            s.updateNextPosition();
        }

        nextPos = s.getMovementTrajectory().get(s.getNextPositionElement());
        entities = map.getEntities(nextPos);
        if (entities == null
                || entities.size() == 0
                || entities.stream().allMatch(e -> e.canMoveOnto(map, s))) {
            map.moveTo(s, nextPos);
            s.updateNextPosition();
        }
    }
}
