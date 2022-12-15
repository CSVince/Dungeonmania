package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends PositionalEntity {
    public Exit(Position position) {
        super(position.asLayer(PositionalEntity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }
}
