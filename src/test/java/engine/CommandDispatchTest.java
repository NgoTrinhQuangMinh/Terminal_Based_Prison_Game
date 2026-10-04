package engine;

import org.junit.jupiter.api.Test;

import java.util.List;

import model.Inventory;
import model.Maze;
import model.Npc;
import model.Player;
import model.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that player input is dispatched to the right game action through
 * {@link GameEngine#execute(String)}.
 *
 * @author Xinran Tian
 */
class CommandDispatchTest {

    private static final Position START = new Position(1, 1);
    private static final Position NPC_TILE = new Position(2, 1);

    /**
     * Creates a corridor: start, NPC, two floor tiles, then the exit.
     *
     * @return a small test maze
     */
    private static Maze corridor() {
        return new Maze(List.of(
                "-------",
                "|P1..X|",
                "-------"
        ));
    }

    /**
     * Creates a weak NPC next to the start that drops the exit key.
     *
     * @return an NPC defeated by one player attack
     */
    private static Npc weakNpc() {
        return new Npc(NPC_TILE, 3, 2, "What has hands but cannot clap?",
                "a clock", List.of(Inventory.KEY));
    }

    private static GameEngine newGame() {
        return new GameEngine(corridor(), List.of(weakNpc()));
    }

    /**
     * Verifies that movement commands and aliases move the player.
     */
    @Test
    void movementCommandsMoveThePlayer() {
        GameEngine engine = newGame();

        engine.execute("right");
        assertEquals(NPC_TILE, engine.player().position());

        engine.execute("a");
        assertEquals(START, engine.player().position());
    }

    /**
     * Verifies that moving into a wall leaves the player in place.
     */
    @Test
    void blockedMovementLeavesPlayerInPlace() {
        GameEngine engine = newGame();

        String feedback = engine.execute("forward");

        assertEquals(START, engine.player().position());
        assertEquals("Movement blocked.", feedback);
    }

    /**
     * Verifies that fight attacks the NPC on the current tile.
     */
    @Test
    void fightCommandAttacksCurrentNpc() {
        GameEngine engine = newGame();
        engine.execute("d");

        String feedback = engine.execute("f");

        assertTrue(feedback.startsWith("You defeat the NPC."));
        assertTrue(engine.player().inventory().has(Inventory.KEY));
    }

    /**
     * Verifies that fight without an NPC gives guidance.
     */
    @Test
    void fightWithoutNpcGivesGuidance() {
        GameEngine engine = newGame();

        assertEquals("There is no NPC here to fight.", engine.execute("fight"));
    }

    /**
     * Verifies that talk shows the riddle and a multi-word answer resolves it.
     */
    @Test
    void talkAndMultiWordAnswerResolveRiddle() {
        GameEngine engine = newGame();
        engine.execute("right");

        String riddle = engine.execute("talk");
        String feedback = engine.execute("answer A CLOCK");

        assertTrue(riddle.contains("What has hands but cannot clap?"));
        assertTrue(feedback.startsWith("NPC: Correct!"));
        assertTrue(engine.player().inventory().has(Inventory.KEY));
        assertEquals(Player.MAX_HEALTH, engine.player().health());
    }

    /**
     * Verifies that answer without any text asks for an answer instead of failing.
     */
    @Test
    void answerWithoutTextAsksForAnswer() {
        GameEngine engine = newGame();
        engine.execute("right");
        engine.execute("t");

        assertEquals("Type answer <your answer>.", engine.execute("answer"));
    }

    /**
     * Verifies that use passes a multi-word item name to the player.
     */
    @Test
    void useCommandPassesMultiWordItemName() {
        GameEngine engine = newGame();
        engine.player().inventory().add(Inventory.HERB);
        engine.player().damage(5);

        engine.execute("use Healing herb");

        assertEquals(9, engine.player().health());
        assertFalse(engine.player().inventory().has(Inventory.HERB));
    }

    /**
     * Verifies that equip is accepted as an alias for use.
     */
    @Test
    void equipCommandEquipsWeapon() {
        GameEngine engine = newGame();
        engine.player().inventory().add(Inventory.WEAPON);

        engine.execute("equip weapon");

        assertTrue(engine.player().weaponEquipped());
    }

    /**
     * Verifies that the inventory command lists held items.
     */
    @Test
    void inventoryCommandListsItems() {
        GameEngine engine = newGame();

        assertEquals("Inventory: empty", engine.execute("i"));

        engine.player().inventory().add(Inventory.KEY);
        assertEquals("Inventory: Exit key", engine.execute("inventory"));
    }

    /**
     * Verifies that help and look explain the controls and the objective.
     */
    @Test
    void helpAndLookExplainTheGame() {
        GameEngine engine = newGame();

        assertEquals(GameEngine.HELP_TEXT, engine.execute("help"));
        assertEquals(GameEngine.OBJECTIVE_TEXT, engine.execute("look"));
    }

    /**
     * Verifies that unknown, blank and null input change nothing.
     */
    @Test
    void unknownBlankAndNullInputChangeNothing() {
        GameEngine engine = newGame();

        for (String input : new String[] {"jump", "   ", null}) {
            assertTrue(engine.execute(input).startsWith("Unknown command."));
        }
        assertEquals(START, engine.player().position());
        assertEquals(Player.MAX_HEALTH, engine.player().health());
        assertFalse(engine.finished());
    }

    /**
     * Verifies that quit ends the game and later commands do nothing.
     */
    @Test
    void quitEndsGameAndBlocksLaterCommands() {
        GameEngine engine = newGame();

        engine.execute("q");

        assertTrue(engine.finished());
        assertFalse(engine.won());
        assertEquals(GameEngine.GAME_OVER_TEXT, engine.execute("right"));
        assertEquals(START, engine.player().position());
    }

    /**
     * Verifies a full playthrough: defeat the NPC for the key, then escape.
     */
    @Test
    void fullPlaythroughWinsTheGame() {
        GameEngine engine = newGame();

        engine.execute("d");
        engine.execute("fight");
        engine.execute("d");
        engine.execute("d");
        String feedback = engine.execute("d");

        assertEquals("You escaped!", feedback);
        assertTrue(engine.won());
        assertTrue(engine.finished());
        assertEquals(GameEngine.GAME_OVER_TEXT, engine.execute("a"));
    }

    /**
     * Verifies that the exit stays locked when the player has no key.
     */
    @Test
    void exitStaysLockedWithoutKey() {
        GameEngine engine = new GameEngine(corridor());

        engine.execute("d");
        engine.execute("d");
        engine.execute("d");
        String feedback = engine.execute("d");

        assertEquals("The exit is locked. You need the key.", feedback);
        assertFalse(engine.won());
    }

    /**
     * Verifies that losing all health ends the game and blocks later commands.
     */
    @Test
    void playerDeathEndsGame() {
        Npc strongNpc = new Npc(NPC_TILE, 50, Player.MAX_HEALTH, "Riddle?",
                "answer", List.of(Inventory.HERB));
        GameEngine engine = new GameEngine(corridor(), List.of(strongNpc));
        engine.execute("d");

        engine.execute("f");

        assertTrue(engine.finished());
        assertFalse(engine.won());
        assertEquals(GameEngine.GAME_OVER_TEXT, engine.execute("i"));
    }
}
