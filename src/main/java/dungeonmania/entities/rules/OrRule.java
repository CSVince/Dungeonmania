package dungeonmania.entities.rules;

import java.util.List;

import dungeonmania.entities.Conductor;

public class OrRule implements Rule {
    public OrRule() {
    }

    public boolean satisfied(List<Conductor> conductors) {
        if (conductors.stream().anyMatch(c -> c.isActivated())) {
            return true;
        }
        return false;
    }
}
