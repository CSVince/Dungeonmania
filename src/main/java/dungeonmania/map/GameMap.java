package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Conductor;
import dungeonmania.GameBuilder;
import dungeonmania.entities.Destroyable;
import dungeonmania.entities.PositionalEntity;
import dungeonmania.entities.MoveAwayable;
import dungeonmania.entities.OlderPlayer;
import dungeonmania.entities.Overlappable;
import dungeonmania.entities.Player;
import dungeonmania.entities.StandardPortal;
import dungeonmania.entities.SwampTile;
import dungeonmania.entities.Switch;
import dungeonmania.entities.Wire;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logical.LogicalEntity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class GameMap {
    private Game game;
    private Map<Position, GraphNode> nodes = new HashMap<>();
    private Player player;

    /**
     * Initialise the game map
     * 1. pair up portals
     */
    public void init() {
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        initRegisterBombsAndSwitches();
        initEntitiesOnSwampTiles();
        initRegisterConductors();
        initRegisterLogicalEntities();
    }

    public void init(JSONObject json) {
        initEntitiesOnSwampTiles();
        GameBuilder.updateEntities(json.getJSONArray("entities"), game);
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        initRegisterBombsAndSwitches();
    }

    private void initRegisterBombsAndSwitches() {
        List<Bomb> bombs = getEntities(Bomb.class);
        List<Switch> switches = getEntities(Switch.class);
        bombs.stream().forEach(b ->
            switches.stream()
                .filter(s -> Position.isAdjacent(b.getPosition(), s.getPosition()))
                .forEach(s -> {
                    b.subscribe(s);
                    s.subscribe(b);
                }
            )
        );
    }

    private void initRegisterConductors() {
        List<Wire> wires = getEntities(Wire.class);
        List<Switch> switches = getEntities(Switch.class);
        switches.stream().forEach(s ->
            wires.stream()
                .filter(w -> Position.isAdjacent(w.getPosition(), s.getPosition()))
                .forEach(w -> {
                    s.subscribe(w);
                }
            )
        );

        wires.stream().forEach(w1 ->
            wires.stream()
                .filter(w2 -> Position.isAdjacent(w1.getPosition(), w2.getPosition()))
                .forEach(w2 -> {
                    w1.subscribe(w2);
                }
            )
        );
    }

    private void initRegisterLogicalEntities() {
        List<Conductor> conductors = getEntities(Conductor.class);
        List<LogicalEntity> entities = getEntities(LogicalEntity.class);
        entities.stream().forEach(e ->
            conductors.stream()
                .filter(c -> Position.isAdjacent(e.getPosition(), c.getPosition()))
                .forEach(c -> {
                    c.subscribe(e);
                    e.addConductor(c);
                }
            )
        );
    }

    // Pair up portals if there's any
    private void initPairPortals() {
        Map<String, StandardPortal> portalsMap = new HashMap<>();
        nodes.forEach((k, v) -> {
            v.getEntities()
                    .stream()
                    .filter(StandardPortal.class::isInstance)
                    .map(StandardPortal.class::cast)
                    .forEach(portal -> {
                        String color = portal.getColor();
                        if (portalsMap.containsKey(color)) {
                            portal.bind(portalsMap.get(color));
                        } else {
                            portalsMap.put(color, portal);
                        }
                    });
        });
    }

    private void initRegisterMovables() {
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.forEach(e -> {
            game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
        });
        List<OlderPlayer> older = getEntities(OlderPlayer.class);
        if (older.size() != 0) {
            game.register(() -> older.get(0).replicatePath(game,
                game.getEntityFactory()), Game.AI_MOVEMENT, older.get(0).getId());
        }
    }

    private void initRegisterSpawners() {
        List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
        zts.forEach(e -> {
            game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
        });
        game.register(() -> game.spawnSpider(game), Game.AI_MOVEMENT, "zombieToastSpawner");
    }

    // This ensures that any relevant entities initialised where a swamp tile is
    // behave appropriately
    private void initEntitiesOnSwampTiles() {
        List<SwampTile> st = getEntities(SwampTile.class);
        List<Position> stPos = st.stream().map(s -> s.getPosition()).collect(Collectors.toList());
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.stream().filter(e -> stPos.contains(e.getPosition())).forEach(e -> triggerOverlapEvent(e));
    }

    public void moveTo(PositionalEntity entity, Position position) {
        if (!canMoveTo(entity, position)) return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(position);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    public void moveTo(PositionalEntity entity, Direction direction) {
        if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction))) return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(Position.translateBy(entity.getPosition(), direction));
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    private void triggerMovingAwayEvent(PositionalEntity entity) {
        List<Runnable> callbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof MoveAwayable) {
                MoveAwayable eMovable = (MoveAwayable) e;
                callbacks.add(() -> eMovable.onMovedAway(this, entity));
            }
        });
        callbacks.forEach(callback -> {
            callback.run();
        });
    }

    private void triggerOverlapEvent(PositionalEntity entity) {
        List<Runnable> overlapCallbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof Overlappable) {
                Overlappable eOverlap = (Overlappable) e;
                overlapCallbacks.add(() -> eOverlap.onOverlap(this, entity));
            }
        });
        overlapCallbacks.forEach(callback -> {
            callback.run();
        });
    }

    public boolean canMoveTo(PositionalEntity entity, Position position) {
        return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
    }


    public Position dijkstraPathFind(Position src, Position dest, PositionalEntity entity) {
        // if inputs are invalid, don't move
        if (!nodes.containsKey(src) || !nodes.containsKey(dest))
        return src;

        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        prev.put(src, null);
        dist.put(src, 0);

        PriorityQueue<Position> q = new PriorityQueue<>((x, y) ->
            Integer.compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
        q.add(src);

        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (curr.equals(dest) || dist.get(curr) > 200) break;
            // check portal
            if (nodes.containsKey(curr)
            && nodes.get(curr).getEntities().stream().anyMatch(StandardPortal.class::isInstance)) {
                StandardPortal portal = nodes.get(curr).getEntities()
                    .stream()
                    .filter(StandardPortal.class::isInstance).map(StandardPortal.class::cast)
                    .collect(Collectors.toList())
                    .get(0);
                List<Position> teleportDest = portal.getDestPositions(this, entity);
                teleportDest.stream()
                .filter(p -> !visited.containsKey(p))
                .forEach(p -> {
                    dist.put(p, dist.get(curr));
                    prev.put(p, prev.get(curr));
                    q.add(p);
                });
                continue;
            }
            visited.put(curr, true);
            List<Position> neighbours = curr.getCardinallyAdjacentPositions()
            .stream()
            .filter(p -> !visited.containsKey(p))
            .filter(p -> !nodes.containsKey(p) || nodes.get(p).canMoveOnto(this, entity))
            .collect(Collectors.toList());

            neighbours.forEach(n -> {
                boolean checkSwampTile = true;
                if (entity instanceof Mercenary) {
                    Mercenary m = (Mercenary) entity;
                    checkSwampTile = !m.getReachedAdjacency();
                }
                int newDist = dist.get(curr) + (nodes.containsKey(n) && checkSwampTile ? nodes.get(n).getWeight() : 1);
                if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
                    q.remove(n);
                    dist.put(n, newDist);
                    prev.put(n, curr);
                    q.add(n);
                }
            });
        }
        Position ret = dest;
        if (prev.get(ret) == null || ret.equals(src)) return src;
        while (!prev.get(ret).equals(src)) {
            ret = prev.get(ret);
        }
        return ret;
    }

    public void removeNode(PositionalEntity entity) {
        Position p = entity.getPosition();
        if (nodes.containsKey(p)) {
            nodes.get(p).removeEntity(entity);
            if (nodes.get(p).size() == 0) {
                nodes.remove(p);
            }
        }
    }

    public void destroyEntity(PositionalEntity entity) {
        removeNode(entity);
        if (entity instanceof Destroyable) {
            Destroyable entityDestroyed = (Destroyable) entity;
            entityDestroyed.onDestroy(this);
        }
        if (entity instanceof Enemy) {
            player.setKillCount(player.getKillCount() + 1);
        }
    }

    public void addEntity(PositionalEntity entity) {
        addNode(new GraphNode(entity));
    }

    public void addNode(GraphNode node) {
        Position p = node.getPosition();

        if (!nodes.containsKey(p))
        nodes.put(p, node);
        else {
            GraphNode curr = nodes.get(p);
            curr.mergeNode(node);
            nodes.put(p, curr);
        }
    }

    public PositionalEntity getEntity(String id) {
        PositionalEntity res = null;
        for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
            List<PositionalEntity> es = entry.getValue().getEntities()
            .stream()
            .filter(e -> e.getId().equals(id))
            .collect(Collectors.toList());
            if (es != null && es.size() > 0) {
                res = es.get(0);
                break;
            }
        }
        return res;
    }

    public List<PositionalEntity> getEntities(Position p) {
        GraphNode node = nodes.get(p);
        return (node != null) ? node.getEntities() : new ArrayList<>();
    }

    public List<PositionalEntity> getEntities() {
        List<PositionalEntity> entities = new ArrayList<>();
        nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
        return entities;
    }

    public <T extends PositionalEntity> List<T> getEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public <T extends PositionalEntity> long countEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).count();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Position getPlayerPosition() {
        return player.getPosition();
    }

    public Position getPrevPlayerPosition() {
        return player.getPreviousDistinctPosition();
    }

    public void battle(Player player, Enemy enemy) {
        game.battle(player, enemy);
    }

    public void battleOlderPlayer(Player player, OlderPlayer enemy) {
        game.battleOlderPlayer(player, enemy);
    }

    public void unsubscribe(String id) {
        game.unsubscribe(id);
    }

    public int getTick() {
        return game.getTick();
    }

    public void timeTravelBack(int ticks) {
        GameBuilder builder = new GameBuilder();
        builder.buildGameFromState(game.getRewindedGameState(ticks), game, ticks);
        return;
    }
}
