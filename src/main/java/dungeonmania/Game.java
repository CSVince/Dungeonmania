package dungeonmania;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.Entity;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.JSONFactory;
import dungeonmania.entities.OlderPlayer;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private int initialTreasureCount;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int AI_MOVEMENT = 2;
    public static final int AI_MOVEMENT_CALLBACK = 3;

    private List<JSONObject> gameStates = new ArrayList<>();

    private int tickCount = 0;
    private PriorityQueue<ComparableCallback> sub = new PriorityQueue<>();
    private PriorityQueue<ComparableCallback> addingSub = new PriorityQueue<>();

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void initFromRewind(JSONObject gameState) {
        this.id = UUID.randomUUID().toString();
        map.init(gameState);
        battleFacade.setBattleResponses(JSONFactory.jsonToBattleResponses(
            gameState.getJSONArray("battleFacade")));
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
    }

    public void initFromLoad(JSONObject json) {
        this.id = UUID.randomUUID().toString();
        map.init(json);
        this.tickCount = json.getInt("tickCount");
        this.isInTick = json.getBoolean("isInTick");
        battleFacade.setBattleResponses(JSONFactory.jsonToBattleResponses(
            json.getJSONArray("battleFacade")));
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        initialTreasureCount = json.getInt("initialTreasureCount");
        gameStates.add(JSONFactory.constructJSON(this));
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        initialTreasureCount = map.getEntities(Treasure.class).size();
        gameStates.add(JSONFactory.constructJSON(this));
    }

    public Game tick(Direction movementDirection) {
        JSONObject movementJson = new JSONObject();
        switch (movementDirection) {
            case UP:
                movementJson.put("MOVE", "UP");
                break;
            case RIGHT:
                movementJson.put("MOVE", "RIGHT");
                break;
            case LEFT:
                movementJson.put("MOVE", "LEFT");
                break;
            case DOWN:
                movementJson.put("MOVE", "DOWN");
                break;
            case NONE:
                movementJson.put("MOVE", "NONE");
                break;
            default:
                movementJson.put("MOVE", "NONE");
                break;
        }
        player.addAction(movementJson);
        registerOnce(
            () -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));
        JSONObject useItemJson = new JSONObject();
        useItemJson.put("USE", item.getType());
        player.addAction(useItemJson);
        registerOnce(() -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    /**
     * Battle between player and enemy
     * @param player
     * @param enemy
     */
    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public void battleOlderPlayer(Player player, OlderPlayer enemy) {
        battleFacade.battleOlderPlayer(this, player, enemy);
        if (player.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        List<String> buildables = player.getBuildables(this);
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        JSONObject buildJson = new JSONObject();
        buildJson.put("BUILD", buildable);
        player.addAction(buildJson);
        registerOnce(() -> player.build(buildable, entityFactory), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        PositionalEntity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce(
            () -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public <T extends PositionalEntity> long countEntities(Class<T> type) {
        return map.countEntities(type);
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        sub.forEach(s -> s.run());
        isInTick = false;
        sub.addAll(addingSub);
        addingSub = new PriorityQueue<>();
        sub.removeIf(s -> !s.isValid());
        tickCount++;
        // update the weapons/potions duration
        // Update mercenaries effect
        updateMercenariesSceptreEffect();
        gameStates.add(JSONFactory.constructJSON(this));
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void setBattleFacade(BattleFacade battleFacade) {
        this.battleFacade = battleFacade;
    }

    public int getInitialTreasureCount() {
        return initialTreasureCount;
    }

    public List<PositionalEntity> getEntities() {
        return map.getEntities();
    }

    public List<PositionalEntity> getEntities(Position nextPos) {
        return map.getEntities(nextPos);
    }

    public <T extends PositionalEntity> List<T> getEntities(Class<T> type) {
        return map.getEntities(type);
    }

    public void spawnSpider(Game game) {
        entityFactory.spawnSpider(game);
        return;
    }

    public void spawnZombie(Game game, ZombieToastSpawner spawner) {
        entityFactory.spawnZombie(game, spawner);
        return;
    }

    public void moveTo(PositionalEntity entity, Position nextPos) {
        map.moveTo(entity, nextPos);
        return;
    }

    public void removeFromPlayer(InventoryItem item) {
        player.remove(item);
    }

    public void updateMercenariesSceptreEffect() {
        for (Mercenary mercenary: getEntities(Mercenary.class)) {
            int remainingTime = mercenary.getRemainingSceptreEffect();
            remainingTime--;
            mercenary.setRemainingSceptreEffect(remainingTime);
        }
    }

    public int getPlayerKillCount() {
        return player.getKillCount();
    }

    public void destroyEntity(PositionalEntity entity) {
        map.destroyEntity(entity);
    }


    public void removeAllEntitiesBesidesPlayer() {
        getEntities().stream().filter(e ->  !(e instanceof Player)).forEach(e -> map.destroyEntity(e));
        return;
    }

    public JSONObject getRewindedGameState(int ticks) {
        int gameTickWanted = tickCount - ticks;
        if (gameTickWanted < 0) {
            gameTickWanted = 0;
        }
        return gameStates.get(gameTickWanted);
    }

    public void resetSubs() {
        sub = new PriorityQueue<>();
        addingSub = new PriorityQueue<>();
    }

    public List<JSONObject> getPlayerActions(int ticks) {
        if (tickCount - ticks < 0) {
            return player.getActions();
        } else {
            int size = player.getActions().size();
            return player.getActions().subList(tickCount - ticks, size);
        }
    }

    public boolean getIsInTick() {
        return isInTick;
    }

    public void setMapPlayer(Player player) {
        map.setPlayer(player);
    }

    public Player getMapPlayer() {
        return map.getPlayer();
    }

    public void removeNode(PositionalEntity e) {
        map.removeNode(e);
    }
}
