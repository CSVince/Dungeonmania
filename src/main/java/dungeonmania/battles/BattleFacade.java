package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.BreakableItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.OlderPlayer;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Hydra;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = enemy.getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);


        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = getBuff(player, battleItems);

        // 2. Battle the two stats
        BattleStatistics playerBaseStatistics = player.getBattleStatistics();
        BattleStatistics enemyBaseStatistics = enemy.getBattleStatistics();
        BattleStatistics playerBattleStatistics = BattleStatistics.applyBuff(playerBaseStatistics, playerBuff);
        BattleStatistics enemyBattleStatistics = enemyBaseStatistics;
        if (!playerBattleStatistics.isEnabled() || !enemyBaseStatistics.isEnabled())
            return;

        List<BattleRound> rounds;
        if (enemy instanceof Hydra) {
            Hydra hydra = (Hydra) enemy;
            rounds = BattleStatistics.hydraBattle(playerBattleStatistics,
                enemyBattleStatistics, hydra);
        } else {
            rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);
        }

        // 3. update health to the actual statistics
        player.setHealth(playerBattleStatistics.getHealth());
        enemy.setHealth(enemyBattleStatistics.getHealth());

        // 4. call to decrease durability of items
        decreaseDurabilities(battleItems, game);

        // 5. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(
                enemyString,
                rounds.stream()
                    .map(ResponseBuilder::getRoundResponse)
                    .collect(Collectors.toList()),
                battleItems.stream()
                        .map(Entity.class::cast)
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth,
                initialEnemyHealth));
    }

    public void battleOlderPlayer(Game game, Player player, OlderPlayer enemy) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = enemy.getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);

        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = getBuff(player, battleItems);

        List<BattleItem> enemyBattleItems = new ArrayList<>();
        BattleStatistics enemyBuff = getBuff(enemy, enemyBattleItems);

        // 2. Battle the two stats
        BattleStatistics playerBaseStatistics = player.getBattleStatistics();
        BattleStatistics enemyBaseStatistics = enemy.getBattleStatistics();
        BattleStatistics playerBattleStatistics = BattleStatistics.applyBuff(playerBaseStatistics, playerBuff);
        BattleStatistics enemyBattleStatistics = BattleStatistics.applyBuff(enemyBaseStatistics, enemyBuff);
        if (!playerBattleStatistics.isEnabled() || !enemyBattleStatistics.isEnabled()) {
            return;
        }

        List<BattleRound> rounds;
        rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);

        // 3. update health to the actual statistics
        player.setHealth(playerBattleStatistics.getHealth());
        enemy.setHealth(enemyBattleStatistics.getHealth());

        // 4. call to decrease durability of items
        decreaseDurabilities(battleItems, game);
        decreaseDurabilities(enemyBattleItems, game);

        // 5. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(
                enemyString,
                rounds.stream()
                    .map(ResponseBuilder::getRoundResponse)
                    .collect(Collectors.toList()),
                battleItems.stream()
                        .map(Entity.class::cast)
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth,
                initialEnemyHealth));
    }

    public BattleStatistics getBuff(Player player, List<BattleItem> battleItems) {
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);
        Potion effectivePotion = player.getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = player.applyStateBuff(playerBuff);
        } else {
            playerBuff = player.applyBattleItems(playerBuff, battleItems);
        }
        return playerBuff;
    }

    public void decreaseDurabilities(List<BattleItem> battleItems, Game game) {
        battleItems.stream()
            .filter(i -> i instanceof InventoryItem)
            .filter(i -> i instanceof BreakableItem)
            .forEach(i -> ((BreakableItem) i).use(game));
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }

    public void setBattleResponses(List<BattleResponse> battleResponses) {
        this.battleResponses = battleResponses;
    }
}
