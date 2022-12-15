package dungeonmania.map;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.PositionalEntity;
import dungeonmania.util.Position;

public class GraphNode {
    private Position position;
    private List<PositionalEntity> entities = new ArrayList<>();

    private int weight = 1;

    public GraphNode(PositionalEntity entity, int weight) {
        this(entity, entity.getPosition(), weight);
    }

    public GraphNode(PositionalEntity entity) {
        this(entity, entity.getPosition(), 1);
    }

    public GraphNode(PositionalEntity entity, Position p, int weight) {
        this.position = p;
        this.entities.add(entity);
        this.weight = weight;
    }

    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return entities.size() == 0 || entities.stream().allMatch(e -> e.canMoveOnto(map, entity));
    }

    public int getWeight() {
        return weight;
    }

    public void addEntity(PositionalEntity entity) {
        if (!this.entities.contains(entity))
            this.entities.add(entity);
    }

    public void removeEntity(PositionalEntity entity) {
        entities.remove(entity);
    }

    public int size() {
        return entities.size();
    }

    public void mergeNode(GraphNode node) {
        List<PositionalEntity> es = node.entities;
        es.forEach(this::addEntity);
    }

    public List<PositionalEntity> getEntities() {
        return entities;
    }

    public Position getPosition() {
        return position;
    }
}
