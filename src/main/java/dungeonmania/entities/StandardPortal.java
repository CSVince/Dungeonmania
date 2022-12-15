package dungeonmania.entities;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class StandardPortal extends Portal {
    private ColorCodedType color;
    private StandardPortal pair;

    public StandardPortal(Position position, ColorCodedType color) {
        super(position);
        this.color = color;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        if (pair == null)
            return false;
        if (entity instanceof Player || entity instanceof Mercenary)
            return pair.canTeleportTo(map, entity);
        return true;
    }

    public boolean canTeleportTo(GameMap map, PositionalEntity entity) {
        List<Position> neighbours = getPosition().getCardinallyAdjacentPositions();
        return neighbours.stream().allMatch(n -> map.canMoveTo(entity, n));
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (pair == null)
            return;
        if (entity instanceof Player || entity instanceof Mercenary)
            doTeleport(map, entity);
    }

    private void doTeleport(GameMap map, PositionalEntity entity) {
        Position destination = pair.getPosition()
                .getCardinallyAdjacentPositions()
                .stream()
                .filter(dest -> map.canMoveTo(entity, dest))
                .findAny()
                .orElse(null);
        if (destination != null) {
            map.moveTo(entity, destination);
        }
    }

    public String getColor() {
        return color.toString();
    }

    public List<Position> getDestPositions(GameMap map, PositionalEntity entity) {
        return pair == null
                ? null
                : pair.getPosition().getAdjacentPositions()
                    .stream()
                    .filter(p -> map.canMoveTo(entity, p))
                    .collect(Collectors.toList());
    }
    public void bind(StandardPortal portal) {
        if (this.pair == portal)
            return;
        if (this.pair != null) {
            this.pair.bind(null);
        }
        this.pair = portal;
        if (portal != null) {
            portal.bind(this);
        }
    }
}
