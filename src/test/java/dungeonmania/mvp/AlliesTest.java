package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

public class AlliesTest {

    @Test
    @DisplayName("Allies diplay correct movement based on adjacency and ally is adjacent after player moves")
    public void simpleAlliedMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliesTest_simpleAllyMovement", "c_alliesTest_simpleAllyMovement");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(6, 1), getMercPos(res));
        // Allied, following using Dijkstra
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(new Position(5, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);

        // Adjacent after player moves so it moves to player's previous spot
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
    }

    @Test
    @DisplayName("Ally is adjacent after the ally moves")
    public void simpleAlliedMovement2() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliesTest_simpleAllyMovement2", "c_alliesTest_simpleAllyMovement");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(new Position(6, 1), getMercPos(res));
        // Allied, following using Dijkstra
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(new Position(5, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);

        // Adjacent after player moves so it moves to player's previous spot
        assertEquals(new Position(3, 1), getMercPos(res));
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(4, 1), getMercPos(res));
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(4, 2), getMercPos(res));

    }

    @Test
    @DisplayName("Allies can overlap on the same spot when following")
    public void multipleAllies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliesTest_multipleAllies", "c_alliesTest_multipleAllies");
        List<EntityResponse> mercs = TestUtils.getEntitiesStream(res, "mercenary").collect(Collectors.toList());
        String mercId1 = mercs.get(1).getId();
        String mercId2 = mercs.get(0).getId();


        assertEquals(new Position(7, 1), get2ndMercPos(res));
        assertEquals(new Position(1, 4), getMercPos(res));

        // Allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId1));
        assertEquals(new Position(6, 1), get2ndMercPos(res));
        assertEquals(new Position(1, 3), getMercPos(res));

        res = assertDoesNotThrow(() -> dmc.interact(mercId2));
        assertEquals(new Position(5, 1), get2ndMercPos(res));
        assertEquals(new Position(1, 2), getMercPos(res));

        // 1st Merc now adjacent
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), get2ndMercPos(res));
        assertEquals(new Position(1, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        // 2nd Merc now adjacent, they now overlap
        assertEquals(new Position(2, 1), get2ndMercPos(res));
        assertEquals(new Position(2, 1), getMercPos(res));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 1), get2ndMercPos(res));
        assertEquals(new Position(3, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 2), get2ndMercPos(res));
        assertEquals(new Position(3, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 2), get2ndMercPos(res));
        assertEquals(new Position(4, 2), getMercPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position get2ndMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(1).getPosition();
    }
}
