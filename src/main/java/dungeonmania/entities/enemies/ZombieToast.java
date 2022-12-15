package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.enemies.movement.Randomly;
import dungeonmania.entities.enemies.movement.Swamp;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack, new Randomly());
    }

    @Override
    public void move(Game game) {
        if (getSwampTileDuration() > 0) {
            setMovingStrategy(new Swamp());
        } else {
            setMovingStrategy(new Randomly());
        }
        getMovingStrategy().execute(game.getMap(), this);
    }
}
