import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import ui.ConsoleUI;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the application starts with the default game configuration.
 *
 * @author Trinh Quang Minh Ngo
 * @author Xinran Tian
 */
public class MainTest {

    /**
     * Verifies that the game starts from the bundled maze and NPC files and
     * ends cleanly when the player quits.
     */
    @Test
    void applicationStartsDefaultGameAndQuits() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(
                    "quit\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));

            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = captured.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains(ConsoleUI.TITLE));
        assertTrue(output.contains("@"));
        assertTrue(output.contains("Thanks for playing."));
    }
}
