package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Switch;
import dungeonmania.map.GameMap;

public class Bomb extends CollectableEntity {
    public enum State {
        SPAWNED,
        INVENTORY,
        PLACED
    }

    public static final int DEFAULT_RADIUS = 1;
    private State state;
    private int radius;

    private List<Switch> subs = new ArrayList<>();

    public Bomb(Position position, int radius) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
    }

    public void subscribe(Switch s) {
        this.subs.add(s);
    }

    public void notify(GameMap map) {
        explode(map);
    }

    @Override
    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (state != State.SPAWNED) return;
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this)) return;
            subs.stream().forEach(s -> s.unsubscribe(this));
            map.destroyEntity(this);
        }
        this.state = State.INVENTORY;
    }

    public void onPutDown(GameMap map, Position p) {
        Position offset = Position.calculatePositionBetween(getPosition(), p);
        setPosition(Position.translateBy(getPosition(), offset));
        map.addEntity(this);
        this.state = State.PLACED;
        List<Position> adjPosList = getPosition().getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<PositionalEntity> entities = map.getEntities(node)
                                        .stream()
                                        .filter(e -> (e instanceof Switch))
                                        .collect(Collectors.toList());
            entities.stream()
                    .map(Switch.class::cast)
                    .forEach(s -> s.subscribe(this, map));
            entities.stream()
                    .map(Switch.class::cast)
                    .forEach(s -> this.subscribe(s));
        });
    }

    /**
     * it destroys all entities in diagonally and cardinally adjacent cells, except for the player
     * @param map
     */
    public void explode(GameMap map) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                List<PositionalEntity> entities = map.getEntities(new Position(i, j));
                entities = entities.stream()
                    .filter(e -> !(e instanceof Player))
                    .collect(Collectors.toList());
                entities.stream().forEach(e -> map.destroyEntity(e));
            }
        }
    }

    public State getState() {
        return state;
    }

    public int getRadius() {
        return radius;
    }

    public void setState(String state) {
        switch (state) {
            case "SPAWNED":
                this.state = State.SPAWNED;
                return;
            case "INVENTORY":
                this.state = State.INVENTORY;
                return;
            case "PLACED":
                this.state = State.PLACED;
                return;
            default:
                return;
        }
    }
}
