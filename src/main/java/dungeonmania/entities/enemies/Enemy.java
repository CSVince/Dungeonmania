package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Destroyable;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.OlderPlayer;
import dungeonmania.entities.Overlappable;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.movement.MovingStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends PositionalEntity implements Battleable, Destroyable, Overlappable {
    private BattleStatistics battleStatistics;
    private int swampTileDuration;
    private MovingStrategy movingStrategy;

    public Enemy(Position position, double health, double attack, MovingStrategy movingStrategy) {
        super(position.asLayer(PositionalEntity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
        this.swampTileDuration = 0;
        this.movingStrategy = movingStrategy;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Player && !(entity instanceof OlderPlayer)) {
            Player player = (Player) entity;
            map.battle(player, this);
        }
    }

    public void onDestroy(GameMap map) {
        map.unsubscribe(getId());
    }

    public abstract void move(Game game);

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void setHealth(double health) {
        this.battleStatistics.setHealth(health);
    }

    public int getSwampTileDuration() {
        return swampTileDuration;
    }

    public void setSwampTileDuration(int swampTileDuration) {
        this.swampTileDuration = swampTileDuration;
    }

    public MovingStrategy getMovingStrategy() {
        return movingStrategy;
    }

    public void setMovingStrategy(MovingStrategy movingStrategy) {
        this.movingStrategy = movingStrategy;
    }
}
