package engine;

import model.Inventory;
import model.Maze;
import model.Npc;
import model.Position;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests active encounter selection and persistence independently of combat and rewards.
 *
 * @author Minh
 */
class ActiveNpcSelectionTest {
    /**
     * Creates a session with two marker locations and the supplied encounter objects.
     *
     * @param npcs encounter objects retained by the session
     * @return a new session starting away from both NPC markers
     */
    private GameEngine session(Npc... npcs) {
        return new GameEngine(new Maze(List.of("P12.X")), List.of(npcs));
    }

    /**
     * Creates an unresolved encounter at a specified position on the fixture map.
     *
     * @param x column occupied by the encounter
     * @return a fresh NPC with six health and a key reward
     */
    private Npc npcAt(int x) {
        return new Npc(new Position(x, 0), 6, 2, "What tells the time?", "clock",
                List.of(Inventory.KEY));
    }

    /**
     * Calls the source implementation's private lookup without widening its production visibility.
     *
     * @param engine session whose current encounter is queried
     * @return the selected live NPC, or null when no unresolved encounter occupies the tile
     * @throws AssertionError if the lookup cannot be invoked
     */
    private Npc currentNpc(GameEngine engine) {
        try {
            Method lookup = GameEngine.class.getDeclaredMethod("currentNpc");
            lookup.setAccessible(true);
            return (Npc) lookup.invoke(engine);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not query the active encounter", exception);
        }
    }

    /** Verifies an empty encounter collection has no active target, even on a numbered map tile. */
    @Test
    void emptySessionHasNoActiveNpc() {
        GameEngine engine = session();
        engine.player().moveTo(new Position(1, 0));
        assertNull(currentNpc(engine));
    }

    /** Verifies NPCs on other tiles cannot be selected and selection follows the player's location. */
    @Test
    void selectsOnlyNpcAtCurrentPosition() {
        Npc first = npcAt(1);
        Npc second = npcAt(2);
        GameEngine engine = session(first, second);
        assertNull(currentNpc(engine));
        engine.player().moveTo(second.position());
        assertSame(second, currentNpc(engine));
        engine.player().moveTo(first.position());
        assertSame(first, currentNpc(engine));
        engine.player().moveTo(new Position(3, 0));
        assertNull(currentNpc(engine));
    }

    /** Verifies explicit encounter completion immediately removes the NPC from active selection. */
    @Test
    void resolvedNpcIsExcluded() {
        Npc npc = npcAt(1);
        GameEngine engine = session(npc);
        engine.player().moveTo(npc.position());
        assertSame(npc, currentNpc(engine));
        npc.resolve();
        assertNull(currentNpc(engine));
        assertSame(npc, engine.npcs().get(0));
    }

    /** Verifies a defeated NPC remains excluded after the player leaves and returns. */
    @Test
    void defeatedNpcStaysInactiveOnReturn() {
        Npc npc = npcAt(1);
        GameEngine engine = session(npc);
        engine.player().moveTo(npc.position());
        npc.hit(6);
        assertNull(currentNpc(engine));
        engine.player().moveTo(new Position(0, 0));
        engine.player().moveTo(npc.position());
        assertNull(currentNpc(engine));
        assertEquals(0, npc.health());
        assertTrue(npc.resolved());
    }

    /** Verifies returning to an active encounter preserves its identity, health and offered riddle. */
    @Test
    void leavingAndReturningPreservesEncounterProgress() {
        Npc npc = npcAt(1);
        GameEngine engine = session(npc);
        engine.player().moveTo(npc.position());
        Npc selected = currentNpc(engine);
        selected.hit(2);
        selected.offerRiddle();
        engine.player().moveTo(new Position(0, 0));
        assertNull(currentNpc(engine));
        engine.player().moveTo(npc.position());
        assertSame(selected, currentNpc(engine));
        assertEquals(4, currentNpc(engine).health());
        assertTrue(currentNpc(engine).riddleOffered());
        assertTrue(currentNpc(engine).accepts("clock"));
        assertFalse(currentNpc(engine).resolved());
    }

    /** Verifies progress and completion in one encounter do not alter another encounter's state. */
    @Test
    void encountersMaintainIndependentState() {
        Npc first = npcAt(1);
        Npc second = npcAt(2);
        GameEngine engine = session(first, second);
        engine.player().moveTo(first.position());
        currentNpc(engine).hit(2);
        currentNpc(engine).offerRiddle();
        currentNpc(engine).resolve();
        engine.player().moveTo(second.position());
        assertSame(second, currentNpc(engine));
        assertEquals(6, second.health());
        assertFalse(second.riddleOffered());
        assertFalse(second.resolved());
        assertEquals(4, first.health());
        assertTrue(first.riddleOffered());
        assertTrue(first.resolved());
    }

    /** Verifies repeated lookups are read-only and retain the session's actual NPC object. */
    @Test
    void lookupDoesNotMutateSessionOrEncounter() {
        Npc npc = npcAt(1);
        GameEngine engine = session(npc);
        engine.player().moveTo(npc.position());
        assertSame(npc, currentNpc(engine));
        assertSame(npc, currentNpc(engine));
        assertEquals(6, npc.health());
        assertFalse(npc.riddleOffered());
        assertFalse(npc.resolved());
        assertEquals(List.of(Inventory.KEY), npc.drops());
        assertTrue(engine.player().inventory().isEmpty());
        assertEquals(10, engine.player().health());
        assertEquals(npc.position(), engine.player().position());
        assertFalse(engine.finished());
    }
}
