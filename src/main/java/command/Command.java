package command;

import java.util.Locale;

/**
 * Supported player commands for movement, NPC interaction, inventory
 * actions and game control.
 *
 * @author Pat Kupkee
 * @author Xinran Tian
 */
public enum Command {
    LEFT, RIGHT, FORWARD, BACKWARD, FIGHT, TALK, ANSWER, INVENTORY, USE,
    HELP, LOOK, QUIT, UNKNOWN;

    /**
     * Converts the first input token into a supported command.
     *
     * <p>Ignores surrounding whitespace and letter case. Recognises the
     * movement commands and their WASD aliases, {@code fight}/{@code f},
     * {@code talk}/{@code t}, {@code answer}, {@code inventory}/{@code i},
     * {@code use}/{@code equip}, {@code help}/{@code h}/{@code ?},
     * {@code look}/{@code map} and {@code quit}/{@code q}. Additional
     * arguments after the command, such as the item name in
     * {@code use herb}, are ignored for parsing purposes. Null, blank and
     * unsupported input map to {@code UNKNOWN}.</p>
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
            case "fight", "f" -> FIGHT;
            case "talk", "t" -> TALK;
            case "answer" -> ANSWER;
            case "inventory", "i" -> INVENTORY;
            case "use", "equip" -> USE;
            case "help", "h", "?" -> HELP;
            case "look", "map" -> LOOK;
            case "quit", "q" -> QUIT;
            default -> UNKNOWN;
        };
    }

    /**
     * Returns the text that follows the command word.
     *
     * <p>For example, {@code "use Healing herb"} gives {@code "Healing herb"}
     * and {@code "answer  a clock "} gives {@code "a clock"}. Spaces inside
     * the argument are kept, so multi-word answers and item names work.</p>
     *
     * @param input raw player command; may be null
     * @return the trimmed argument, or an empty string if there is none
     */
    public static String argument(String input) {
        if (input == null) {
            return "";
        }
        String[] parts = input.trim().split("\\s+", 2);
        return parts.length < 2 ? "" : parts[1].trim();
    }
}