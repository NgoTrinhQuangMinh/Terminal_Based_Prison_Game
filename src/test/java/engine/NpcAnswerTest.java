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
 * Tests answer validation, peaceful resolution, target isolation and one-time rewards.
 * @author Minh
 */
class NpcAnswerTest {
    /**
     * Creates a configured encounter carrying duplicate herbs and an exit key.
     * @author Minh
     * @return a fresh NPC with answer clock
     */
    private Npc npc() {
        return new Npc(new Position(1, 0), 6, 2, "What tells the time?", "clock",
                List.of(Inventory.HERB, Inventory.KEY, Inventory.HERB));
    }

    /**
     * Creates a session with the player on the first NPC tile.
     * @author Minh
     * @param npcs encounters owned by the session
     * @return a new session for answer tests
     */
    private GameEngine session(Npc... npcs) {
        GameEngine engine = new GameEngine(new Maze(List.of("P12X")), List.of(npcs));
        engine.player().moveTo(new Position(1, 0));
        return engine;
    }

    /**
     * Invokes the private talk or answer action without adding command dispatch.
     * @author Minh
     * @param engine session performing the action
     * @param name action method name
     * @param args zero arguments for talk or one non-null answer string
     * @return action feedback
     * @throws AssertionError if reflection cannot invoke the action
     */
    private String action(GameEngine engine, String name, String... args) {
        try {
            Method method = GameEngine.class.getDeclaredMethod(name,
                    args.length == 0 ? new Class<?>[0] : new Class<?>[]{String.class});
            method.setAccessible(true);
            return (String) method.invoke(engine, (Object[]) args);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke " + name, exception);
        }
    }

    /**
     * Verifies an answer without an active encounter awards nothing.
     * @author Minh
     */
    @Test
    void noNpcReturnsGuidance() {
        GameEngine engine = session();
        assertEquals("There is no NPC here to answer.", action(engine, "answer", "clock"));
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies even a correct answer requires the player to hear the question first.
     * @author Minh
     */
    @Test
    void requiresTalkingBeforeAnswering() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        assertEquals("Talk to the NPC to hear its riddle first.", action(engine, "answer", "clock"));
        assertFalse(npc.riddleOffered());
        assertFalse(npc.resolved());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies blank and wrong attempts permit retries without damage or rewards.
     * @author Minh
     */
    @Test
    void invalidAnswersLeaveEncounterUnresolved() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        action(engine, "talk");
        assertEquals("Type answer <your answer>.", action(engine, "answer", ""));
        assertEquals("Type answer <your answer>.", action(engine, "answer", " \t"));
        assertEquals("NPC: Incorrect. Try again, or choose to fight.", action(engine, "answer", "watch"));
        assertFalse(npc.resolved());
        assertTrue(npc.riddleOffered());
        assertEquals(6, npc.health());
        assertEquals(10, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
        assertTrue(action(engine, "answer", "clock").startsWith("NPC: Correct!"));
    }

    /**
     * Verifies normalized correct answers resolve peacefully and grant rewards exactly once.
     * @author Minh
     */
    @Test
    void correctAnswerAwardsOnceWithoutDamageOrHealing() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        engine.player().damage(2);
        action(engine, "talk");
        assertEquals("NPC: Correct! Drops collected: Healing herb, Exit key, Healing herb.",
                action(engine, "answer", "  ClOcK  "));
        assertTrue(npc.resolved());
        assertEquals(6, npc.health());
        assertEquals(8, engine.player().health());
        assertEquals(npc.drops(), engine.player().inventory().items());
        assertEquals("There is no NPC here to answer.", action(engine, "answer", "clock"));
        assertEquals("There is no NPC here to talk to.", action(engine, "talk"));
        assertEquals(npc.drops(), engine.player().inventory().items());
    }

    /**
     * Verifies leaving blocks answers and returning preserves the offered-riddle progress.
     * @author Minh
     */
    @Test
    void answersRequireCurrentNpcButProgressSurvivesLeaving() {
        Npc npc = npc();
        GameEngine engine = session(npc);
        action(engine, "talk");
        engine.player().moveTo(new Position(0, 0));
        assertEquals("There is no NPC here to answer.", action(engine, "answer", "clock"));
        assertFalse(npc.resolved());
        assertTrue(engine.player().inventory().isEmpty());
        engine.player().moveTo(npc.position());
        assertTrue(action(engine, "answer", "clock").startsWith("NPC: Correct!"));
    }

    /**
     * Verifies answers use the current NPC's question and never resolve a different encounter.
     * @author Minh
     */
    @Test
    void answersAreIsolatedBetweenEncounters() {
        Npc first = npc();
        Npc second = new Npc(new Position(2, 0), 8, 3, "Second question?", "egg", List.of(Inventory.WEAPON));
        GameEngine engine = session(first, second);
        action(engine, "talk");
        engine.player().moveTo(second.position());
        assertEquals("Talk to the NPC to hear its riddle first.", action(engine, "answer", "egg"));
        action(engine, "talk");
        assertEquals("NPC: Incorrect. Try again, or choose to fight.", action(engine, "answer", "clock"));
        assertFalse(first.resolved());
        assertFalse(second.resolved());
        assertTrue(action(engine, "answer", "egg").startsWith("NPC: Correct!"));
        assertFalse(first.resolved());
        assertEquals(List.of(Inventory.WEAPON), engine.player().inventory().items());
    }
}
