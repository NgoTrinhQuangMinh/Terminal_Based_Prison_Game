package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests the Position representation and coordinate movement.
 *
 * @author Pat Kupkee
 */
class PositionTest {

    @Test
    void positionsWithSameCoordinatesAreEqual() {
        Position first = new Position(2, 3);
        Position second = new Position(2, 3);

        assertEquals(first, second);
    }

    @Test
    void positionsWithDifferentCoordinatesAreNotEqual() {
        Position first = new Position(2, 3);
        Position second = new Position(3, 2);

        assertNotEquals(first, second);
    }

    @Test
    void moveReturnsPositionWithOffsetCoordinates() {
        Position position = new Position(2, 3);

        Position moved = position.move(-1, 1);

        assertEquals(new Position(1, 4), moved);
    }

    @Test
    void moveSupportsZeroOffsets() {
        Position position = new Position(2, 3);

        assertEquals(new Position(2, 3), position.move(0, 0));
    }

    @Test
    void moveDoesNotModifyOriginalPosition() {
        Position position = new Position(2, 3);

        position.move(1, 1);

        assertEquals(new Position(2, 3), position);
    }
}