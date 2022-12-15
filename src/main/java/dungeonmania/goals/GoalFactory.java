package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {

    public static GoalStrategy getStrategy(JSONObject jsonGoal, JSONObject config) {
        JSONArray subgoals;
        switch (jsonGoal.getString("goal")) {
        case "AND":
            subgoals = jsonGoal.getJSONArray("subgoals");
            return new AndGoal(
                getStrategy(subgoals.getJSONObject(0), config),
                getStrategy(subgoals.getJSONObject(1), config)
            );
        case "OR":
            subgoals = jsonGoal.getJSONArray("subgoals");
            return new OrGoal(
                getStrategy(subgoals.getJSONObject(0), config),
                getStrategy(subgoals.getJSONObject(1), config)
            );
        case "exit":
            return new ExitGoal();
        case "boulders":
            return new BoulderGoal();
        case "treasure":
            int treasureGoal = config.optInt("treasure_goal", 1);
            return new TreasureGoal(treasureGoal);
        case "enemies":
            int enemyGoal = config.optInt("enemy_goal", 1);
            return new EnemiesGoal(enemyGoal);
        default:
            return null;
        }
    }

    public static Goal createGoal(JSONObject jsonGoal, JSONObject config) {
        return new Goal(getStrategy(jsonGoal, config));
    }

}
