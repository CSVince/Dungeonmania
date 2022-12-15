package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Treasure;

public class TreasureGoal implements GoalStrategy {

    private int target;

    public TreasureGoal(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        int currentTotal = game.getEntities(Treasure.class).size();
        return game.getInitialTreasureCount() - currentTotal >= target;
    }

    public String toString(Game game) {
        return (this.achieved(game) ? "" : ":treasure");
    }

    public String getType() {
        return "treasure";
    }
}
