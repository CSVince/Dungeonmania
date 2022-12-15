package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemiesGoal implements GoalStrategy {

    private int target;

    public EnemiesGoal(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        return (game.getEntities(ZombieToastSpawner.class).size() == 0 && game.getPlayerKillCount() >= target);
    }

    public String toString(Game game) {
        return (this.achieved(game) ? "" : ":enemies");
    }
}
