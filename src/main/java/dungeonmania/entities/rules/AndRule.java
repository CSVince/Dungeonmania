package dungeonmania.entities.rules;

import java.util.List;

import dungeonmania.entities.Conductor;

public class AndRule implements Rule {

    public AndRule() {
    }

    public boolean satisfied(List<Conductor> conductors) {
        if (conductors.size() >= 2 && conductors.stream().allMatch(c -> c.isActivated())) {
            return true;
        }
        return false;
    }
}
