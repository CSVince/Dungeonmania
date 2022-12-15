package dungeonmania.goals;

import dungeonmania.Game;

public class Goal {
    private GoalStrategy goals;

    public Goal(GoalStrategy goals) {
        this.goals = goals;
    }

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return goals.achieved(game);
    }

    public String toString(Game game) {
        return goals.toString(game);
    }

    public GoalStrategy getGoals() {
        return goals;
    }

}
