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
 * Tests player-first combat, target selection, counterattacks and one-time rewards.
 *
 * @author Minh
 */
class NpcCombatTest {
    /**
     * Creates a configurable encounter on the first NPC tile.
     *
     * @author Minh
     * @param health initial NPC health
     * @param attack counterattack damage
     * @return a fresh encounter carrying two herbs and the exit key
     */
    private Npc npc(int health, int attack) {
        return new Npc(new Position(1, 0), health, attack, "What tells the time?", "clock",
                List.of(Inventory.HERB, Inventory.KEY, Inventory.HERB));
    }

    /**
     * Creates a session and places its player at the first encounter tile.
     *
     * @author Minh
     * @param npcs encounters available to this session
     * @return a fresh session ready for a combat action
     */
    private GameEngine session(Npc... npcs) {
        GameEngine engine = new GameEngine(new Maze(List.of("P12X")), List.of(npcs));
        engine.player().moveTo(new Position(1, 0));
        return engine;
    }

    /**
     * Invokes the source's private action without adding command dispatch or widening visibility.
     *
     * @author Minh
     * @param engine session performing the combat exchange
     * @return combat feedback
     * @throws AssertionError if the combat method cannot be invoked
     */
    private String fight(GameEngine engine) {
        try {
            Method method = GameEngine.class.getDeclaredMethod("fight");
            method.setAccessible(true);
            return (String) method.invoke(engine);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not perform combat", exception);
        }
    }

    /**
     * Verifies combat without an encounter neither harms the player nor awards items.
     *
     * @author Minh
     */
    @Test
    void noTargetLeavesPlayerUnchanged() {
        GameEngine engine = session();
        assertEquals("There is no NPC here to fight.", fight(engine));
        assertEquals(10, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
        assertFalse(engine.finished());
    }

    /**
     * Verifies leaving an encounter prevents remote attacks and returning resumes its health state.
     *
     * @author Minh
     */
    @Test
    void distantNpcCannotBeAttacked() {
        Npc npc = npc(9, 2);
        GameEngine engine = session(npc);
        fight(engine);
        engine.player().moveTo(new Position(0, 0));
        assertEquals("There is no NPC here to fight.", fight(engine));
        assertEquals(6, npc.health());
        assertEquals(8, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
        engine.player().moveTo(npc.position());
        fight(engine);
        assertEquals(3, npc.health());
        assertEquals(6, engine.player().health());
    }

    /**
     * Verifies the standard exchange deals player damage first and reports the survivor's counterattack.
     *
     * @author Minh
     */
    @Test
    void survivingNpcCounterattacksWithConfiguredDamage() {
        Npc npc = npc(6, 2);
        GameEngine engine = session(npc);
        assertEquals("You deal 3 damage. NPC has 3 health and hits you for 2.", fight(engine));
        assertEquals(3, npc.health());
        assertEquals(8, engine.player().health());
        assertFalse(npc.resolved());
        assertFalse(engine.finished());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies lethal player damage prevents counterattack and awards every configured drop only once.
     *
     * @author Minh
     */
    @Test
    void defeatingNpcAwardsDropsOnceWithoutCounterattack() {
        Npc npc = npc(6, 2);
        GameEngine engine = session(npc);
        fight(engine);
        assertEquals("You defeat the NPC. Drops collected: Healing herb, Exit key, Healing herb.",
                fight(engine));
        assertEquals(0, npc.health());
        assertTrue(npc.resolved());
        assertEquals(8, engine.player().health());
        assertEquals(npc.drops(), engine.player().inventory().items());
        assertEquals("There is no NPC here to fight.", fight(engine));
        assertEquals(npc.drops(), engine.player().inventory().items());
        assertEquals(8, engine.player().health());
    }

    /**
     * Verifies an encounter resolved outside combat cannot be attacked or award drops again.
     *
     * @author Minh
     */
    @Test
    void previouslyResolvedNpcCannotBeFought() {
        Npc npc = npc(6, 2);
        GameEngine engine = session(npc);
        npc.resolve();
        assertEquals("There is no NPC here to fight.", fight(engine));
        assertEquals(6, npc.health());
        assertEquals(10, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies a lethal counterattack finishes the session without victory or encounter rewards.
     *
     * @author Minh
     */
    @Test
    void lethalCounterattackEndsGameWithoutRewards() {
        Npc npc = npc(6, 20);
        GameEngine engine = session(npc);
        assertEquals("You have fallen. Game over.", fight(engine));
        assertEquals(0, engine.player().health());
        assertEquals(3, npc.health());
        assertFalse(npc.resolved());
        assertTrue(engine.finished());
        assertFalse(engine.won());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies combat uses the equipped attack value without adding another equipment bonus.
     *
     * @author Minh
     */
    @Test
    void equippedWeaponUsesPlayerAttack() {
        Npc npc = npc(4, 20);
        GameEngine engine = session(npc);
        engine.player().inventory().add(Inventory.WEAPON);
        engine.player().use("weapon");
        engine.player().use("weapon");
        assertEquals(5, engine.player().attack());
        assertTrue(fight(engine).startsWith("You defeat the NPC."));
        assertEquals(0, npc.health());
        assertEquals(10, engine.player().health());
        assertEquals(5, engine.player().attack());
        assertEquals(1, engine.player().inventory().count(Inventory.WEAPON));
        assertEquals(2, engine.player().inventory().count(Inventory.HERB));
    }

    /**
     * Verifies one exchange affects only the encounter on the player's current tile.
     *
     * @author Minh
     */
    @Test
    void combatLeavesOtherEncountersUntouched() {
        Npc target = npc(6, 2);
        Npc other = new Npc(new Position(2, 0), 8, 3, "Question?", "answer",
                List.of(Inventory.WEAPON));
        GameEngine engine = session(other, target);
        fight(engine);
        assertEquals(3, target.health());
        assertEquals(8, other.health());
        assertFalse(other.resolved());
        assertFalse(other.riddleOffered());
        assertEquals(List.of(Inventory.WEAPON), other.drops());
    }
}
