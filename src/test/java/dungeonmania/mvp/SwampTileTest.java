package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class SwampTileTest {
    @Test
    @DisplayName("Test the player is not impacted by swamp tile")
    public void testPlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_swampTileTest_player", "c_swampTileTest");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result, player not impacted by swamp tile
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(),
        new Position(1, 2), false);

        // move player downward
        DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @DisplayName("Test swamp tile impacts spider")
    public void testSpider() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_spider", "c_swampTileTest");

            assertEquals(new Position(6, 2), getSpiderPos(res));

            // Move
            res = dmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));
            res = dmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));
            res = dmc.tick(Direction.DOWN);
            assertEquals(new Position(6, 2), getSpiderPos(res));

            res = dmc.tick(Direction.DOWN);
            // Spider goes up now
            assertEquals(new Position(6, 1), getSpiderPos(res));
    }

    @Test
    @DisplayName("Test swamp tile impacts zombie")
    public void testZombie() {
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

        // They move at some point
        prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);
    }

    @Test
    @DisplayName("Test swamp tile impacts unallied mercenary")
    public void testUnalliedMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_unalliedMercenary", "c_swampTileTest");

            assertEquals(new Position(8, 2), getMercPos(res));

            // Move
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(8, 2), getMercPos(res));
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(8, 2), getMercPos(res));
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(8, 2), getMercPos(res));

            res = dmc.tick(Direction.RIGHT);
            // Merc goes left
            assertEquals(new Position(7, 2), getMercPos(res));
    }

    @Test
    @DisplayName("Test swamp tile impacts allied non adjacent mercenary")
    public void testAlliedNonAdjacentMerc() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_alliedNonAdjacentMerc", "c_swampTileTest_allies");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(9, 2), getMercPos(res));
        // Allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(new Position(9, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(9, 2), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(9, 2), getMercPos(res));

        // Out of swamp, merc goes left
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(8, 2), getMercPos(res));
    }

    @Test
    @DisplayName("Test swamp tile does not impact adjacent ally")
    public void testAlliedAdjacentMerc() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_alliedAdjacentMerc", "c_swampTileTest_allies");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(3, 1), getMercPos(res));
        // Allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        // Now adjacent
        assertEquals(new Position(2, 1), getMercPos(res));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 1), getMercPos(res));

        // Ally not stuck on swamp tile
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 2), getMercPos(res));

        // Ally not stuck on swamp tile
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getMercPos(res));
    }

    @Test
    @DisplayName("Test swamp tile does not impact adjacent ally that's on a swamp tile")
    public void testAlliedAdjacentMerc2() {
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

        // Player at (3, 2)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));

        // // Player at (4, 2), Ally is now adjacent and not stuck on swamp tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 2), getMercPos(res));

        // Go back to swamp tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 2), getMercPos(res));
    }

    @Test
    @DisplayName("Test swamp tile impacts mercenary pathfinding")
    public void mercPathFinding() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_mercPathFinding", "c_swampTileTest");

        assertEquals(new Position(6, 2), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);

        // Avoids swamp tile and goes somewhere else
        assertNotEquals(new Position(5, 2), getMercPos(res));

        assertEquals(new Position(6, 3), getMercPos(res));
    }

    @Test
    @DisplayName("Test swamp tile impacts ally pathfinding")
    public void allyPathFinding() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_allyPathFinding", "c_swampTileTest_allies");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(6, 2), getMercPos(res));
        // Allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Avoids swamp tile and goes somewhere else
        assertNotEquals(new Position(5, 2), getMercPos(res));

        assertEquals(new Position(6, 3), getMercPos(res));
    }

    @Test
    @DisplayName("Swamp tile with factor 0 has no impact")
    public void factor0() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_factor0", "c_swampTileTest");

        assertEquals(new Position(6, 2), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);

        // Goes to swamp tile and goes somewhere else
        assertEquals(new Position(5, 2), getMercPos(res));
    }

    @Test
    @DisplayName("Battle on swamp tile")
    public void battle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_swampTileTest_battle", "c_swampTileTest_battle");

        assertEquals(new Position(6, 2), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);

        // Battle on swamp tile
        assertEquals(new Position(5, 2), getMercPos(res));
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
