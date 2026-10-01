package command;

import java.util.Locale;

/**
 * Supported player commands for movement and inventory actions.
 *
 * @author Pat Kupkee
 * @author Xinran Tian
 */
public enum Command {
    LEFT, RIGHT, FORWARD, BACKWARD, INVENTORY, USE, UNKNOWN;

    /**
     * Converts the first input token into a supported command.
     *
     * <p>Ignores surrounding whitespace and letter case. Recognises the
     * supported movement commands and their WASD aliases, the inventory
     * command and its {@code i} alias, and the use command and its
     * {@code equip} alias. Additional arguments after the command, such
     * as the item name in {@code use herb}, are ignored for parsing
     * purposes. Null, blank and unsupported input map to {@code UNKNOWN}.</p>
     *
     * @param input raw player command, optionally followed by additional input;
     *              may be null
     * @return the recognised command, or {@code UNKNOWN}
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
            case "inventory", "i" -> INVENTORY;
            case "use", "equip" -> USE;
            default -> UNKNOWN;
        };
    }
}