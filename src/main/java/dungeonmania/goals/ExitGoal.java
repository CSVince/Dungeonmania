package dungeonmania.goals;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class ExitGoal implements GoalStrategy {

    public ExitGoal() {
    }

    public boolean achieved(Game game) {
        Player character = game.getPlayer();
        Position pos = character.getPosition();
        List<Exit> es = game.getEntities(Exit.class);
        if (es == null || es.size() == 0) return false;
        return es
            .stream()
            .map(PositionalEntity::getPosition)
            .anyMatch(pos::equals);
    }

    public String toString(Game game) {
        return (this.achieved(game) ? "" : ":exit");
    }
}
