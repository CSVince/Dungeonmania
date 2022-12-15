package dungeonmania.goals;

import dungeonmania.Game;

public class AndGoal implements GoalStrategy {

    private GoalStrategy goal1;
    private GoalStrategy goal2;

    public AndGoal(GoalStrategy goal1, GoalStrategy goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public boolean achieved(Game game) {
        return goal1.achieved(game) && goal2.achieved(game);
    }

    public String toString(Game game) {
        return "(" + goal1.toString(game) + " AND " + goal2.toString(game) + ")";
    }

    public GoalStrategy getGoalOne() {
        return goal1;
    }

    public GoalStrategy getGoalTwo() {
        return goal2;
    }
}
