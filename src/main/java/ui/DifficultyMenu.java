package ui;

import java.io.IOException;
import model.Difficulty;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

/** Immediate-key difficulty selection before a game session is constructed.
 * @author Minh
 */
public final class DifficultyMenu {
    /** Prevents utility construction.
     * @author Minh
     */
    private DifficultyMenu() { }

    /**
     * Displays bundled choices and reads a selection without requiring Enter after a number.
     *
     * <p>Enter selects Normal. Unsupported keys leave the menu active. Q, Escape, Ctrl+C,
     * Ctrl+D and end-of-input cancel without constructing a game. Original terminal
     * attributes are restored on success, cancellation or input failure.</p>
     * @author Minh
     * @param terminal open terminal shared with the subsequent game UI
     * @return selected difficulty, or null when cancelled
     * @throws IOException if terminal input cannot be read
     */
    public static Difficulty select(Terminal terminal) throws IOException {
        var original = terminal.enterRawMode();
        try {
            terminal.puts(Capability.clear_screen);
            terminal.writer().println("MAZE ESCAPE - CHOOSE DIFFICULTY");
            terminal.writer().println("Choose a ready-to-play map.");
            for (Difficulty difficulty : Difficulty.values()) {
                terminal.writer().println((difficulty.ordinal() + 1) + ") " + difficulty.label()
                        + " - " + difficulty.description());
            }
            terminal.writer().println("Press 1, 2 or 3. Enter: Normal. Q: Quit.");
            terminal.flush();
            while (true) {
                int key = terminal.reader().read();
                if (key == -1 || key == 3 || key == 4 || key == 27 || key == 'q' || key == 'Q') {
                    return null;
                }
                if (key == '\r' || key == '\n') { return Difficulty.NORMAL; }
                if (key >= '1' && key <= '3') { return Difficulty.values()[key - '1']; }
                terminal.writer().println("Choose 1 (Easy), 2 (Normal), 3 (Hard), or Q to quit.");
                terminal.flush();
            }
        } finally {
            terminal.setAttributes(original);
        }
    }
}
