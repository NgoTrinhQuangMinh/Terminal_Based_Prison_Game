package ui;

import config.MazeLoader;
import engine.GameEngine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.jline.terminal.Size;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Exercises the JLine loop through a virtual terminal. */
class ConsoleUITest {
    /**
     * Verifies the full key loop through a virtual terminal.
     *
     * <p>Feeds real arrow escape sequences, riddle text and quit input. Asserts key collection, session completion, emitted title text and restoration of terminal local flags.</p>
     *
     * @throws Exception if virtual terminal setup, execution or cleanup fails
     */
    @Test
    void keyLoopSolvesRiddleAndRestoresTerminal() throws Exception {
        // Use a deterministic route: arrow right, right, down, down, right x3.
        byte[] input = "\u001b[C\u001b[C\u001b[B\u001b[Bdddtclock\rq".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameEngine game = new GameEngine(MazeLoader.loadDefault());
        try (var terminal = TestTerminal.create(new ByteArrayInputStream(input), output)) {
            terminal.setSize(new Size(80, 24));
            var original = terminal.getAttributes();
            new ConsoleUI(game).run(terminal);
            assertEquals(original.getLocalFlags(), terminal.getAttributes().getLocalFlags());
            assertTrue(game.player().has(GameEngine.KEY));
            assertTrue(game.finished());
        }
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("MAZE ESCAPE"));
    }
}
