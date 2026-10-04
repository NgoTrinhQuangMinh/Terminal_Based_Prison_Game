package ui;

import config.MazeLoader;
import engine.GameEngine;
import model.Player;
import model.Inventory;
import config.NpcLoader;
import model.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Verifies that immediate controls and answer typing stay separate. * @author Minh
     */
class GameControlsTest {
    /** Creates an independent session with bundled NPCs for control tests.
     * @return fresh default game
     * @author Minh
     */
    private GameEngine newGame() {
        var maze = MazeLoader.loadDefault();
        return new GameEngine(maze, NpcLoader.loadDefault(maze));
    }

    /** Verifies empty submissions, the answer length limit and global quit keys.
     * @author Minh
     */
    @Test
    void answerLimitsAndQuitKeys() {
        for (String quit : new String[] {"\u0003", "\u0004"}) {
            GameEngine game = newGame();
            GameControls controls = new GameControls(game);
            for (String key : new String[] {"d", "d", "s", "s", "d", "d", "d", "t"}) {
                controls.handle(key);
            }
            controls.handle("\r");
            assertTrue(controls.answering());
            for (int i = 0; i < 100; i++) { controls.handle("a"); }
            assertEquals(80, controls.answer().length());
            controls.handle(quit);
            assertTrue(game.finished());
            assertFalse(game.canAnswerRiddle());
            Position stopped = game.player().position();
            controls.handle("d");
            assertEquals(stopped, game.player().position());
        }
    }

    /**
     * Verifies immediate movement shortcuts and inventory visibility.
     *
     * <p>Checks that named arrows and letter keys move without Enter and that repeated inventory shortcuts toggle the presentation state.</p>
     * @author Minh
     */
    @Test
    void immediateMovementAndInventory() {
        GameEngine game = newGame();
        GameControls controls = new GameControls(game);
        controls.handle("RIGHT");
        assertEquals(new Position(2, 1), game.player().position());
        controls.handle("D");
        assertEquals(new Position(3, 1), game.player().position());
        controls.handle("i");
        assertTrue(controls.inventoryVisible());
        controls.handle("I");
        assertFalse(controls.inventoryVisible());
    }

    /**
     * Verifies that riddle text editing is isolated from action controls.
     *
     * <p>Types movement, fight and quit letters while answering, checks unchanged position and health, then corrects and submits an answer to collect the key.</p>
     * @author Minh
     */
    @Test
    void riddleTypingDoesNotMoveOrQuit() {
        GameEngine game = newGame();
        GameControls controls = new GameControls(game);
        for (String key : new String[] {"d", "d", "s", "s", "d", "d", "d", "t"}) { controls.handle(key); }
        assertTrue(controls.answering());
        Position position = game.player().position();
        for (char key : "wasdfq".toCharArray()) { controls.handle(String.valueOf(key)); }
        controls.handle("UP");
        assertEquals(position, game.player().position());
        assertEquals(10, game.player().health());
        assertFalse(game.finished());
        controls.handle("\r");
        assertTrue(controls.answering());
        for (char key : "clockx".toCharArray()) { controls.handle(String.valueOf(key)); }
        controls.handle("\u007f");
        assertEquals("clock", controls.answer());
        controls.handle("\r");
        assertFalse(controls.answering());
        assertTrue(game.player().inventory().has(Inventory.KEY));
    }

    /**
     * Verifies cancellation and item shortcuts across an NPC encounter.
     *
     * <p>Confirms talking on an empty tile does not enter answer mode, cancellation restores combat controls, healing and equipment apply their effects, and quitting ends play.</p>
     * @author Minh
     */
    @Test
    void cancelFightHealAndEquip() {
        GameEngine game = newGame();
        GameControls controls = new GameControls(game);
        controls.handle("t");
        assertFalse(controls.answering());
        for (String key : new String[] {"d", "d", "s", "s", "d", "d", "d", "t", "\u001b", "f", "f"}) { controls.handle(key); }
        assertFalse(controls.answering());
        assertEquals(8, game.player().health());
        controls.handle("h");
        assertEquals(10, game.player().health());
        game.player().inventory().add(Inventory.WEAPON);
        controls.handle("e");
        assertEquals(5, game.player().attack());
        controls.handle("q");
        assertTrue(game.finished());
    }
}
