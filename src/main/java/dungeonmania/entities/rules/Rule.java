package dungeonmania.entities.rules;

import java.util.List;

import dungeonmania.entities.Conductor;

public interface Rule {
    public boolean satisfied(List<Conductor> conductors);
}
