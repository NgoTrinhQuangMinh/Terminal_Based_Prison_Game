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
 * Tests encounter reward transfer independently of combat and riddle completion.
 *
 * @author Minh
 */
class EncounterRewardsTest {
    /**
     * Creates a configured NPC whose rewards can be awarded by the session.
     *
     * @author Minh
     * @param drops ordered item names, including any repeated rewards
     * @return a fresh NPC carrying the specified drops
     */
    private Npc npcWith(List<String> drops) {
        return new Npc(new Position(1, 0), 6, 2, "What tells the time?", "clock", drops);
    }

    /**
     * Creates an independent session containing the supplied encounter.
     *
     * @author Minh
     * @param npc encounter retained by the session
     * @return a session with a fresh player and empty inventory
     */
    private GameEngine session(Npc npc) {
        return new GameEngine(new Maze(List.of("P1X")), List.of(npc));
    }

    /**
     * Invokes the copied private helper without widening its production interface.
     *
     * @author Minh
     * @param engine session whose player receives rewards
     * @param npc encounter supplying the rewards
     * @return the reward feedback
     * @throws AssertionError if the helper cannot be invoked
     */
    private String award(GameEngine engine, Npc npc) {
        try {
            Method method = GameEngine.class.getDeclaredMethod("awardDrops", Npc.class);
            method.setAccessible(true);
            return (String) method.invoke(engine, npc);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not award encounter rewards", exception);
        }
    }

    /**
     * Verifies all drops append to existing items in configured order, including duplicates.
     *
     * @author Minh
     */
    @Test
    void appendsEveryDropWithoutReplacingExistingInventory() {
        Npc npc = npcWith(List.of(Inventory.HERB, Inventory.KEY, Inventory.HERB, Inventory.WEAPON));
        GameEngine engine = session(npc);
        engine.player().inventory().add(Inventory.HERB);
        npc.resolve();

        award(engine, npc);

        assertEquals(List.of(Inventory.HERB, Inventory.HERB, Inventory.KEY,
                Inventory.HERB, Inventory.WEAPON), engine.player().inventory().items());
        assertEquals(3, engine.player().inventory().count(Inventory.HERB));
    }

    /**
     * Verifies feedback lists every awarded item, preserving duplicate names and their order.
     *
     * @author Minh
     */
    @Test
    void feedbackReportsConfiguredDrops() {
        Npc npc = npcWith(List.of(Inventory.HERB, Inventory.KEY, Inventory.HERB));
        GameEngine engine = session(npc);
        npc.resolve();

        assertEquals("Drops collected: Healing herb, Exit key, Healing herb.", award(engine, npc));
    }

    /**
     * Verifies awarded herbs and weapons remain stored without healing or equipping the player.
     *
     * @author Minh
     */
    @Test
    void collectingDropsDoesNotUseOrEquipItems() {
        Npc npc = npcWith(List.of(Inventory.HERB, Inventory.WEAPON));
        GameEngine engine = session(npc);
        engine.player().damage(4);
        npc.resolve();

        award(engine, npc);

        assertEquals(6, engine.player().health());
        assertEquals(3, engine.player().attack());
        assertFalse(engine.player().weaponEquipped());
        assertEquals(List.of(Inventory.HERB, Inventory.WEAPON), engine.player().inventory().items());
    }

    /**
     * Verifies reward collection preserves the NPC's configured drops and encounter progress.
     *
     * @author Minh
     */
    @Test
    void preservesNpcStateAndSessionStatus() {
        Npc npc = npcWith(List.of(Inventory.KEY));
        GameEngine engine = session(npc);
        npc.hit(2);
        npc.offerRiddle();
        npc.resolve();
        List<String> drops = npc.drops();
        Position playerPosition = engine.player().position();

        award(engine, npc);

        assertSame(drops, npc.drops());
        assertEquals(List.of(Inventory.KEY), npc.drops());
        assertEquals(4, npc.health());
        assertTrue(npc.riddleOffered());
        assertTrue(npc.resolved());
        assertEquals(playerPosition, engine.player().position());
        assertFalse(engine.won());
        assertFalse(engine.finished());
    }

    /**
     * Verifies rewards affect only the receiving session's player inventory.
     *
     * @author Minh
     */
    @Test
    void rewardsRemainInReceivingSession() {
        Npc npc = npcWith(List.of(Inventory.KEY));
        GameEngine receiver = session(npc);
        GameEngine other = session(npcWith(List.of(Inventory.HERB)));
        npc.resolve();

        award(receiver, npc);

        assertTrue(receiver.player().inventory().has(Inventory.KEY));
        assertTrue(other.player().inventory().isEmpty());
    }
}
