package command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests parsing of player movement commands.
 *
 * @author Pat Kupkee
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
        assertEquals(Command.UNKNOWN, Command.parse("fight"));
        assertEquals(Command.UNKNOWN, Command.parse("talk"));
        assertEquals(Command.UNKNOWN, Command.parse("inventory"));
        assertEquals(Command.UNKNOWN, Command.parse("jump"));
        assertEquals(Command.UNKNOWN, Command.parse("hello"));
    }
}