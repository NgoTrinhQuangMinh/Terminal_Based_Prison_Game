package ui;

import config.MazeLoader;
import config.NpcLoader;
import engine.GameEngine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import model.Inventory;
import model.Position;
import org.jline.terminal.Size;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Exercises immediate input and screen cleanup through deterministic terminals.
 * @author Minh
 */
class ConsoleUITest {
    /** Creates an independent game with the bundled map and NPC definitions.
     * @author Minh
     * @return fresh game for each input script
     */
    private GameEngine newGame() {
        var maze = MazeLoader.loadDefault();
        return new GameEngine(maze, NpcLoader.loadDefault(maze));
    }

    /** Runs a finite key script and verifies restoration of terminal input flags.
     * @author Minh
     * @param game session receiving key input
     * @param input characters and terminal escape sequences to consume
     * @param width terminal columns
     * @param height terminal rows
     * @return all terminal output including screen-control sequences
     * @throws Exception if terminal setup or execution fails
     */
    private String play(GameEngine game, String input, int width, int height) throws Exception {
        var output = new ByteArrayOutputStream();
        try (var terminal = TestTerminal.create(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), output)) {
            terminal.setSize(new Size(width, height));
            var original = terminal.getAttributes();
            new ConsoleUI(game).run(terminal);
            assertEquals(original.getLocalFlags(), terminal.getAttributes().getLocalFlags());
            assertEquals(original.getInputFlags(), terminal.getAttributes().getInputFlags());
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    /** Verifies real arrow sequences, answer submission, inventory and clean quit.
     * @author Minh
     * @throws Exception if virtual terminal execution fails
     */
    @Test
    void solvesRiddleThroughTerminalKeys() throws Exception {
        GameEngine game = newGame();
        String output = play(game, "\u001b[C\u001b[C\u001b[B\u001b[Bdddtclock\riq", 100, 35);
        assertTrue(game.player().inventory().has(Inventory.KEY));
        assertTrue(game.finished());
        assertTrue(output.contains(ConsoleUI.TITLE));
        assertTrue(output.contains("Exit key"));
        assertTrue(output.contains("\u001b[?1049l"), "Alternate screen must be left");
        assertTrue(output.contains("\u001b[?25h"), "Cursor must be restored");
    }

    /** Verifies the full combat escape route and the final feedback screen.
     * @author Minh
     * @throws Exception if virtual terminal execution fails
     */
    @Test
    void combatRouteWins() throws Exception {
        GameEngine game = newGame();
        String output = play(game, "ddssdddffdddww ", 100, 35);
        assertTrue(game.won());
        assertTrue(output.contains("You escaped!"));
        assertTrue(output.contains("Game ended."));
    }

    /** Verifies EOF returns without manufacturing a win or quit command.
     * @author Minh
     * @throws Exception if virtual terminal execution fails
     */
    @Test
    void eofRestoresTerminal() throws Exception {
        GameEngine game = newGame();
        play(game, "d", 100, 35);
        assertEquals(new Position(2, 1), game.player().position());
        assertFalse(game.finished());
    }

    /** Verifies an undersized screen blocks movement but keeps quit available.
     * @author Minh
     * @throws Exception if virtual terminal execution fails
     */
    @Test
    void smallTerminalShowsResizePromptAndAllowsQuit() throws Exception {
        GameEngine game = newGame();
        String output = play(game, "dq", 65, 20);
        assertEquals(new Position(1, 1), game.player().position());
        assertTrue(game.finished());
        assertTrue(output.contains("Resize terminal"));
    }

    /** Verifies raw attributes and screen state are restored if rendering fails.
     * @author Minh
     * @throws Exception if virtual terminal setup fails
     */
    @Test
    void renderingFailureRestoresTerminal() throws Exception {
        GameEngine game = new GameEngine(MazeLoader.loadDefault()) {
            /** Simulates an unexpected rendering failure inside the terminal loop.
             * @author Minh
             * @return never returns because this fixture always throws
             */
            @Override
            public String render() { throw new IllegalStateException("render failed"); }
        };
        var output = new ByteArrayOutputStream();
        try (var terminal = TestTerminal.create(new ByteArrayInputStream(new byte[0]), output)) {
            terminal.setSize(new Size(100, 35));
            var original = terminal.getAttributes();
            assertThrows(IllegalStateException.class, () -> new ConsoleUI(game).run(terminal));
            assertEquals(original.getLocalFlags(), terminal.getAttributes().getLocalFlags());
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("\u001b[?1049l"));
        }
    }
}
