package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;

public class SceptreTest {

    @Test
    @DisplayName("Test Sceptre can be built with correct materials")
    public void sceptreCanBeBuilt() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        // pick up materials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
    }

    @Test
    @DisplayName("Test Sceptre can be built with OTHER correct materials")
    public void sceptreCanBeBuiltWithOtherMaterials() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(2, TestUtils.getEntities(res, "arrow").size());
        // pick up materials
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
    }

    @Test
    @DisplayName("Test Sceptre can be built with two sun stones")
    public void sceptreCanBeBuiltWithTwoSunStones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(2, TestUtils.getEntities(res, "arrow").size());
        // pick up materials
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
    }

    @Test
    @DisplayName("Test Sceptre cannot be built without correct materials")
    public void sceptreCannotBeBuilt() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        // pick up materials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertThrows(InvalidActionException.class, () ->
                dmc.build("sceptre")
        );
    }

    @Test
    @DisplayName("Test Sceptre's can bribe a mercenary")
    public void sceptreCanBribeAllMercenaries() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        // pick up materials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        // At this point, the mercenary is RIGHT next to the player
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
    }


    @Test
    @DisplayName("Test Sceptre's mind control expires")
    public void sceptreBribingEffectExpires() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreTest", "c_generateTest");
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        // pick up materials
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        // At this point, the mercenary is RIGHT next to the player
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, TestUtils.getEntities(res, "mercenary").size());
    }
}
