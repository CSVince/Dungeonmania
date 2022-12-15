package dungeonmania.entities.rules;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Conductor;

public class CoAndRule implements Rule {
    public CoAndRule() {
    }

    public boolean satisfied(List<Conductor> conductors) {
        if (conductors.size() >= 2 && isSameTick(conductors)) {
            return true;
        }
        return false;
    }

    public boolean isSameTick(List<Conductor> conductors) {
        for (Conductor c1: conductors) {
            if (conductors.stream()
                .filter(c -> c.isActivated())
                .filter(c -> c.getActivateTick() == c1.getActivateTick())
                .collect(Collectors.toList())
                .size() >= 2) {
                return true;
            }
        }
        return false;
    }
}
