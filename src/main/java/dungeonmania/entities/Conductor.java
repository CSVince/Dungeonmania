package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.logical.LogicalEntity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends PositionalEntity {
    private boolean activated;
    private int activateTick;
    private List<Wire> adjWires;
    private List<LogicalEntity> adjEntities;

    public Conductor(Position position) {
        super(position);
        this.adjWires =  new ArrayList<>();
        this.adjEntities =  new ArrayList<>();
        this.activateTick = -1;
    }

    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }

    public void activate(int tick, Switch source) {
        setActivated(true);
        setActivateTick(tick);
        notifyLogicalEntitySubscribers();
        getAdjWires().stream()
            .filter(w -> !w.getActivateSources().contains(source))
            .forEach(w -> w.activateWireCircuit(tick, source));
    }

    public void deactivate(Switch source) {
        setActivated(false);
        notifyLogicalEntitySubscribers();
        getAdjWires().stream()
            .filter(w -> w.getActivateSources().contains(source))
            .forEach(w -> w.deactivateWireCircuit(source));
    }

    public void subscribe(Wire w) {
        adjWires.add(w);
    }

    public void subscribe(LogicalEntity e) {
        adjEntities.add(e);
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public List<Wire> getAdjWires() {
        return adjWires;
    }

    public void setAdjWires(List<Wire> adjWires) {
        this.adjWires = adjWires;
    }

    public void notifyLogicalEntitySubscribers() {
        adjEntities.stream().forEach(e -> e.updateEntity());
    }

    public void setActivateTick(int tick) {
        this.activateTick = tick;
    }

    public int getActivateTick() {
        return activateTick;
    }
}
