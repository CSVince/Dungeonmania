package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.List;

public class PositionalEntity extends Entity {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private Position previousPosition;
    private Position previousDistinctPosition;
    private Direction facing;

    public PositionalEntity(Position position) {
        super();
        this.position = position;
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.facing = null;
    }

    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return false;
    }

    public Position getPosition() {
        return position;
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public List<Position> getCardinallyAdjacentPositions() {
        return position.getCardinallyAdjacentPositions();
    }

    public void setPosition(Position position) {
        previousPosition = this.position;
        this.position = position;
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public void setPreviousPosition(Position previousPosition) {
        this.previousPosition = previousPosition;
    }

    public void setPreviousDistinctPosition(Position previousDistinctPosition) {
        this.previousDistinctPosition = previousDistinctPosition;
    }
}
