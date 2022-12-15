package dungeonmania.entities.logical;

import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.rules.Rule;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    public SwitchDoor(Position position, Rule rule) {
        super(position, rule);
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        if (isActivated() || entity instanceof Spider) {
            return true;
        }
        return false;
    }
}
