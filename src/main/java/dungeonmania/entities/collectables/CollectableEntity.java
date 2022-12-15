package dungeonmania.entities.collectables;

import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.Overlappable;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class CollectableEntity extends PositionalEntity implements Overlappable, InventoryItem {

    public CollectableEntity(Position position) {
        super(position);
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this)) return;
            map.destroyEntity(this);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }
}
