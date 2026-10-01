package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /**
     * Verifies that a new player starts with an empty inventory.
     */
    @Test
    void newPlayerHasEmptyInventory() {
        Player player = new Player(new Position(0, 0));

        assertTrue(player.inventory().isEmpty());
    }

    /**
     * Verifies that items added to the player's inventory are kept.
     */
    @Test
    void playerInventoryKeepsAddedItems() {
        Player player = new Player(new Position(0, 0));

        player.inventory().add(Inventory.KEY);

        assertTrue(player.inventory().has(Inventory.KEY));
    }
}