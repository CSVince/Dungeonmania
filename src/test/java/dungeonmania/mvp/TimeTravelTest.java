package dungeonmania.mvp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static org.junit.jupiter.api.Assertions.*;

public class TimeTravelTest {
    @Test
    @DisplayName("Time turner can be picked up and used for one tick")
    public void timeTurnOneTick() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.rewind(1);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), pos);
    }

    @Test
    @DisplayName("Time turner can be picked up and used for five ticks")
    public void timeTurnFiveTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        Position playerPos = TestUtils.getPlayerPos(res);
        Position boulderPos = TestUtils.getEntityPos(res, "boulder");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        res = dmc.rewind(5);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos);
        assertEquals(TestUtils.getEntityPos(res, "boulder"), boulderPos);
    }

    @Test
    @DisplayName("Time travelling portal reverts game back to initial state")
    public void portalInitialGame() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        Position playerPos = TestUtils.getPlayerPos(res);
        Position boulderPos = TestUtils.getEntityPos(res, "boulder");
        Position spiderPos = TestUtils.getEntityPos(res, "spider");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos);
        assertEquals(TestUtils.getEntityPos(res, "boulder"), boulderPos);
        assertEquals(TestUtils.getEntityPos(res, "spider"), spiderPos);
    }

    @Test
    @DisplayName("Time travelling portal reverts game back to 30 ticks ago")
    public void portalThirtyBack() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        Position playerPos = null;
        Position boulderPos = null;
        Position spiderPos = null;
        for (int i = 0; i < 15; i++) {
            if (i == 2) {
                // The player will enter the portal on the 31st tick.
                playerPos = TestUtils.getPlayerPos(res);
                boulderPos = TestUtils.getEntityPos(res, "boulder");
                spiderPos = TestUtils.getEntityPos(res, "spider");
            }
            res = dmc.tick(Direction.DOWN);
        }
        for (int i = 0; i < 17; i++) {
            res = dmc.tick(Direction.UP);
        }
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos);
        assertEquals(TestUtils.getEntityPos(res, "boulder"), boulderPos);
        assertEquals(TestUtils.getEntityPos(res, "spider"), spiderPos);
    }

    @Test
    @DisplayName("Old player mimics current player's path")
    public void oldPlayerPath() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        Position initBoulderPos = TestUtils.getEntityPos(res, "boulder");
        Position playerPos0 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.RIGHT);
        Position playerPos1 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.RIGHT);
        Position playerPos2 = TestUtils.getPlayerPos(res);
        // At this point, the player has pushed the boulder
        Position boulderPos = TestUtils.getEntityPos(res, "boulder");
        assertNotEquals(boulderPos, initBoulderPos);
        res = dmc.tick(Direction.UP);
        Position playerPos3 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.UP);
        Position playerPos4 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.UP);
        Position playerPos5 = TestUtils.getPlayerPos(res);

        res = dmc.rewind(5);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos0);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos1);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos2);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos3);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos4);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos5);
        assertEquals(TestUtils.getEntityPos(res, "boulder"), boulderPos);
    }

    @Test
    @DisplayName("Same interactions are carried out")
    public void samePlayerInteractions() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() ->  dmc.build("bow"));
        res = dmc.rewind(5);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // At this point the old player's bow should be built
        // It is, I tested via system prints but don't know how to actually test
    }

    @Test
    @DisplayName("Exceptions are thrown properly")
    public void exceptionHandling() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(100));
        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(-1));
        assertDoesNotThrow(() -> dmc.rewind(1));
    }

    @Test
    @DisplayName("Mercenaries follow current player")
    public void mercenariesFollowCurrent() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = assertDoesNotThrow(() -> dmc.rewind(5));

        // Manufacture a movement pattern that will show the mercenary
        // moving towards current player
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        Position mercPos = TestUtils.getEntityPos(res, "mercenary");
        int initDistanceBetweenOlderPlayerAndMerc = Position.calculatePositionBetween(
            mercPos, TestUtils.getEntityPos(res, "older_player")).magnitude();
        int initDistanceBetweenPlayerAndMerc = Position.calculatePositionBetween(
            mercPos, TestUtils.getPlayerPos(res)).magnitude();

        res = dmc.tick(Direction.UP);
        mercPos = TestUtils.getEntityPos(res, "mercenary");
        int distanceBetweenOlderPlayerAndMerc = Position.calculatePositionBetween(
            mercPos, TestUtils.getEntityPos(res, "older_player")).magnitude();
        int distanceBetweenPlayerAndMerc = Position.calculatePositionBetween(
            mercPos, TestUtils.getPlayerPos(res)).magnitude();

        assert (initDistanceBetweenOlderPlayerAndMerc <= distanceBetweenOlderPlayerAndMerc);
        assert (initDistanceBetweenPlayerAndMerc >= distanceBetweenPlayerAndMerc);
    }

    @Test
    @DisplayName("Only player can travel through time travel portals")
    public void onlyPlayerTravelThroughPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
    }

    @Test
    @DisplayName("Older player can use potions")
    public void olderPlayerCanUsePotions() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        final DungeonResponse response = res;
        res = assertDoesNotThrow(() -> dmc.tick(TestUtils.getFirstItemId(response, "invisibility_potion")));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        res = assertDoesNotThrow(() -> dmc.rewind(5));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
    }

    @Test
    @DisplayName("Older player can use potions")
    public void olderPlayerCanUseInvincibilityPotion() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemiesInvincibility", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        final DungeonResponse response = res;
        res = assertDoesNotThrow(() -> dmc.tick(TestUtils.getFirstItemId(response, "invincibility_potion")));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        res = assertDoesNotThrow(() -> dmc.rewind(5));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
    }

    @Test
    @DisplayName("Older player can use potions")
    public void olderPlayerCanSpawnDuringPotionEffect() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        final DungeonResponse response = res;
        res = assertDoesNotThrow(() -> dmc.tick(TestUtils.getFirstItemId(response, "invisibility_potion")));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        res = assertDoesNotThrow(() -> dmc.rewind(2));
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
    }

    @Test
    @DisplayName("Old player can use bomb")
    public void testOldPlayerUseBomb() throws InvalidActionException  {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_place", "c_bombTest_place");

        // Pick up Bomb
        Position bombPos = TestUtils.getEntityPos(res, "bomb");
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // Remove bomb from inventory
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // Bomb not in inventory
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        // Bomb in the position the character was previously
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(pos, TestUtils.getEntities(res, "bomb").get(0).getPosition());

        //Bomb can not be re-picked up
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());

        res = assertDoesNotThrow(() -> dmc.rewind(4));
        assertEquals(bombPos, TestUtils.getEntityPos(res, "bomb"));
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(pos, TestUtils.getEntities(res, "bomb").get(0).getPosition());
    }

    @Test
    @DisplayName("Old and new player battle properly")
    public void playerBattleOldPlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = assertDoesNotThrow(() -> dmc.rewind(5));
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getEntities(res, "mercenary").size(), 1);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
        assertEquals(TestUtils.getEntities(res, "player").size(), 1);

        // Battle
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        assertEquals(TestUtils.getEntities(res, "mercenary").size(), 0);
        assertEquals(TestUtils.getEntities(res, "player").size(), 1);
        assertEquals(res.getBattles().size(), 2);
        assert (TestUtils.getPlayer(res).isPresent());
    }

    @Test
    @DisplayName("Old player disappears at correct time")
    public void oldPlayerDisappears() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurn", "c_generateTest");
        Position playerPos = TestUtils.getPlayerPos(res);
        Position boulderPos = TestUtils.getEntityPos(res, "boulder");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        res = dmc.rewind(5);
        assertEquals(TestUtils.getEntityPos(res, "older_player"), playerPos);
        assertEquals(TestUtils.getEntityPos(res, "boulder"), boulderPos);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
    }

    @Test
    @DisplayName("Old player doesn't fight any enemies they did on original path")
    public void oldPlayerEnemyTeam() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        assertEquals(TestUtils.getEntities(res, "spider").size(), 1);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntities(res, "spider").size(), 0);
        res = dmc.rewind(5);
        assertEquals(TestUtils.getEntities(res, "spider").size(), 1);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "spider").size(), 1);
    }

    @Test
    @DisplayName("Fight after walking out of portal")
    public void portalFight() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnEnemies", "c_generateTest");
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 1);
        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntities(res, "older_player").size(), 0);
        assertEquals(res.getBattles().size(), 1);
    }
}
