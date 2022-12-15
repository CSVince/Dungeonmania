package dungeonmania.entities;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends PositionalEntity implements Overlappable {
    private int movementFactor;

    public SwampTile(Position position, int movementFactor) {
        super(position);
        this.movementFactor = movementFactor;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Enemy) {
            Enemy e = (Enemy) entity;
            e.setSwampTileDuration(movementFactor);
        }
    }

    public int getMovementFactor() {
        return movementFactor;
    }
}
