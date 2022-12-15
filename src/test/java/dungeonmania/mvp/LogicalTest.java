package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogicalTest {
    @Test
    @DisplayName("Tests a circuit with multiple sources stays on")
    public void multiSourceCircuit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_multiSourceCircuit", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate second switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Turn off first switch
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // lightbulb still open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Can walk on wire
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), (TestUtils.getEntities(res, "player").get(0).getPosition()));
    }

    @Test
    @DisplayName("Tests a circuit with multiple sources turns off")
    public void multiSourceCircuitOff() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_multiSourceCircuit", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate second switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Turn off first switch
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // lightbulb still open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Turn off second switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Lightbulb off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("And Lightbulb Works + opens and closes")
    public void andBulbWorks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_andBulbWork", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        res = dmc.tick(Direction.RIGHT);
        // only one switch is activated now
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("And lightbulb fails")
    public void andBulbFails() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_andBulbFail", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        // lightbulb is still not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @DisplayName("or work lightbulb")
    public void orBulbWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_orBulbWork", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("or work lightbulb2")
    public void orBulbWork2() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_orBulbWork2", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb still not open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Xor work lightbulb")
    public void xorBulbWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_xorBulbWork", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Xor fail lightbulb")
    public void xorBulbfail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_xorBulbFail1", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb still not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @DisplayName("co_and work lightbulb")
    public void coAndBulbWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_coAndBulbWork", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("co_and work lightbulb 2")
    public void coAndBulbWorkSeveral() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_coAndBulbWorkSeveral", "c_logicalTest");
        // light not open
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb open
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("co_and work lightbulb fail")
    public void coAndBulbFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_coAndBulbFail", "c_logicalTest");
        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // lightbulb closed still
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Key does not open door")
    public void doorKeyFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_doorKeyFail", "c_logicalTest");

        assertEquals(1, TestUtils.getEntities(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());

        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // try to walk through door and fail
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    //Door tests
    @Test
    @DisplayName("And Door Works")
    public void andDoorWorks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_andDoorWork", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        //door open

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("And Door Opens and closes")
    public void andDoorCloses() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_andDoorCloses", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        //door open

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // move boulder, 2nd swtich is off now
        res = dmc.tick(Direction.DOWN);

        pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // cannot go through dooor
        res = dmc.tick(Direction.UP);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }


    @Test
    @DisplayName("And doorfails")
    public void andDoorFails() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_andDoorFail", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        //lightbulb still closed

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        //door open

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // cannot go through dooor
        res = dmc.tick(Direction.DOWN);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());

    }

    @Test
    @DisplayName("or door work")
    public void orDoorWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_orDoorWork", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("or door work 2")
    public void orDoorWork2() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_orDoorWork2", "c_logicalTest");
        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Xor door work")
    public void xorDoorWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_xorDoorWork", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Xor door fail")
    public void xorDoorfail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_xorDoorFail", "c_logicalTest");
        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // cannot go through dooor
        res = dmc.tick(Direction.DOWN);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("co_and work door")
    public void coAndDoorWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_coAndDoorWork", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // can go through dooor
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("co_and work door fail")
    public void coAndDoorFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalTest_coAndDoorFail", "c_logicalTest");

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);
        // lightbulb not open


        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // door not open, try travel through it
        res = dmc.tick(Direction.DOWN);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // cannot go through dooor
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }
}
