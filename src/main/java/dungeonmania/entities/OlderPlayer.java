package dungeonmania.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class OlderPlayer extends Player implements Destroyable {

    private JSONArray actions;

    public OlderPlayer(Position position, double health, double attack, JSONArray actions) {
        super(position, health, attack);
        this.actions = actions;
        setBattleStatistics(new BattleStatistics(
            health,
            attack,
            0,
            BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
            BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER)
        );
    }

    @Override
    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Player
        && ((Player) entity).getFirstInventoryItem(SunStone.class) == null
        && ((Player) entity).getFirstInventoryItem(MidnightArmour.class) == null
        && !((Player) entity).getState().equals("InvisibleState")
        && this.getFirstInventoryItem(SunStone.class) == null
        && this.getFirstInventoryItem(MidnightArmour.class) == null
        && !this.getState().equals("InvisibleState")) {
            Player player = (Player) entity;
            map.battleOlderPlayer(player, this);
        }
    }

    public void onDestroy(GameMap map) {
        map.unsubscribe(getId());
    }

    public void mimicPotionState(String state) {
        if (this.getState() == state) {
            return;
        }
        switch (state) {
            case "InvisibleState":
                if (getFirstInventoryItem(InvisibilityPotion.class) != null) {
                    this.changeState(this.getInvisibleState());
                    remove(getFirstInventoryItem(InvisibilityPotion.class));
                }
                return;
            case "InvincibleState":
                if (getFirstInventoryItem(InvincibilityPotion.class) != null) {
                    this.changeState(this.getInvincibleState());
                    remove(getFirstInventoryItem(InvincibilityPotion.class));
                }
                return;
            default:
                this.changeState(this.getBaseState());
                return;
        }
    }

    public void replicatePath(Game game, EntityFactory factory) {
        JSONObject action = null;
        try {
            action = actions.getJSONObject(0);
        } catch (Exception e) {
            game.removeNode(this);
            return;
        }

        // Mimic the state of the old user
        mimicPotionState(action.getString("state"));

        if (action.has("MOVE")) {
            move(game);
        } else if (action.has("BUILD")) {
            build(action.getString("BUILD"), factory);
        } else if (action.has("USE")) {
            use(action.getString("USE"), game);
        }
        actions.remove(0);
    }

    public void use(String entityUsed, Game game) {
        if (entityUsed.equals("bomb")) {
            use(this.getFirstInventoryItem(Bomb.class), game.getMap());
        }
    }

    public void move(Game game) {
        JSONObject action = actions.getJSONObject(0);
        Direction direction = null;
        switch (action.getString("MOVE")) {
            case "UP":
                direction = Direction.UP;
                break;
            case "DOWN":
                direction = Direction.DOWN;
                break;
            case "RIGHT":
                direction = Direction.RIGHT;
                break;
            case "LEFT":
                direction = Direction.LEFT;
                break;
            case "NONE":
                direction = Direction.NONE;
                break;
            default:
                direction = null;
                break;
        }
        if (direction != null) {
            this.setFacing(direction);
            game.moveTo(this, Position.translateBy(this.getPosition(), direction));
        }
    }
}
