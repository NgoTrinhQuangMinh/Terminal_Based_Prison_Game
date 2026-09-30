package ui;

import engine.GameEngine;
import java.util.Locale;

/** Immediate game controls, with a separate buffer for riddle answers. */
public class GameControls {
    private final GameEngine game;
    private final StringBuilder answer = new StringBuilder();
    private boolean answering;
    private boolean inventoryVisible;
    private String message = "Find the key. Fight an NPC or solve its riddle for drops.";

    /**
     * Creates an input controller for a game session.
     *
     * <p>Retains the engine and begins with normal controls, hidden inventory and an empty answer buffer. Construction does not execute a command.</p>
     *
     * @param game engine that receives translated player commands
     */
    public GameControls(GameEngine game) { this.game = game; }
    /**
     * Returns the latest feedback shown by the controller.
     *
     * <p>The message changes after handled commands and answer cancellation; reading it does not execute any action.</p>
     *
     * @return the current feedback message
     */
    public String message() { return message; }
    /**
     * Returns the answer text currently being edited.
     *
     * <p>Creates a string snapshot of the buffer without submitting or clearing it.</p>
     *
     * @return the current answer text, possibly empty
     */
    public String answer() { return answer.toString(); }
    /**
     * Reports whether the controller is accepting riddle text.
     *
     * <p>In this mode ordinary action letters are entered as answer characters rather than dispatched as gameplay commands.</p>
     *
     * @return true when riddle answer entry is active
     */
    public boolean answering() { return answering; }
    /**
     * Reports whether inventory should be displayed.
     *
     * <p>This is presentation state toggled by the inventory shortcut and does not change the items held.</p>
     *
     * @return true when the inventory display is enabled
     */
    public boolean inventoryVisible() { return inventoryVisible; }

    /**
     * Processes one character or named control key.
     *
     * <p>Ignores input after the game ends. Quit control characters work in either input mode; other keys are routed to answer editing while a riddle is active. In normal mode, translates movement, encounter and item shortcuts, toggles inventory visibility, and enters answer mode after a successful talk action.</p>
     *
     * @param key non-null input character, control character, or named arrow key such as UP
     */
    public void handle(String key) {
        if (game.finished()) { return; }
        if (key.equals("\u0003") || key.equals("\u0004")) {
            message = game.execute("quit");
            return;
        }
        if (answering) {
            typeAnswer(key);
            return;
        }
        String command = switch (key.toLowerCase(Locale.ROOT)) {
            case "w", "up" -> "forward";
            case "s", "down" -> "backward";
            case "a", "left" -> "left";
            case "d", "right" -> "right";
            case "f" -> "fight";
            case "t" -> "talk";
            case "h" -> "use herb";
            case "e" -> "use weapon";
            case "q" -> "quit";
            default -> "";
        };
        if (key.equalsIgnoreCase("i")) { inventoryVisible = !inventoryVisible; }
        if (!command.isEmpty()) {
            message = game.execute(command);
            if (command.equals("talk") && game.canAnswerRiddle()) {
                answering = true;
                answer.setLength(0);
                message = message.replace("\nType answer <your answer>.", "");
            }
        }
    }

    /**
     * Edits, submits or cancels the active riddle answer.
     *
     * <p>Escape clears the buffer and returns to normal controls. Enter submits non-blank text and keeps answer mode active only while a riddle remains available. Backspace removes one character; printable single characters append up to the 80-character limit. Movement and action commands are not dispatched by typing.</p>
     *
     * @param key non-null character or named key received during answer entry
     */
    private void typeAnswer(String key) {
        if (key.equals("\u001b")) {
            answering = false;
            answer.setLength(0);
            message = "Answer cancelled. Press T to retry, or F to fight.";
        } else if (key.equals("\r") || key.equals("\n")) {
            if (answer.toString().isBlank()) { return; }
            message = game.execute("answer " + answer);
            answering = game.canAnswerRiddle();
            answer.setLength(0);
        } else if (key.equals("\u007f") || key.equals("\b")) {
            if (!answer.isEmpty()) { answer.deleteCharAt(answer.length() - 1); }
        } else if (key.length() == 1 && !Character.isISOControl(key.charAt(0)) && answer.length() < 80) {
            answer.append(key);
        }
    }
}
