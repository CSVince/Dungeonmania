package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.util.Position;

public class Wall extends PositionalEntity {
    public Wall(Position position) {
        super(position.asLayer(PositionalEntity.CHARACTER_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return entity instanceof Spider;
    }
}
