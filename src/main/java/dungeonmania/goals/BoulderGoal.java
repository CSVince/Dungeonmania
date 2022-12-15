package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Switch;

public class BoulderGoal implements GoalStrategy {

    public BoulderGoal() {
    }

    public boolean achieved(Game game) {
        return game.getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    }

    public String toString(Game game) {
        return (this.achieved(game) ? "" : ":boulders");
    }

}
