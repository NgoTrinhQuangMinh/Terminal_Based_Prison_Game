package command;

import java.util.Locale;

/**
 * Supported movement commands for the player's movement.
 *
 * @author Pat Kupkee
 */
public enum Command {
    LEFT, RIGHT, FORWARD, BACKWARD, UNKNOWN;

    /**
     * Converts the first input token into a supported movement command.
     *
     * <p>Ignores surrounding whitespace and letter case. Recognises the
     * supported movement commands and their WASD aliases. Additional
     * arguments after the command are ignored for parsing purposes.
     * Null, blank and unsupported input map to {@code UNKNOWN}.</p>
     *
     * @param input raw player command, optionally followed by additional input;
     *              may be null
     * @return the recognised movement command, or {@code UNKNOWN}
     */
    public static Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }

        String command = input.trim()
                .toLowerCase(Locale.ROOT)
                .split("\\s+", 2)[0];

        return switch (command) {
            case "left", "a" -> LEFT;
            case "right", "d" -> RIGHT;
            case "forward", "w" -> FORWARD;
            case "backward", "backwards", "s" -> BACKWARD;
            default -> UNKNOWN;
        };
    }
}