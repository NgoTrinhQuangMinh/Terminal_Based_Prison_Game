package ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;

/** Provides deterministic stream-backed xterm fixtures without native PTY input pumps.
 * @author Minh
 */
final class TestTerminal {
    /** Prevents fixture utility construction.
     * @author Minh
     */
    private TestTerminal() { }

    /**
     * Creates a terminal whose finite scripted input is read directly by the test consumer.
     *
     * <p>The xterm type retains escape sequences and display capabilities despite the
     * implementation name. Avoiding provider discovery prevents a native PTY pump from
     * reaching EOF and closing the terminal before the test consumes its queued keys.</p>
     * @author Minh
     * @param input scripted input owned by the returned terminal
     * @param output destination for rendered frames
     * @return terminal to close after the test
     * @throws IOException if terminal construction fails
     */
    static Terminal create(InputStream input, OutputStream output) throws IOException {
        return new DumbTerminal("test", "xterm", input, output, StandardCharsets.UTF_8);
    }
}
