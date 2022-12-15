package dungeonmania.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleFacade;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.goals.AndGoal;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalStrategy;
import dungeonmania.goals.OrGoal;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class JSONFactory {

    public static JSONObject createJSON(Object entity) {
        return constructJSON(entity);
    }

    public static List<JSONObject> allEntitiesToJSON(Game game) {
        List<JSONObject> entitiesJson = game.getEntities().stream()
                                                    .map(e -> constructJSON(e))
                                                    .collect(Collectors.toList());
        return entitiesJson;
    }

    public static JSONObject gameToJSON(Game game) {
        JSONObject gameJson = new JSONObject();
        gameJson.put("entities", allEntitiesToJSON(game));
        gameJson.put("goal-condition", constructJSON(game.getGoals()));
        gameJson.put("player", constructJSON(game.getPlayer()));
        gameJson.put("tickCount", game.getTick());
        gameJson.put("initialTreasureCount", game.getInitialTreasureCount());
        gameJson.put("battleFacade", battleFacadeToJSON(game.getBattleFacade()));
        gameJson.put("isInTick", game.getIsInTick());

        return gameJson;
    }

    public static JSONObject entityToJSON(PositionalEntity entity) {
        JSONObject entityJson = new JSONObject();
        Position position = entity.getPosition();
        Position previousPosition = entity.getPreviousPosition();
        Position previousDistinctPosition = entity.getPreviousDistinctPosition();
        Direction facing = entity.getFacing();
        entityJson.put("x", position != null ? position.getX() : 0);
        entityJson.put("y", position != null ? position.getY() : 0);
        entityJson.put("position", constructJSON(position));
        entityJson.put("previousPosition", constructJSON(previousPosition));
        entityJson.put("previousDistinctPosition", constructJSON(previousDistinctPosition));
        entityJson.put("facing", constructJSON(facing));
        entityJson.put("entityId", entity.getId());
        entityJson.put("type", entity.getType());
        return entityJson;
    }

    public static JSONObject entityToJSON(Entity entity) {
        JSONObject entityJson = new JSONObject();
        entityJson.put("x", 0);
        entityJson.put("y", 0);
        entityJson.put("entityId", entity.getId());
        entityJson.put("type", entity.getType());
        return entityJson;
    }

    public static JSONObject enemyToJSON(Enemy enemy) {
        JSONObject json = entityToJSON(enemy);
        json.put("swampTileDuration", enemy.getSwampTileDuration());
        json.put("battleStatistics", constructJSON(enemy.getBattleStatistics()));
        return json;
    }

    public static JSONObject switchToJSON(Switch switchObj) {
        JSONObject json = entityToJSON(switchObj);
        json.put("activated", switchObj.isActivated());
        return json;
    }

    public static List<JSONObject> battleFacadeToJSON(BattleFacade battleFacade) {
        return battleFacade.getBattleResponses().stream().map(
            round -> constructJSON(round)).collect(Collectors.toList());
    }

    public static JSONObject doorToJSON(Door door) {
        JSONObject json = entityToJSON(door);
        json.put("open", door.isOpen());
        json.put("key", door.getNumber());
        return json;
    }

    public static List<JSONObject> inventoryToJSON(Inventory inventory) {
        return inventory.getItems().stream().map(item -> constructJSON(item)).collect(Collectors.toList());
    }

    public static JSONObject potionToJSON(Potion potion) {
        JSONObject json = entityToJSON(potion);
        json.put("duration", potion.getDuration());
        return json;
    }

    public static JSONObject playerToJSON(Player player) {
        JSONObject json = entityToJSON(player);
        json.put("battleStatistics", constructJSON(player.getBattleStatistics()));
        json.put("inventory", inventoryToJSON(player.getInventory()));
        json.put("queue", queueToJson(player.getQueue()));
        json.put("inEffective", (player.getInEffective() == null ? "null" : constructJSON(player.getInEffective())));
        json.put("nextTrigger", player.getNextTrigger());
        json.put("killCount", player.getKillCount());
        json.put("state", player.getState());
        return json;
    }

    public static JSONObject breakableItemToJSON(Entity item) {
        JSONObject json;
        if (item instanceof Bow || item instanceof Shield) {
            json = entityToJSON(item);
        } else {
            json = entityToJSON((PositionalEntity) item);
        }
        json.put("durability", ((BreakableItem) item).getDurability());
        return json;
    }

    public static JSONObject bombToJSON(Bomb bomb) {
        JSONObject json = entityToJSON(bomb);
        json.put("state", bomb.getState().toString());
        json.put("radius", bomb.getRadius());
        return json;
    }

    public static JSONObject keyToJSON(Key key) {
        JSONObject json = entityToJSON(key);
        json.put("key", key.getNumber());
        return json;
    }

    public static JSONObject mercenaryToJSON(Mercenary mercenary) {
        JSONObject json = enemyToJSON(mercenary);
        json.put("bribeAmount", mercenary.getBribeAmount());
        json.put("bribeRadius", mercenary.getBribeRadius());
        json.put("allied", mercenary.getBribed());
        json.put("remainingSceptreEffect", mercenary.getRemainingSceptreEffect());
        json.put("reachedAdjacency", mercenary.getReachedAdjacency());
        return json;
    }

    public static JSONObject spiderToJSON(Spider spider) {
        JSONObject json = enemyToJSON(spider);
        json.put("movementTrajectory", movementTrajectoryToJson(spider.getMovementTrajectory()));
        json.put("nextPositionElement", spider.getNextPositionElement());
        json.put("forward", spider.getForward());
        return json;
    }

    public static JSONObject goalToJSON(Goal goal) {
        return constructJSON(goal.getGoals());
    }

    public static JSONObject goalStrategyToJSON(GoalStrategy goals) {
        JSONObject json = new JSONObject();
        switch (goals.getClass().getSimpleName().toLowerCase()) {
            case "andgoal":
                json.put("goal", "AND");
                AndGoal andGoal = (AndGoal) goals;
                json.put("subgoals", new ArrayList<JSONObject>(Arrays.asList(
                    constructJSON(andGoal.getGoalOne()), constructJSON(andGoal.getGoalTwo()))
                ));
                return json;
            case "orgoal":
                json.put("goal", "OR");
                OrGoal orGoal = (OrGoal) goals;
                json.put("subgoals", new ArrayList<JSONObject>(Arrays.asList(
                    constructJSON(orGoal.getGoalOne()), constructJSON(orGoal.getGoalTwo()))
                ));
                return json;
            case "bouldergoal":
                json.put("goal", "boulders");
                return json;
            case "enemiesgoal":
                json.put("goal", "enemies");
                return json;
            case "treasuregoal":
                json.put("goal", "treasure");
                return json;
            case "exitgoal":
                json.put("goal", "exit");
                return json;
            default:
                return null;
        }
    }

    public static JSONObject portalToJSON(StandardPortal portal) {
        JSONObject json = entityToJSON(portal);
        json.put("colour", portal.getColor());
        return json;
    }

    public static JSONObject itemResponseToJSON(ItemResponse item) {
        JSONObject json = new JSONObject();
        json.put("id", item.getId());
        json.put("type", item.getType());
        return json;
    }

    public static JSONObject battleResponseToJSON(BattleResponse response) {
        JSONObject json = new JSONObject();
        json.put("enemy", response.getEnemy());
        json.put("initialEnemyHealth", response.getInitialEnemyHealth());
        json.put("initialPlayerHealth", response.getInitialPlayerHealth());
        json.put("battleItems", response.getBattleItems().stream().map(
            item -> constructJSON(item)).collect(Collectors.toList()));
        json.put("rounds", response.getRounds().stream().map(
            round -> constructJSON(round)).collect(Collectors.toList()));
        return json;
    }

    public static JSONObject roundResponseToJSON(RoundResponse round) {
        JSONObject json = new JSONObject();
        json.put("deltaPlayerHealth", round.getDeltaCharacterHealth());
        json.put("deltaEnemyHealth", round.getDeltaEnemyHealth());
        return json;
    }

    public static JSONObject swampTileToJSON(SwampTile swamp) {
        JSONObject json = entityToJSON(swamp);
        json.put("movement_factor", swamp.getMovementFactor());
        return json;
    }

    public static JSONObject positionToJson(Position pos) {
        if (pos == null) {
            return null;
        }
        JSONObject json = new JSONObject();
        json.put("x", pos.getX());
        json.put("y", pos.getY());
        json.put("layer", pos.getLayer());
        return json;
    }

    public static JSONObject directionToJSON(Direction dir) {
        if (dir == null) {
            return null;
        }
        return positionToJson(dir.getOffset());
    }

    public static JSONObject battleStatisticsToJSON(BattleStatistics stats) {
        JSONObject json = new JSONObject();
        json.put("health", stats.getHealth());
        json.put("attack", stats.getAttack());
        json.put("defence", stats.getDefence());
        json.put("magnifier", stats.getMagnifier());
        json.put("reducer", stats.getReducer());
        json.put("invincible", stats.isInvincible());
        json.put("enabled", stats.isEnabled());
        return json;
    }

    public static List<JSONObject> movementTrajectoryToJson(List<Position> movementTrajectory) {
        if (movementTrajectory.size() == 0) {
            List<JSONObject> filler = new ArrayList<JSONObject>();
            return filler;
        }
        return movementTrajectory.stream().map(pos -> positionToJson(pos)).collect(Collectors.toList());
    }

    public static List<JSONObject> queueToJson(Queue<Potion> queue) {
        if (queue.size() == 0) {
            List<JSONObject> filler = new ArrayList<JSONObject>();
            return filler;
        }
        return queue.stream().map(potion -> potionToJSON(potion)).collect(Collectors.toList());
    }

    public static JSONObject constructJSON(Object entity) {
        if (entity == null) {
            return null;
        }
        switch (entity.getClass().getSimpleName().toLowerCase()) {
            case "game":
                return gameToJSON((Game) entity);
            case "door":
                return doorToJSON((Door) entity);
            case "player":
                return playerToJSON((Player) entity);
            case "switch":
                return switchToJSON((Switch) entity);
            case "sword":
            case "shield":
            case "bow":
                return breakableItemToJSON((Entity) entity);
            case "bomb":
                return bombToJSON((Bomb) entity);
            case "key":
                return keyToJSON((Key) entity);
            case "invisibilitypotion":
            case "invincibilitypotion":
                return potionToJSON((Potion) entity);
            case "mercenary":
                return mercenaryToJSON((Mercenary) entity);
            case "spider":
                return spiderToJSON((Spider) entity);
            case "zombietoast":
            case "hydra":
                return enemyToJSON((Enemy) entity);
            case "goal":
                return goalToJSON((Goal) entity);
            case "andgoal":
            case "orgoal":
            case "exitgoal":
            case "treasuregoal":
            case "bouldergoal":
            case "enemiesgoal":
                return goalStrategyToJSON((GoalStrategy) entity);
            case "standardportal":
                return portalToJSON((StandardPortal) entity);
            case "roundresponse":
                return roundResponseToJSON((RoundResponse) entity);
            case "itemresponse":
                return itemResponseToJSON((ItemResponse) entity);
            case "battleresponse":
                return battleResponseToJSON((BattleResponse) entity);
            case "swamptile":
                return swampTileToJSON((SwampTile) entity);
            case "position":
                return positionToJson((Position) entity);
            case "direction":
                return directionToJSON((Direction) entity);
            case "battlestatistics":
                return battleStatisticsToJSON((BattleStatistics) entity);
            case "midnightarmour":
            case "sceptre":
                return entityToJSON((Entity) entity);
            default:
                return entityToJSON((PositionalEntity) entity);
        }
    }

    public static Position jsonToPosition(JSONObject json) {
        return new Position(json.getInt("x"), json.getInt("y"));
    }

    public static List<ItemResponse> jsonToBattleItems(JSONArray battleItems) {
        List<ItemResponse> battle = new ArrayList<>();
        Iterator<Object> iterator = battleItems.iterator();
        while (iterator.hasNext()) {
            JSONObject item = (JSONObject) iterator.next();
            battle.add(new ItemResponse(item.getString("id"), item.getString("type")));
        }
        return battle;
    }

    public static List<RoundResponse> jsonToRounds(JSONArray jsonRounds) {
        List<RoundResponse> rounds = new ArrayList<>();
        Iterator<Object> iterator = jsonRounds.iterator();
        while (iterator.hasNext()) {
            JSONObject item = (JSONObject) iterator.next();
            rounds.add(new RoundResponse(item.getDouble("deltaPlayerHealth"), item.getDouble("deltaEnemyHealth")));
        }
        return rounds;
    }

    public static List<BattleResponse> jsonToBattleResponses(JSONArray battleRounds) {
        Iterator<Object> iterator = battleRounds.iterator();
        List<BattleResponse> battleResponses = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject battleRound = (JSONObject) iterator.next();
            String enemy = battleRound.getString("enemy");
            double initialPlayerHealth = battleRound.getDouble("initialPlayerHealth");
            double initialEnemyHealth = battleRound.getDouble("initialEnemyHealth");
            List<ItemResponse> battleItems = JSONFactory.jsonToBattleItems(
                battleRound.getJSONArray("battleItems"));
            List<RoundResponse> rounds = JSONFactory.jsonToRounds(battleRound.getJSONArray("rounds"));
            battleResponses.add(new BattleResponse(
                enemy,
                rounds,
                battleItems,
                initialPlayerHealth,
                initialEnemyHealth
            ));
        }
        return battleResponses;
    }

    public static List<Position> jsonToMovementPattern(JSONArray movementTrajectory) {
        List<Position> newMovementPattern = new ArrayList<Position>();
        movementTrajectory.forEach(json -> newMovementPattern.add(JSONFactory.jsonToPosition((JSONObject) json)));
        return newMovementPattern;
    }
}
