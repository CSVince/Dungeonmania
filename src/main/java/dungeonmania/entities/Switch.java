package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor implements MoveAwayable, Overlappable {
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(PositionalEntity.ITEM_LAYER));
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (isActivated()) {
            notifyBombSubscribers(map);
        }
    }

    public void notifyBombSubscribers(GameMap map) {
        bombs.stream().forEach(b -> b.notify(map));
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Boulder) {
            activate(map.getTick(), this);
            notifyBombSubscribers(map);
        }
    }

    public void onMovedAway(GameMap map, PositionalEntity entity) {
        if (entity instanceof Boulder) {
            deactivate(this);
        }
    }
}
