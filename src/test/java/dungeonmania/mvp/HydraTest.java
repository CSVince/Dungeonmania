package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.EntityResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HydraTest {

    public void assertHydraIncreaseBattleCalculations(
            BattleResponse battle, boolean enemyDies, String configFilePath, String enemyType) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = battle.getInitialPlayerHealth(); // Should come from config
        double enemyHealth = battle.getInitialEnemyHealth(); // Should come from config
        double enemyAttack = Double
                .parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(-enemyAttack / 10, round.getDeltaCharacterHealth(), 1);

            // Hydra gains back 2 health every round
            assertEquals(round.getDeltaEnemyHealth(), 2.0);

            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        // Battle ends after 10 rounds, hydra has 10 + 10*2 health
        assertEquals(enemyHealth, 30);

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    public void assertBattleCalculations(
        BattleResponse battle, boolean enemyDies, String configFilePath, String enemyType) {
    List<RoundResponse> rounds = battle.getRounds();
    double playerHealth = battle.getInitialPlayerHealth(); // Should come from config
    double enemyHealth = battle.getInitialEnemyHealth(); // Should come from config
    double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
    double enemyAttack = Double
            .parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_attack", configFilePath));

    for (RoundResponse round : rounds) {
        assertEquals(-enemyAttack / 10, round.getDeltaCharacterHealth(), 1);
        assertEquals(-playerAttack / 5, round.getDeltaEnemyHealth(), 5);
        // Delta health is negative
        enemyHealth += round.getDeltaEnemyHealth();
        playerHealth += round.getDeltaCharacterHealth();
    }

    if (enemyDies) {
        assertTrue(enemyHealth <= 0);
    } else {
        assertTrue(playerHealth <= 0);
    }
}

    @Test
    @DisplayName("Test hydra moves randomly")
    public void hydraMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_hydraTest_movement", "c_hydraTest_playerDiesWhenBattleHydra");

        assertEquals(1, getHydra(res).size());

        // Teams may assume that random movement includes choosing to stay still, so we should just
        // check that they do move at least once in a few turns
        boolean hydraMoved = false;
        Position prevPosition = getHydra(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
            if (!prevPosition.equals(getHydra(res).get(0).getPosition())) {
                hydraMoved = true;
                break;
            }
        }
        assertTrue(hydraMoved);
    }

    @Test
    @DisplayName("Test hydra health increase works and hydra wins")
    public void testPlayerDiesWhenBattleHydra() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericHydraSequence(
                controller, "c_hydraTest_playerDiesWhenBattleHydra");
        List<EntityResponse> entities = postBattleResponse.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "hydra") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "player") == 0);
    }

    @Test
    @DisplayName("Test hydra health increase works and player wins")
    public void testHydraDiesWhenBattleHydra() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericHydraSequence(
                controller, "c_hydraTest_hydraDiesWhenBattleHydra");
        List<EntityResponse> entities = postBattleResponse.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "hydra") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "player") == 1);
    }

    @Test
    @DisplayName("Test basic health calculations hydra - hydra wins")
    public void testRoundCalculationsHydraWins() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericHydraSequence(
                controller, "c_hydraTest_playerDiesWhenBattleHydra");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertHydraIncreaseBattleCalculations(battle, false, "c_hydraTest_playerDiesWhenBattleHydra", "hydra");
    }

    @Test
    @DisplayName("Test basic health calculations hydra - player wins")
    public void testRoundCalculationsPlayerWins() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericHydraSequence(
                controller, "c_hydraTest_hydraDiesWhenBattleHydra");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations(battle, true, "c_hydraTest_hydraDiesWhenBattleHydra", "hydra");
    }

    private List<EntityResponse> getHydra(DungeonResponse res) {
        return TestUtils.getEntities(res, "hydra");
    }
}
