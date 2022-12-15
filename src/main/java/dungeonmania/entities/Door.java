package dungeonmania.entities;

import dungeonmania.map.GameMap;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.Position;

public class Door extends PositionalEntity implements Overlappable {
    private boolean open = false;
    private int number;

    public Door(Position position, int number) {
        super(position.asLayer(PositionalEntity.DOOR_LAYER));
        this.number = number;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return (entity instanceof Player && hasKey((Player) entity));
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        Inventory inventory = player.getInventory();
        Key key = player.getFirstInventoryItem(Key.class);

        if (hasKey(player)) {
            if (key != null) {
                inventory.remove(key);
            }
            open();
        }
    }

    private boolean hasKey(Player player) {
        Key key = player.getFirstInventoryItem(Key.class);
        SunStone stone = player.getFirstInventoryItem(SunStone.class);

        return ((key != null && key.getNumber() == number) || stone != null);
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

    public int getNumber() {
        return number;
    }

}
