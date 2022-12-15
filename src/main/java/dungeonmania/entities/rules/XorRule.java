package dungeonmania.entities.rules;

import java.util.List;

import dungeonmania.entities.Conductor;

public class XorRule implements Rule {
    public XorRule() {
    }

    public boolean satisfied(List<Conductor> conductors) {
        if (conductors.size() == 1 && conductors.stream().allMatch(c -> c.isActivated())) {
            return true;
        }
        return false;
    }
}
