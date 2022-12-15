package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EnemyGoalTest {
    @Test
    public void basicEnemiesTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemy", "c_generateTest");
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        System.out.println(TestUtils.getGoals(res));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void basicEnemiesTestWithTwoEnemies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_multipleEnemies", "c_twoEnemyGoal");
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        System.out.println(TestUtils.getGoals(res));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void basicEnemiesTestWithFiveEnemies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_multipleEnemies", "c_fiveEnemyGoal");
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 5);

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 4);

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 3);

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 1);

        // Move to the right and kill last spider
        res = dmc.tick(Direction.RIGHT);
        System.out.println(TestUtils.getGoals(res));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void exitLastTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemyAndExit", "c_generateTest");
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Move to the right and kill one spider
        res = dmc.tick(Direction.RIGHT);
        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        // Move to exit
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        res = dmc.tick(Direction.DOWN);
        assertFalse(TestUtils.getGoals(res).contains(":exit"));
    }

    @Test
    public void spawnerKillNoTargetTest() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalWithSpawner", "c_enemyGoalZero");
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // cardinally adjacent: true, has sword: false
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // pick up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // cardinally adjacent: false, has sword: true
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // move right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true, but invalid_id
        assertThrows(IllegalArgumentException.class, () ->
                dmc.interact("random_invalid_id")
        );
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void spawnerKillTargetTest() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalWithSpawner", "c_generateTest");
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        assertEquals(1, TestUtils.countType(res, "zombie_toast_spawner"));
        assertEquals(1, TestUtils.countType(res, "spider"));

        // cardinally adjacent: true, has sword: false
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // pick up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // cardinally adjacent: false, has sword: true
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // move right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true, but invalid_id
        assertThrows(IllegalArgumentException.class, () ->
                dmc.interact("random_invalid_id")
        );
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertEquals(1, TestUtils.countType(res, "spider"));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void complexGoalAndAllTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalTestWithEnemies_andAll", "c_generateTest");

        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        // pickup treasure
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        // move to exit
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    public void complexGoalOrAllTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalTestWithEnemies_orAll", "c_generateTest");

        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move onto exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
