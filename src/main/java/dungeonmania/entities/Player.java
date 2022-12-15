package dungeonmania.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.InvincibleState;
import dungeonmania.entities.playerState.InvisibleState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends PositionalEntity implements Battleable, Overlappable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;
    private int killCount = 0;
    private List<JSONObject> actions = new ArrayList<JSONObject>();

    private PlayerState state;
    private PlayerState baseState;
    private PlayerState invincibleState;
    private PlayerState invisibleState;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        baseState = new BaseState(this);
        invincibleState = new InvincibleState(this);
        invisibleState = new InvisibleState(this);
        state = new BaseState(this);
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BreakableItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables(Game game) {
        return inventory.getBuildables(game);
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(this, true, entity, factory);
        if (item == null) return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    public void onOverlap(GameMap map, PositionalEntity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied()) return;
            }
            map.battle(this, (Enemy) entity);
            return;
        }
        if (entity instanceof OlderPlayer
        && ((OlderPlayer) entity).getFirstInventoryItem(SunStone.class) == null
        && ((OlderPlayer) entity).getFirstInventoryItem(MidnightArmour.class) == null
        && !((OlderPlayer) entity).getState().equals("InvisibleState")
        && getFirstInventoryItem(SunStone.class) == null
        && getFirstInventoryItem(MidnightArmour.class) == null
        && !getState().equals("InvisibleState")) {
            OlderPlayer olderPlayer = (OlderPlayer) entity;
            map.battleOlderPlayer(this, olderPlayer);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, PositionalEntity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(PositionalEntity item) {
        if (item instanceof Key && inventory.count(Key.class) == 1) {
            return false;
        }
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null) inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        state.triggerNext(currentTick);
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyStateBuff(BattleStatistics origin) {
        return state.applyBuff(origin);
    }

    public Queue<Potion> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Potion> queue) {
        this.queue = queue;
    }

    public Potion getInEffective() {
        return inEffective;
    }

    public void setInEffective(Potion inEffective) {
        this.inEffective = inEffective;
    }

    public PlayerState getBaseState() {
        return baseState;
    }

    public void setBaseState(PlayerState baseState) {
        this.baseState = baseState;
    }

    public PlayerState getInvincibleState() {
        return invincibleState;
    }

    public void setInvincibleState(PlayerState invincibleState) {
        this.invincibleState = invincibleState;
    }

    public PlayerState getInvisibleState() {
        return invisibleState;
    }

    public void setInvisibleState(PlayerState invisibleState) {
        this.invisibleState = invisibleState;
    }

    public int getNextTrigger() {
        return nextTrigger;
    }

    public void setNextTrigger(int currentTick) {
        this.nextTrigger = currentTick + inEffective.getDuration();
    }

    public <T extends InventoryItem> T getFirstInventoryItem(Class<T> type) {
        return inventory.getFirst(type);
    }

    public void useWeapon(BreakableItem item, Game game) {
        item.use(game);
    }

    public BattleStatistics applyBattleItems(BattleStatistics playerBuff, List<BattleItem> battleItems) {
        for (BattleItem item : inventory.getEntities(BattleItem.class)) {
            if (!(item instanceof Potion)) {
                playerBuff = item.applyBuff(playerBuff);
                battleItems.add(item);
            }
        }
        return playerBuff;
    }

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void setHealth(double health) {
        this.battleStatistics.setHealth(health);
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public void updateInventoryAfterBribe(int amount) {
        inventory.updateAfterBribe(amount);
    }

    public String getState() {
        return state.getClass().getSimpleName();
    }

    public void repopulateQueue(JSONArray json, EntityFactory factory) {
        json.forEach(potion -> queue.add((Potion) factory.createEntity((JSONObject) potion)));
    }

    public void repopulateInEffective(JSONObject potion, EntityFactory factory) {
        setInEffective((Potion) factory.createEntity((JSONObject) potion));
        return;
    }

    public void updateState(String state) {
        switch (state) {
            case "InvisibleState":
                this.changeState(this.getInvisibleState());
                return;
            case "InvincibleState":
                this.changeState(this.getInvincibleState());
                return;
            default:
                return;
        }
    }

    public void reloadNextTrigger(int currentTick) {
        this.nextTrigger = currentTick;
    }

    public void repopulateInventory(JSONArray json, EntityFactory factory) {
        inventory.repopulateInventory(json, factory);
    }

    public int getSceptreDuration() {
        return inventory.getSceptreDuration();
    }

    public void addAction(JSONObject action) {
        action.put("state", getState());
        actions.add(action);
    }

    public List<JSONObject> getActions() {
        return actions;
    }

    public void setBattleStatistics(BattleStatistics battleStatistics) {
        this.battleStatistics = battleStatistics;
    }
}
