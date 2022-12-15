package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.rules.Rule;
import dungeonmania.util.Position;
import dungeonmania.entities.Conductor;

public abstract class LogicalEntity extends PositionalEntity {
    private Rule rule;
    private List<Conductor> adjConductors;
    private boolean isActivated;

    public LogicalEntity(Position position, Rule rule) {
        super(position);
        this.rule = rule;
        this.adjConductors = new ArrayList<>();
        this.isActivated = false;
    }

    public void updateEntity() {
        if (getRule().satisfied(getAdjConductors())) {
            setActivated(true);
        } else {
            setActivated(false);
        }
    }

    public void addConductor(Conductor c) {
        adjConductors.add(c);
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public List<Conductor> getAdjConductors() {
        return adjConductors;
    }

    public void setAdjConductors(List<Conductor> adjConductors) {
        this.adjConductors = adjConductors;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }
}
