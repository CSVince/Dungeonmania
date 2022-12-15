package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class TimeTravellingPortal extends Portal {

    public TimeTravellingPortal(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof OlderPlayer) {
            map.destroyEntity(entity);
            return;
        }
        if (entity instanceof Player) {
            map.timeTravelBack(29);
        }
    }
}
