package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.util.Position;

public class Wire extends Conductor {
    private List<Switch> activateSources;

    public Wire(Position position) {
        super(position);
        this.activateSources = new ArrayList<Switch>();
    }

    public void activateWireCircuit(int tick, Switch source) {
        activateSources.add(source);
        activate(tick, source);
    }

    public void deactivateWireCircuit(Switch source) {
        activateSources.remove(source);
        if (activateSources.size() == 0) {
            deactivate(source);
        } else {
            getAdjWires().stream()
            .filter(w -> w.getActivateSources().contains(source))
            .forEach(w -> w.deactivateWireCircuit(source));
        }
    }

    public List<Switch> getActivateSources() {
        return activateSources;
    }
}
