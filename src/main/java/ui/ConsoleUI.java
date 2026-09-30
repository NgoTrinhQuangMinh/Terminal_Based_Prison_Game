package ui;

import engine.GameEngine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp.Capability;

/** Full-screen terminal UI with immediate keys and isolated riddle typing. */
public class ConsoleUI {
    private final GameEngine game;
    private final GameControls controls;

    /**
     * Connects a terminal UI to an existing game session.
     *
     * <p>Retains the supplied engine and creates a GameControls instance for input state. Construction does not open a terminal or start reading keys.</p>
     *
     * @param game game session whose commands, status and display are used by this UI
     */
    public ConsoleUI(GameEngine game) {
        this.game = game;
        controls = new GameControls(game);
    }

    /**
     * Opens a native terminal and runs the immediate-key interface.
     *
     * <p>Creates and closes the terminal with try-with-resources, delegates the key loop, and reports terminal setup or state failures to standard error with launch guidance.</p>
     */
    public void run() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).dumb(false).build()) {
            run(terminal);
        } catch (IOException | IllegalStateException exception) {
            System.err.println("Could not open the game terminal: " + exception.getMessage());
            System.err.println("Run play.bat in Windows Terminal or PowerShell, outside the IDE output console.");
        }
    }

    /**
     * Runs the immediate-key loop using an existing terminal.
     *
     * <p>Enters raw input and the alternate screen, draws frames, and dispatches keys while the terminal is large enough. Quit shortcuts remain available when undersized. A finally block restores terminal attributes, keypad mode, cursor visibility and the normal screen; the supplied terminal is not closed here.</p>
     *
     * @param terminal open terminal used for input, sizing and output
     */
    void run(Terminal terminal) {
        Attributes original = terminal.enterRawMode();
        try {
            terminal.puts(Capability.enter_ca_mode);
            terminal.puts(Capability.keypad_xmit);
            terminal.puts(Capability.cursor_invisible);
            terminal.flush();
            Display display = new Display(terminal, true);
            display.clear();
            BindingReader reader = new BindingReader(terminal.reader());
            KeyMap<String> keys = keys(terminal);
            while (true) {
                draw(terminal, display);
                String binding = reader.readBinding(keys);
                if (binding == null || game.finished()) { break; }
                String key = binding.equals("TEXT") ? reader.getLastBinding() : binding;
                boolean quitting = key.equals("\u0003") || key.equals("\u0004")
                        || (!controls.answering() && key.equalsIgnoreCase("q"));
                if (quitting || (terminal.getWidth() >= 70 && terminal.getHeight() >= 24)) {
                    controls.handle(key);
                }
                if (quitting) { break; }
            }
        } finally {
            terminal.setAttributes(original);
            terminal.puts(Capability.cursor_normal);
            terminal.puts(Capability.keypad_local);
            terminal.puts(Capability.exit_ca_mode);
            terminal.flush();
        }
        terminal.writer().println(controls.message());
        terminal.flush();
    }

    /**
     * Builds the key bindings used by the terminal input loop.
     *
     * <p>Maps ordinary ASCII and Unicode input to text handling and registers named arrow actions with terminal-specific and common escape sequences. Uses a short ambiguity timeout to distinguish escape-key input.</p>
     *
     * @param terminal terminal whose capabilities provide native arrow sequences
     * @return a new key map for text input and arrow actions
     */
    private KeyMap<String> keys(Terminal terminal) {
        KeyMap<String> keys = new KeyMap<>();
        keys.setNomatch("TEXT");
        keys.setUnicode("TEXT");
        keys.setAmbiguousTimeout(150);
        for (char key = 0; key < 128; key++) { keys.bind("TEXT", String.valueOf(key)); }
        bindArrow(keys, terminal, "UP", Capability.key_up, "\u001b[A", "\u001bOA");
        bindArrow(keys, terminal, "DOWN", Capability.key_down, "\u001b[B", "\u001bOB");
        bindArrow(keys, terminal, "RIGHT", Capability.key_right, "\u001b[C", "\u001bOC");
        bindArrow(keys, terminal, "LEFT", Capability.key_left, "\u001b[D", "\u001bOD");
        return keys;
    }

    /**
     * Registers fallback and terminal-native sequences for an arrow action.
     *
     * <p>Mutates the supplied key map. Fallback sequences are always registered; the capability sequence is added only when the terminal supplies one.</p>
     *
     * @param keys key map to update
     * @param terminal terminal providing the native capability sequence
     * @param action logical action name returned for a matching sequence
     * @param capability terminal capability identifying the arrow key
     * @param sequences fallback escape sequences accepted for the same action
     */
    private void bindArrow(KeyMap<String> keys, Terminal terminal, String action,
                           Capability capability, String... sequences) {
        keys.bind(action, sequences);
        String sequence = KeyMap.key(terminal, capability);
        if (sequence != null) { keys.bind(action, sequence); }
    }

    /**
     * Draws a bounded frame for the current terminal size.
     *
     * <p>Shows a resize prompt for terminals smaller than 70 columns by 24 rows. Otherwise displays the map, controls, inventory visibility, feedback and answer input. Clips output to the available rows and columns, updates the display and flushes the terminal without changing gameplay state.</p>
     *
     * @param terminal terminal providing dimensions and output
     * @param display display updater that applies the new frame
     */
    private void draw(Terminal terminal, Display display) {
        int width = Math.max(1, terminal.getWidth());
        int height = Math.max(1, terminal.getHeight());
        display.resize(height, width);
        List<AttributedString> lines = new ArrayList<>();
        if (width < 70 || height < 24) {
            lines.add(new AttributedString("Resize terminal to at least 70 columns x 24 rows."));
            lines.add(new AttributedString("Current size: " + width + " x " + height + ". Press a key to refresh."));
            lines.add(new AttributedString("Q quits; Escape cancels riddle input."));
        } else {
            lines.add(new AttributedString("MAZE ESCAPE", AttributedStyle.BOLD.foreground(AttributedStyle.CYAN)));
            lines.add(new AttributedString("-".repeat(Math.min(width - 1, 78))));
            for (String row : game.render().split("\n")) {
                lines.add(new AttributedString(row));
            }
            lines.add(new AttributedString(""));
            lines.add(new AttributedString("WASD / Arrows: Move   F: Fight   T: Riddle   Q: Quit"));
            lines.add(new AttributedString("H: Heal   E: Equip weapon   I: Inventory   Ctrl+C: Quit"));
            String inventory = controls.inventoryVisible()
                    ? "Inventory: " + (game.player().inventory().isEmpty() ? "empty" : String.join(", ", game.player().inventory()))
                    : "Inventory hidden (I to show)";
            addWrapped(lines, inventory, width - 1, 2);
            lines.add(new AttributedString("-".repeat(Math.min(width - 1, 78))));
            addWrapped(lines, controls.message(), width - 1, 3);
            if (game.finished()) {
                lines.add(new AttributedString("Game ended. Press any key to return to the terminal."));
            } else if (controls.answering()) {
                String answer = controls.answer();
                lines.add(new AttributedString("Answer: " + answer.substring(Math.max(0, answer.length() - width + 10)) + "_"));
                lines.add(new AttributedString("Enter: Submit   Backspace: Edit   Escape: Cancel"));
            } else {
                lines.add(new AttributedString("Press a control key. No Enter needed."));
            }
        }
        List<AttributedString> frame = lines.stream().limit(height - 1L)
                .map(line -> line.columnSubSequence(0, width - 1)).toList();
        display.update(frame, -1);
        terminal.flush();
    }

    /**
     * Appends a limited number of wrapped feedback lines.
     *
     * <p>Replaces embedded newlines with spaces and prefers breaking at word boundaries. When content exceeds the line limit, the final line is shortened with an ellipsis. The destination list is modified.</p>
     *
     * @param lines destination list receiving attributed text lines
     * @param text non-null feedback to wrap
     * @param width maximum line width; callers provide at least three columns for truncation
     * @param count maximum number of lines to append
     */
    private void addWrapped(List<AttributedString> lines, String text, int width, int count) {
        String remaining = text.replace('\n', ' ');
        for (int index = 0; index < count && !remaining.isEmpty(); index++) {
            int end = Math.min(width, remaining.length());
            if (end < remaining.length()) {
                int space = remaining.lastIndexOf(' ', end);
                if (space > 0) { end = space; }
            }
            String line = remaining.substring(0, end);
            remaining = remaining.substring(end).stripLeading();
            if (index == count - 1 && !remaining.isEmpty()) { line = line.substring(0, Math.min(line.length(), width - 3)) + "..."; }
            lines.add(new AttributedString(line));
        }
    }
}
