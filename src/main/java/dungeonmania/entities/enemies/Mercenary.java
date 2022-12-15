package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.enemies.movement.Dijkstra;
import dungeonmania.entities.enemies.movement.Sticking;
import dungeonmania.entities.enemies.movement.Swamp;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private int remainingSceptreEffect = 0;
    private boolean reachedAdjacency = false;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius) {
        super(position, health, attack, new Dijkstra());
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
    }

    public boolean isAllied() {
        if (remainingSceptreEffect > 0) {
            return true;
        }
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (isAllied()) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        boolean hasSceptre = player.countEntityOfType(Sceptre.class) >= 1;
        int treasure = player.countEntityOfType(Treasure.class) - player.countEntityOfType(SunStone.class);
        boolean inRange = Position.calculatePositionBetween(this.getPosition(),
            player.getPosition()).magnitude() <= bribeRadius;
        return ((inRange && treasure >= bribeAmount) || hasSceptre);
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        player.updateInventoryAfterBribe(bribeAmount);

    }

    @Override
    public void interact(Player player, Game game) {
        if (player.countEntityOfType(Sceptre.class) >= 1) {
            int mindControlEffect = player.getSceptreDuration();
            setRemainingSceptreEffect(mindControlEffect);
        } else {
            allied = true;
            bribe(player);
        }
    }

    @Override
    public void move(Game game) {
        GameMap map = game.getMap();
        // Check if adjacent after player moves
        if (isAllied() && !reachedAdjacency) {
            checkReachedAdjacency(map.getPlayerPosition());
        }

        if (!reachedAdjacency && getSwampTileDuration() > 0) {
            setMovingStrategy(new Swamp());
        } else if (isAllied()) {
            if (reachedAdjacency) {
                setMovingStrategy(new Sticking());
            } else {
                setMovingStrategy(new Dijkstra());
            }
        } else {
            // Follow hostile
            setMovingStrategy(new Dijkstra());
        }

        getMovingStrategy().execute(map, this);

        // Check if adjacent after the all moves
        if (isAllied() && !reachedAdjacency) {
            checkReachedAdjacency(map.getPlayerPosition());
        }
    }

    @Override
    public boolean isInteractable(Player player) {
        return !isAllied() && canBeBribed(player);
    }

    public int getRemainingSceptreEffect() {
        return remainingSceptreEffect;
    }

    public void setRemainingSceptreEffect(int remainingSceptreEffect) {
        this.remainingSceptreEffect = remainingSceptreEffect;
    }

    public void setReachedAdjacency(boolean reachedAdjacency) {
        this.reachedAdjacency = reachedAdjacency;
    }

    public void checkReachedAdjacency(Position playerPos) {
        if (Position.isAdjacent(getPosition(), playerPos)) {
            setReachedAdjacency(true);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        if (entity instanceof Mercenary) {
            if (((Mercenary) entity).isAllied()) {
                return true;
            }
        }
        return super.canMoveOnto(map, entity);
    }

    public int getBribeAmount() {
        return bribeAmount;
    }

    public int getBribeRadius() {
        return bribeRadius;
    }

    public void setAllied(boolean allied) {
        this.allied = allied;
    }

    public boolean getBribed() {
        return allied;
    }

    public boolean getReachedAdjacency() {
        return reachedAdjacency;
    }
}
