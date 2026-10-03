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
 * Tests offering riddles to the active encounter without changing combat or inventory state.
 * @author Minh
 */
class NpcTalkTest {
    /**
     * Creates a fresh encounter with a known question and reward.
     * @author Minh
     * @return an NPC on the first encounter tile
     */
    private Npc npc() {
        return new Npc(new Position(1, 0), 6, 2, "What tells the time?", "clock", List.of(Inventory.KEY));
    }

    /**
     * Creates a session with its player on the first encounter tile.
     * @author Minh
     * @param npcs encounters retained by this session
     * @return a session ready for dialogue
     */
    private GameEngine session(Npc... npcs) {
        GameEngine engine = new GameEngine(new Maze(List.of("P12X")), List.of(npcs));
        engine.player().moveTo(new Position(1, 0));
        return engine;
    }

    /**
     * Calls the private dialogue action without introducing command dispatch.
     * @author Minh
     * @param engine session performing the action
     * @return dialogue feedback
     * @throws AssertionError if reflection cannot invoke the action
     */
    private String talk(GameEngine engine) {
        try {
            Method method = GameEngine.class.getDeclaredMethod("talk");
            method.setAccessible(true);
            return (String) method.invoke(engine);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke dialogue", exception);
        }
    }

    /**
     * Verifies an absent encounter produces guidance without awarding items.
     * @author Minh
     */
    @Test
    void noNpcReturnsGuidance() {
        GameEngine engine = session();
        assertEquals("There is no NPC here to talk to.", talk(engine));
        assertEquals(10, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies repeated talking offers the same question without damage, resolution or rewards.
     * @author Minh
     */
    @Test
    void offersQuestionWithoutOtherStateChanges() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        String expected = "NPC: What tells the time?\nType answer <your answer>.";
        assertEquals(expected, talk(engine));
        assertEquals(expected, talk(engine));
        assertTrue(npc.riddleOffered());
        assertFalse(npc.resolved());
        assertEquals(6, npc.health());
        assertEquals(10, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies distant and resolved encounters cannot offer a riddle.
     * @author Minh
     */
    @Test
    void ignoresDistantAndResolvedNpcs() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        engine.player().moveTo(new Position(0, 0));
        assertEquals("There is no NPC here to talk to.", talk(engine));
        engine.player().moveTo(npc.position());
        npc.resolve();
        assertEquals("There is no NPC here to talk to.", talk(engine));
        assertFalse(npc.riddleOffered());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies talking selects only the encounter on the player's current tile.
     * @author Minh
     */
    @Test
    void offersOnlyCurrentNpcsQuestion() {
        Npc first = npc();
        Npc second = new Npc(new Position(2, 0), 8, 3, "Second question?", "egg", List.of(Inventory.HERB));
        GameEngine engine = session(first, second);
        engine.player().moveTo(second.position());
        assertEquals("NPC: Second question?\nType answer <your answer>.", talk(engine));
        assertTrue(second.riddleOffered());
        assertFalse(first.riddleOffered());
    }
}
