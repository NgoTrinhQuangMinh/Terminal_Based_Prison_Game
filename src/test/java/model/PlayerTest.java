package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the player's current location state.
 *
 * @author Pat Kupkee
 */
class PlayerTest {

    /**
     * Verifies that a player stores the position supplied at creation.
     */
    @Test
    void playerStoresInitialPosition() {
        Position start = new Position(1, 2);

        Player player = new Player(start);

        assertEquals(start, player.position());
    }

    /**
     * Verifies that the player's current position can be retrieved.
     */
    @Test
    void playerPositionCanBeRetrieved() {
        Position start = new Position(2, 3);

        Player player = new Player(start);

        assertEquals(new Position(2, 3), player.position());
    }

    /**
     * Verifies that the player's position can be updated.
     */
    @Test
    void playerPositionCanBeUpdated() {
        Position start = new Position(1, 2);
        Position destination = new Position(3, 4);

        Player player = new Player(start);

        player.moveTo(destination);

        assertEquals(destination, player.position());
    }
}