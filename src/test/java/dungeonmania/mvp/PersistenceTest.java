package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class PersistenceTest {

    @Test
    @DisplayName("Test that game can be saved and loaded")
    public void testSaveandLoadGame() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_persistence", "c_generateTest");
        assertDoesNotThrow(() -> dmc.saveGame("testFileForTest"));
        assertTrue(dmc.allGames().contains("testFileForTest"));

        // Loading game
        DungeonResponse res = dmc.loadGame("testFileForTest");
        assertEquals(TestUtils.getEntities(res, "player").size(), 1);
        assertEquals(TestUtils.getEntities(res, "wood").size(), 1);
        assertEquals(TestUtils.getEntities(res, "invisibility_potion").size(), 1);
        assertEquals(TestUtils.getEntities(res, "sun_stone").size(), 1);
        assertEquals(TestUtils.getEntities(res, "treasure").size(), 1);
        assertEquals(TestUtils.getEntities(res, "spider").size(), 1);
        assertEquals(TestUtils.getEntities(res, "mercenary").size(), 1);
        assertEquals(TestUtils.getEntities(res, "player").get(0).getPosition(), new Position(1, 1));
        assertEquals(TestUtils.getEntities(res, "wood").get(0).getPosition(), new Position(2, 1));
        assertEquals(TestUtils.getEntities(res, "invisibility_potion").get(0).getPosition(), new Position(2, 0));
        assertEquals(TestUtils.getEntities(res, "sun_stone").get(0).getPosition(), new Position(4, 1));
        assertEquals(TestUtils.getEntities(res, "treasure").get(0).getPosition(), new Position(3, 1));
        assertEquals(TestUtils.getEntities(res, "spider").get(0).getPosition(), new Position(10, 1));
        assertEquals(TestUtils.getEntities(res, "mercenary").get(0).getPosition(), new Position(9, 1));
    }

    @Test
    @DisplayName("Test that a game that doesn't exist throws error")
    public void testInvalidFileName() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("invalidFile"));
    }

    @Test
    @DisplayName("Test potion duration")
    public void testPotionDuration() throws InvalidActionException {
        //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3/P_5
        //   S1_6   S1_5       P_6                              S2_2       S2_3
        //                     P_7                 P_8/S2_8     S2_1       S2_4
        //                                         S2_7         S2_6       S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invisibilityDuration", "c_potionsTest_invisibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invisibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));

        // Save file
        assertDoesNotThrow(() -> dmc.saveGame("testInvisibilityDuration"));
        assertTrue(dmc.allGames().contains("testInvisibilityDuration"));

        // Load file
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testInvisibilityDuration");

        // meet first spider, battle does not occur because the player is invisible
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = newDmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());
        // meet second spider and battle because the player is no longer invisible
        res = newDmc.tick(Direction.RIGHT);
        res = newDmc.tick(Direction.DOWN);
        res = newDmc.tick(Direction.DOWN);
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertTrue(res.getBattles().get(0).getRounds().size() >= 1);
    }

    @Test
    @DisplayName("Test the effects of the invincibility potion only last for a limited time and are persistent")
    public void invincibilityDurationPersistence() throws InvalidActionException {
        //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3
        //          P_5        S2_2         S2_3
        //          P_6        S2_1         S2_4
        //          P_7/S2_7   S2_6         S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invincibilityDuration", "c_potionsTest_invincibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invincibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // consume invincibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));

        // meet first spider, battle won immediately using invincibility_potion
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertEquals(1, res.getBattles().get(0).getRounds().size());

        // Save file
        assertDoesNotThrow(() -> dmc.saveGame("testInvincibilityDuration"));
        assertTrue(dmc.allGames().contains("testInvincibilityDuration"));

        // Load file
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testInvincibilityDuration");

        // meet second spider and battle without effects of potion
        res = newDmc.tick(Direction.DOWN);
        res = newDmc.tick(Direction.DOWN);
        res = newDmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        assertEquals(2, res.getBattles().size());
        assertTrue(res.getBattles().get(1).getRounds().size() >= 1);
        assertEquals(0, res.getBattles().get(1).getBattleItems().size());
    }

    @Test
    @DisplayName("Test sceptre effect duration")
    public void testSceptreDuration() throws InvalidActionException {
        // Pick up and consume invisibility potion
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistence", "c_generateTest");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        res = dmc.tick(Direction.LEFT);

        // Only one tick left
        assertDoesNotThrow(() -> dmc.saveGame("testSceptreDuration"));
        assertTrue(dmc.allGames().contains("testSceptreDuration"));
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testSceptreDuration");
        assertEquals(TestUtils.getInventory(res, "sceptre").size(), 1);
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntities(res, "mercenary").size(), 1);
        // Zero ticks of sceptre bribing effect left
        res = newDmc.tick(Direction.LEFT);
        assertEquals(TestUtils.getEntities(res, "mercenary").size(), 0);
    }

    @Test
    @DisplayName("Test bribing effect lasts")
    public void testBribing() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyBattle", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        assertDoesNotThrow(() -> dmc.saveGame("testBribing"));
        assertTrue(dmc.allGames().contains("testBribing"));
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testBribing");
        // walk into mercenary, a battle does not occur
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Test goals are maintained")
    public void testGoalPersistence() {
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

        assertDoesNotThrow(() -> dmc.saveGame("testGoals1"));
        assertTrue(dmc.allGames().contains("testGoals1"));
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testGoals1");

        // move boulder onto switch
        res = newDmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        // pickup treasure
        res = newDmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));

        assertDoesNotThrow(() -> newDmc.saveGame("testGoals2"));
        assertTrue(dmc.allGames().contains("testGoals2"));
        DungeonManiaController newDmcTwo;
        newDmcTwo = new DungeonManiaController();
        res = newDmcTwo.loadGame("testGoals2");

        // move to exit
        res = newDmcTwo.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Test weapon duration")
    public void testWeaponDurationPersistence() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceWeaponDuration", "c_zombieTest_toastDestruction");
        assertEquals(2, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        final String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        // System.out.println("spawnerId: " + spawnerId);
        // cardinally adjacent: true, has sword: false
        assertThrows(InvalidActionException.class, () -> dmc.interact(spawnerId));
        assertEquals(2, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // pick up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // cardinally adjacent: false, has sword: true
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(spawnerId)
        );
        assertEquals(2, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // move right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true, but invalid_id
        assertThrows(IllegalArgumentException.class, () ->
                dmc.interact("random_invalid_id")
        );
        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(1, TestUtils.countType(res, "zombie_toast_spawner"));
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        assertDoesNotThrow(() -> dmc.saveGame("testWeaponDuration"));
        assertTrue(dmc.allGames().contains("testWeaponDuration"));
        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testWeaponDuration");

        // move right
        res = newDmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        final String spawnerId2 = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        res = assertDoesNotThrow(() -> newDmc.interact(spawnerId2));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @DisplayName("Test doors stay open")
    public void testDoorsStayOpen() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // walk through door and check key is gone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        assertDoesNotThrow(() -> dmc.saveGame("testDoorOpen"));
        assertTrue(dmc.allGames().contains("testDoorOpen"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testDoorOpen");
        res = dmc.tick(Direction.RIGHT);
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Test persistence works on swamp tile impacting spider")
    public void testPersistenceSwampSpider() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_spider", "c_swampTileTest");

            assertEquals(new Position(6, 2), getSpiderPos(res));

            // Move
            res = dmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));
            res = dmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));
            assertDoesNotThrow(() -> dmc.saveGame("testSpiderPersistenceSwamp"));
            assertTrue(dmc.allGames().contains("testSpiderPersistenceSwamp"));

            DungeonManiaController newDmc;
            newDmc = new DungeonManiaController();
            res = newDmc.loadGame("testSpiderPersistenceSwamp");
            res = newDmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));

            res = newDmc.tick(Direction.DOWN);
            // Spider goes up now
            assertEquals(new Position(6, 1), getSpiderPos(res));
    }

    @Test
    @DisplayName("Test swamp tile impacts zombie")
    public void testZombieSwampPersistence() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombie", "c_swampTileTest");

        assertEquals(1, getZombies(res).size());

        // Zombie doesn't move for 5 ticks
        boolean zombieMoved = false;
        Position prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(!zombieMoved);

        assertDoesNotThrow(() -> dmc.saveGame("testZombiePersistenceSwamp"));
        assertTrue(dmc.allGames().contains("testZombiePersistenceSwamp"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testZombiePersistenceSwamp");

        // They move at some point
        prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = newDmc.tick(Direction.UP);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);
    }

    @Test
    @DisplayName("Test swamp tile does not impact adjacent ally that's on a swamp tile with persistence")
    public void testAlliedAdjacentMercPersistence() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_alliedAdjacentMerc2", "c_swampTileTest_allies");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(6, 2), getMercPos(res));
        // Allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        // On swamp tile
        assertEquals(new Position(5, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));

        assertDoesNotThrow(() -> dmc.saveGame("testAllyAdjMercPersistenceSwamp"));
        assertTrue(dmc.allGames().contains("testAllyAdjMercPersistenceSwamp"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testAllyAdjMercPersistenceSwamp");

        // Player at (3, 2)
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));

        // // Player at (4, 2), Ally is now adjacent and not stuck on swamp tile
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 2), getMercPos(res));

        // Go back to swamp tile
        res = newDmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 2), getMercPos(res));

        res = newDmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));

        res = newDmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 2), getMercPos(res));
    }

    @Test
    @DisplayName(
        "Persistence - Test placing a bomb cardinally adjacent to an active switch"
    )
    public void placeCardinallyActive() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinallyActive", "c_bombTest_placeCardinallyActive");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        assertDoesNotThrow(() -> dmc.saveGame("testBombActiveSwitch"));
        assertTrue(dmc.allGames().contains("testBombActiveSwitch"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testBombActiveSwitch");

        // Place Cardinally Adjacent
        res = newDmc.tick(Direction.RIGHT);
        res = newDmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Check Bomb exploded
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "boulder").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());
        assertEquals(1, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test spider_spawn_interval is persistent")
    public void testSpawnRatePersistence() {

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spiderTest_spawnRate", "c_spiderTest_spawnRate5");

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertDoesNotThrow(() -> dmc.saveGame("testSpiderSpawnRate"));
        assertTrue(dmc.allGames().contains("testSpiderSpawnRate"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testSpiderSpawnRate");

        res = newDmc.tick(Direction.UP);
        res = newDmc.tick(Direction.UP);
        res = newDmc.tick(Direction.UP);
        assertEquals(1 - getNumKilledSpiders(res), TestUtils.getEntities(res, "spider").size());
    }

    @Test
    @DisplayName("Test MidnightArmour lasts forever - Persistence")
    public void testMidnightArmourPersistence() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_midnightArmourForever", "c_testMidnightArmourEffect");
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertDoesNotThrow(() -> dmc.saveGame("testMidnightArmourPersistence"));
        assertTrue(dmc.allGames().contains("testMidnightArmourPersistence"));

        DungeonManiaController newDmc;
        newDmc = new DungeonManiaController();
        res = newDmc.loadGame("testMidnightArmourPersistence");

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
            res = dmc.tick(Direction.DOWN);
        }
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
    }

    private int getNumKilledSpiders(DungeonResponse res) {
        // If we have had x battles and the player is still alive, we must have killed x spiders
        return res.getBattles().size();
    }

    private Position getSpiderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "spider").get(0).getPosition();
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private List<EntityResponse> getZombies(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast");
    }
}
