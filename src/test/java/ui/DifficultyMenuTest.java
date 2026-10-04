package ui;

import engine.GameEngine;
import model.Difficulty;
import org.jline.terminal.Size;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

/** Tests selection, cancellation and buffered input handoff to the continuous UI.
 * @author Minh
 */
class DifficultyMenuTest {
    /**
     * Selects from a virtual terminal and verifies its input attributes are restored.
     * @author Minh
     * @param input keys supplied by the virtual player
     * @return selected difficulty or null on cancellation
     * @throws Exception if virtual terminal setup or reading fails
     */
    private Difficulty select(String input) throws Exception {
        try (var terminal = TestTerminal.create(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), new ByteArrayOutputStream())) {
            var before = terminal.getAttributes();
            Difficulty result = DifficultyMenu.select(terminal);
            assertEquals(before.getLocalFlags(), terminal.getAttributes().getLocalFlags());
            return result;
        }
    }

    /** Verifies each numeric choice, the default and invalid-input retry.
     * @author Minh
     * @throws Exception if virtual terminal setup or reading fails
     */
    @Test void selectsAllDifficultiesAndNormalDefault() throws Exception {
        assertEquals(Difficulty.EASY, select("1"));
        assertEquals(Difficulty.NORMAL, select("2"));
        assertEquals(Difficulty.HARD, select("3"));
        assertEquals(Difficulty.NORMAL, select("\r"));
        assertEquals(Difficulty.NORMAL, select("\n"));
        assertEquals(Difficulty.EASY, select("x91"));
    }

    /** Verifies cancellation shortcuts and end-of-input never select a map.
     * @author Minh
     * @throws Exception if virtual terminal setup or reading fails
     */
    @Test void cancellationDoesNotStartGame() throws Exception {
        for (String input : new String[]{"q", "Q", "\u001b", "\u0003", "\u0004", ""}) {
            assertNull(select(input));
        }
    }

    /** Verifies buffered gameplay keys survive the menu and use the chosen map.
     * @author Minh
     * @throws Exception if virtual terminal setup or execution fails
     */
    @Test void selectionHandsOffToContinuousGame() throws Exception {
        for (int choice = 1; choice <= 3; choice++) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (var terminal = TestTerminal.create(new ByteArrayInputStream((choice + "dq").getBytes(StandardCharsets.UTF_8)), output)) {
                terminal.setSize(new Size(100, 35));
                var before = terminal.getAttributes();
                GameEngine game = GameLauncher.run(terminal);
                assertNotNull(game); assertTrue(game.finished()); assertFalse(game.won());
                assertEquals(2, game.player().position().x());
                assertEquals(config.MazeLoader.load(Difficulty.values()[choice - 1].mazeResource()).width(), game.maze().width());
                assertTrue(output.toString(StandardCharsets.UTF_8).contains("MAZE ESCAPE - " + Difficulty.values()[choice - 1].label()));
                assertEquals(before.getLocalFlags(), terminal.getAttributes().getLocalFlags());
            }
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("CHOOSE DIFFICULTY"));
        }
    }

    /** Verifies cancelling the launcher returns without constructing or rendering a game.
     * @author Minh
     * @throws Exception if virtual terminal setup or execution fails
     */
    @Test void launcherCancellationReturnsToTerminal() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (var terminal = TestTerminal.create(new ByteArrayInputStream("q".getBytes(StandardCharsets.UTF_8)), output)) {
            assertNull(GameLauncher.run(terminal));
        }
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Goodbye."));
        assertFalse(output.toString(StandardCharsets.UTF_8).contains("WASD / Arrows"));
    }
}
