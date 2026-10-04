package command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests parsing of player movement and inventory commands.
 *
 * @author Pat Kupkee
 * @author Xinran Tian
 */
class CommandTest {

    /**
     * Verifies that left and its WASD alias are recognised.
     */
    @Test
    void parsesLeftCommand() {
        assertEquals(Command.LEFT, Command.parse("left"));
        assertEquals(Command.LEFT, Command.parse("a"));
    }

    /**
     * Verifies that right and its WASD alias are recognised.
     */
    @Test
    void parsesRightCommand() {
        assertEquals(Command.RIGHT, Command.parse("right"));
        assertEquals(Command.RIGHT, Command.parse("d"));
    }

    /**
     * Verifies that forward and its WASD alias are recognised.
     */
    @Test
    void parsesForwardCommand() {
        assertEquals(Command.FORWARD, Command.parse("forward"));
        assertEquals(Command.FORWARD, Command.parse("w"));
    }

    /**
     * Verifies that backward, backwards and the WASD alias are recognised.
     */
    @Test
    void parsesBackwardCommand() {
        assertEquals(Command.BACKWARD, Command.parse("backward"));
        assertEquals(Command.BACKWARD, Command.parse("backwards"));
        assertEquals(Command.BACKWARD, Command.parse("s"));
    }

    /**
     * Verifies that commands are case-insensitive.
     */
    @Test
    void parsesCommandsRegardlessOfCase() {
        assertEquals(Command.LEFT, Command.parse("LEFT"));
        assertEquals(Command.RIGHT, Command.parse("RiGhT"));
        assertEquals(Command.FORWARD, Command.parse("FoRwArD"));
        assertEquals(Command.BACKWARD, Command.parse("BACKWARDS"));
    }

    /**
     * Verifies that surrounding whitespace is ignored.
     */
    @Test
    void ignoresSurroundingWhitespace() {
        assertEquals(Command.LEFT, Command.parse("  left  "));
        assertEquals(Command.RIGHT, Command.parse(" right "));
        assertEquals(Command.FORWARD, Command.parse("\tforward\t"));
        assertEquals(Command.BACKWARD, Command.parse("  backwards  "));
    }

    /**
     * Verifies that additional input after a movement command does not
     * prevent the command from being recognised.
     */
    @Test
    void recognisesCommandWithAdditionalInput() {
        assertEquals(Command.LEFT, Command.parse("left something"));
        assertEquals(Command.RIGHT, Command.parse("right something"));
        assertEquals(Command.FORWARD, Command.parse("forward something"));
        assertEquals(Command.BACKWARD, Command.parse("backwards something"));
    }

    /**
     * Verifies that null input returns UNKNOWN.
     */
    @Test
    void nullInputReturnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.parse(null));
    }

    /**
     * Verifies that blank input returns UNKNOWN.
     */
    @Test
    void blankInputReturnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.parse(""));
        assertEquals(Command.UNKNOWN, Command.parse("   "));
        assertEquals(Command.UNKNOWN, Command.parse("\t"));
    }

    /**
     * Verifies that unsupported commands return UNKNOWN.
     */
    @Test
    void unsupportedInputReturnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.parse("jump"));
        assertEquals(Command.UNKNOWN, Command.parse("hello"));
    }

    /**
     * Verifies that inventory and its short alias are recognised.
     */
    @Test
    void parsesInventoryCommand() {
        assertEquals(Command.INVENTORY, Command.parse("inventory"));
        assertEquals(Command.INVENTORY, Command.parse("i"));
        assertEquals(Command.INVENTORY, Command.parse("  INVENTORY "));
    }

    /**
     * Verifies that use and its equip alias are recognised, with or
     * without an item name after the command.
     */
    @Test
    void parsesUseCommand() {
        assertEquals(Command.USE, Command.parse("use"));
        assertEquals(Command.USE, Command.parse("use herb"));
        assertEquals(Command.USE, Command.parse("equip weapon"));
        assertEquals(Command.USE, Command.parse("USE Healing herb"));
    }

    /**
     * Verifies that NPC interaction commands and their aliases are recognised.
     */
    @Test
    void parsesNpcInteractionCommands() {
        assertEquals(Command.FIGHT, Command.parse("fight"));
        assertEquals(Command.FIGHT, Command.parse("f"));
        assertEquals(Command.TALK, Command.parse("talk"));
        assertEquals(Command.TALK, Command.parse("t"));
        assertEquals(Command.ANSWER, Command.parse("answer clock"));
    }

    /**
     * Verifies that help, look and quit commands and their aliases are recognised.
     */
    @Test
    void parsesGameControlCommands() {
        assertEquals(Command.HELP, Command.parse("help"));
        assertEquals(Command.HELP, Command.parse("h"));
        assertEquals(Command.HELP, Command.parse("?"));
        assertEquals(Command.LOOK, Command.parse("look"));
        assertEquals(Command.LOOK, Command.parse("map"));
        assertEquals(Command.QUIT, Command.parse("quit"));
        assertEquals(Command.QUIT, Command.parse("Q"));
    }

    /**
     * Verifies that the argument after the command word is extracted and
     * that spaces inside it are kept.
     */
    @Test
    void extractsArgumentAfterCommand() {
        assertEquals("Healing herb", Command.argument("use Healing herb"));
        assertEquals("a clock", Command.argument("  answer   a clock  "));
        assertEquals("", Command.argument("fight"));
        assertEquals("", Command.argument("   "));
        assertEquals("", Command.argument(null));
    }
}