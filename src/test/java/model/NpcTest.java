package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the NPC model's validation, encounter lifecycle, rewards and riddle contract.
 *
 * @author Minh
 */
class NpcTest {
    /**
     * Creates a fresh encounter for tests that do not vary constructor arguments.
     *
     *
     * @author Minh
     * @return an unresolved NPC with six health, two attack and a whitespace-padded answer
     */
    private Npc newNpc() {
        return new Npc(new Position(2, 3), 6, 2, "What tells the time?", " clock ", List.of("Key", "Herb"));
    }

    /**
     * Verifies configured values are available and a new encounter has no dialogue progress.
     *
     * @author Minh
     */
    @Test
    void newEncounterExposesConfiguredState() {
        Npc npc = newNpc();
        assertAll(
                () -> assertEquals(new Position(2, 3), npc.position()),
                () -> assertEquals(6, npc.health()),
                () -> assertEquals(2, npc.attack()),
                () -> assertFalse(npc.resolved()),
                () -> assertFalse(npc.riddleOffered()));
    }

    /**
     * Verifies both zero and negative health are rejected at construction.
     *
     * @author Minh
     */
    @Test
    void rejectsNonPositiveHealth() {
        for (int health : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new Npc(new Position(0, 0), health, 2, "Question", "answer", List.of("Key")));
        }
    }

    /**
     * Verifies both zero and negative attack values are rejected at construction.
     *
     * @author Minh
     */
    @Test
    void rejectsNonPositiveAttack() {
        for (int attack : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new Npc(new Position(0, 0), 6, attack, "Question", "answer", List.of("Key")));
        }
    }

    /**
     * Verifies empty and whitespace-only riddles and answers cannot create an encounter.
     *
     * @author Minh
     */
    @Test
    void rejectsBlankPuzzleText() {
        for (String blank : List.of("", " \t\n")) {
            assertThrows(IllegalArgumentException.class, () ->
                    new Npc(new Position(0, 0), 6, 2, blank, "answer", List.of("Key")));
            assertThrows(IllegalArgumentException.class, () ->
                    new Npc(new Position(0, 0), 6, 2, "Question", blank, List.of("Key")));
        }
    }

    /**
     * Verifies an encounter must have at least one configured reward.
     *
     * @author Minh
     */
    @Test
    void rejectsEmptyRewards() {
        assertThrows(IllegalArgumentException.class, () ->
                new Npc(new Position(0, 0), 6, 2, "Question", "answer", List.of()));
    }

    /**
     * Verifies reward ownership is isolated from callers while preserving order and duplicates.
     *
     * @author Minh
     */
    @Test
    void rewardsAreDefensivelyCopiedAndImmutable() {
        List<String> rewards = new ArrayList<>(List.of("Herb", "Key", "Herb"));
        Npc npc = new Npc(new Position(0, 0), 6, 2, "Question", "answer", rewards);
        rewards.clear();
        assertEquals(List.of("Herb", "Key", "Herb"), npc.drops());
        assertThrows(UnsupportedOperationException.class, () -> npc.drops().add("Weapon"));
        assertFalse(npc.resolved());
    }

    /**
     * Verifies zero and negative damage neither heals the NPC nor resolves the encounter.
     *
     * @author Minh
     */
    @Test
    void nonPositiveDamageHasNoEffect() {
        Npc npc = newNpc();
        npc.hit(0);
        npc.hit(-5);
        npc.hit(Integer.MIN_VALUE);
        assertEquals(6, npc.health());
        assertFalse(npc.resolved());
    }

    /**
     * Verifies successive nonfatal hits reduce health without ending the encounter.
     *
     * @author Minh
     */
    @Test
    void damageAccumulatesAcrossHits() {
        Npc npc = newNpc();
        npc.hit(2);
        npc.hit(1);
        assertEquals(3, npc.health());
        assertFalse(npc.resolved());
        assertEquals(2, npc.attack());
    }

    /**
     * Verifies damage exactly equal to remaining health resolves the encounter.
     *
     * @author Minh
     */
    @Test
    void exactLethalDamageResolvesEncounter() {
        Npc npc = newNpc();
        npc.hit(6);
        assertEquals(0, npc.health());
        assertTrue(npc.resolved());
    }

    /**
     * Verifies excessive and repeated damage leaves defeated NPC health at zero.
     *
     * @author Minh
     */
    @Test
    void excessiveDamageIsClampedToZero() {
        Npc npc = newNpc();
        npc.hit(Integer.MAX_VALUE);
        assertEquals(0, npc.health());
        assertTrue(npc.resolved());
        npc.hit(1);
        assertEquals(0, npc.health());
        assertTrue(npc.resolved());
    }

    /**
     * Verifies explicit resolution is repeatable and preserves health, rewards and dialogue progress.
     *
     * @author Minh
     */
    @Test
    void explicitResolutionPreservesOtherState() {
        Npc npc = newNpc();
        npc.offerRiddle();
        npc.resolve();
        npc.resolve();
        assertTrue(npc.resolved());
        assertEquals(6, npc.health());
        assertEquals(List.of("Key", "Herb"), npc.drops());
        assertTrue(npc.riddleOffered());
    }

    /**
     * Verifies even the correct answer is rejected before dialogue begins.
     *
     * @author Minh
     */
    @Test
    void answerRequiresRiddleToBeOffered() {
        Npc npc = newNpc();
        assertFalse(npc.accepts("clock"));
        assertFalse(npc.riddleOffered());
        assertFalse(npc.resolved());
    }

    /**
     * Verifies repeat dialogue returns the configured question without damaging or resolving the NPC.
     *
     * @author Minh
     */
    @Test
    void offeringRiddleRecordsDialogueProgress() {
        Npc npc = newNpc();
        assertEquals("What tells the time?", npc.offerRiddle());
        assertEquals("What tells the time?", npc.offerRiddle());
        assertTrue(npc.riddleOffered());
        assertEquals(6, npc.health());
        assertFalse(npc.resolved());
    }

    /**
     * Verifies answer matching ignores case and surrounding whitespace without resolving or rewarding.
     *
     * @author Minh
     */
    @Test
    void correctAnswerIsNormalizedWithoutResolvingEncounter() {
        Npc npc = newNpc();
        npc.offerRiddle();
        assertTrue(npc.accepts("clock"));
        assertTrue(npc.accepts(" \tClOcK \n"));
        assertFalse(npc.resolved());
        assertEquals(6, npc.health());
        assertEquals(List.of("Key", "Herb"), npc.drops());
    }

    /**
     * Verifies incorrect and blank answers leave the riddle available for a later correct attempt.
     *
     * @author Minh
     */
    @Test
    void incorrectAnswersAllowRetry() {
        Npc npc = newNpc();
        npc.offerRiddle();
        assertFalse(npc.accepts("watch"));
        assertFalse(npc.accepts(""));
        assertFalse(npc.accepts(" \t"));
        assertTrue(npc.riddleOffered());
        assertFalse(npc.resolved());
        assertEquals(6, npc.health());
        assertTrue(npc.accepts("clock"));
    }
}
