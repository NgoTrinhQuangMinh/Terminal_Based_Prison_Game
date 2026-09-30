package ui;

import config.MazeLoader;
import engine.GameEngine;
import model.Player;
import model.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Verifies that immediate controls and answer typing stay separate. */
class GameControlsTest {
    /**
     * Verifies immediate movement shortcuts and inventory visibility.
     *
     * <p>Checks that named arrows and letter keys move without Enter and that repeated inventory shortcuts toggle the presentation state.</p>
     */
    @Test
    void immediateMovementAndInventory() {
        GameEngine game = new GameEngine(MazeLoader.loadDefault());
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
     */
    @Test
    void riddleTypingDoesNotMoveOrQuit() {
        GameEngine game = new GameEngine(MazeLoader.loadDefault());
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
        assertTrue(game.player().has(Player.KEY));
    }

    /**
     * Verifies cancellation and item shortcuts across an NPC encounter.
     *
     * <p>Confirms talking on an empty tile does not enter answer mode, cancellation restores combat controls, healing and equipment apply their effects, and quitting ends play.</p>
     */
    @Test
    void cancelFightHealAndEquip() {
        GameEngine game = new GameEngine(MazeLoader.loadDefault());
        GameControls controls = new GameControls(game);
        controls.handle("t");
        assertFalse(controls.answering());
        for (String key : new String[] {"d", "d", "s", "s", "d", "d", "d", "t", "\u001b", "f", "f"}) { controls.handle(key); }
        assertFalse(controls.answering());
        assertEquals(8, game.player().health());
        controls.handle("h");
        assertEquals(10, game.player().health());
        game.player().collect(Player.WEAPON);
        controls.handle("e");
        assertEquals(5, game.player().attack());
        controls.handle("q");
        assertTrue(game.finished());
    }
}
