package ui;

import engine.GameEngine;
import model.Level;
import model.Maze;
import model.Position;
import org.junit.jupiter.api.Test;
import org.jline.terminal.Size;
import org.jline.terminal.TerminalBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Verifies linked-map transitions through continuous input and the terminal loop.
 * @author Minh
 */
class LinkedMapsUITest {
    /**
     * Creates two maps of different dimensions and different riddle answers.
     * @author Minh
     * @return a new campaign with independent encounter state
     */
    private GameEngine game() {
        return new GameEngine(List.of(
                new Level("First", new Maze(List.of("P1X", "...", "...")), "/npcs.properties"),
                new Level("Second", new Maze(List.of("P.1.X")), "/levels/courtyard-npcs.properties")));
    }

    /**
     * Sends printable/control characters to the immediate-key controller.
     * @author Minh
     * @param controls controller receiving input
     * @param keys ordered key sequence
     */
    private void type(GameControls controls, String keys) {
        for (char key : keys.toCharArray()) { controls.handle(String.valueOf(key)); }
    }

    /** Verifies intermediate doors keep the controller active and the final door wins.
     * @author Minh
     */
    @Test void immediateControlsContinueAcrossMaps() {
        GameEngine game = game(); GameControls controls = new GameControls(game);
        type(controls, "dtclock\rd");
        assertEquals(2, game.levelNumber()); assertFalse(game.finished());
        assertFalse(controls.answering()); assertEquals("", controls.answer());
        assertTrue(controls.message().contains("Second"));
        type(controls, "ddtpiano\rdd");
        assertTrue(game.won()); assertFalse(controls.answering());
    }

    /** Verifies external level transitions cannot preserve stale riddle input.
     * @author Minh
     */
    @Test void externalTransitionClearsAnswerBuffer() {
        GameEngine game = game(); GameControls controls = new GameControls(game);
        type(controls, "dtcl");
        assertTrue(controls.answering());
        game.execute("answer clock"); game.execute("d");
        controls.handle("d");
        assertFalse(controls.answering()); assertEquals("", controls.answer());
        assertEquals(new Position(1, 0), game.player().position());
    }

    /** Verifies the full-screen terminal redraws a smaller second map and restores terminal state.
     * @author Minh
     * @throws Exception if virtual terminal setup or execution fails
     */
    @Test void terminalLoopCompletesBothMaps() throws Exception {
        GameEngine game = game(); ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] input = "dtclock\rdddtpiano\rddq".getBytes(StandardCharsets.UTF_8);
        try (var terminal = TerminalBuilder.builder().system(false).type("xterm")
                .streams(new ByteArrayInputStream(input), output).build()) {
            terminal.setSize(new Size(80, 24));
            var original = terminal.getAttributes();
            new ConsoleUI(game).run(terminal);
            assertTrue(game.won());
            assertEquals(original.getLocalFlags(), terminal.getAttributes().getLocalFlags());
        }
        String screen = output.toString(StandardCharsets.UTF_8);
        assertTrue(screen.contains("First")); assertTrue(screen.contains("Second"));
    }

    /** Verifies larger maps request more terminal space and still allow quitting.
     * @author Minh
     * @throws Exception if virtual terminal setup or execution fails
     */
    @Test void largerMapRequestsResizeInsteadOfClipping() throws Exception {
        GameEngine game = new GameEngine(List.of(new Level("Wide",
                new Maze(List.of("P" + ".".repeat(90) + "X")), "/npcs.properties")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (var terminal = TerminalBuilder.builder().system(false).type("xterm")
                .streams(new ByteArrayInputStream("dq".getBytes(StandardCharsets.UTF_8)), output).build()) {
            terminal.setSize(new Size(80, 24));
            new ConsoleUI(game).run(terminal);
        }
        assertEquals(new Position(0, 0), game.player().position());
        assertTrue(game.finished()); assertFalse(game.won());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Resize terminal to at least 93 columns"));
    }
}
