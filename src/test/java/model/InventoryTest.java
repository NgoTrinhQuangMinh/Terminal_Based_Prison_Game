package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests storing and querying items in the inventory.
 *
 * @author Xinran Tian
 */
class InventoryTest {

    /**
     * Verifies that a new inventory holds no items.
     */
    @Test
    void newInventoryIsEmpty() {
        Inventory inventory = new Inventory();

        assertTrue(inventory.isEmpty());
        assertEquals(List.of(), inventory.items());
    }

    /**
     * Verifies that an added item is stored and can be found.
     */
    @Test
    void addedItemIsHeld() {
        Inventory inventory = new Inventory();

        inventory.add(Inventory.KEY);

        assertFalse(inventory.isEmpty());
        assertTrue(inventory.has(Inventory.KEY));
        assertEquals(List.of(Inventory.KEY), inventory.items());
    }

    /**
     * Verifies that an item that was never added is not reported as held.
     */
    @Test
    void missingItemIsNotHeld() {
        Inventory inventory = new Inventory();
        inventory.add(Inventory.HERB);

        assertFalse(inventory.has(Inventory.KEY));
        assertEquals(0, inventory.count(Inventory.KEY));
    }

    /**
     * Verifies that duplicate items are stored as separate entries.
     */
    @Test
    void duplicateItemsAreStoredSeparately() {
        Inventory inventory = new Inventory();

        inventory.add(Inventory.HERB);
        inventory.add(Inventory.HERB);

        assertEquals(2, inventory.count(Inventory.HERB));
        assertEquals(List.of(Inventory.HERB, Inventory.HERB), inventory.items());
    }

    /**
     * Verifies that items are listed in the order they were added.
     */
    @Test
    void itemsKeepInsertionOrder() {
        Inventory inventory = new Inventory();

        inventory.add(Inventory.HERB);
        inventory.add(Inventory.WEAPON);
        inventory.add(Inventory.KEY);

        assertEquals(List.of(Inventory.HERB, Inventory.WEAPON, Inventory.KEY),
                inventory.items());
    }

    /**
     * Verifies that the returned list cannot be used to change the inventory.
     */
    @Test
    void returnedItemsCannotModifyInventory() {
        Inventory inventory = new Inventory();
        inventory.add(Inventory.HERB);

        List<String> snapshot = inventory.items();

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.add(Inventory.KEY));
        assertFalse(inventory.has(Inventory.KEY));
    }

    /**
     * Verifies that a snapshot does not change when items are added later.
     */
    @Test
    void snapshotIsNotAffectedByLaterAdds() {
        Inventory inventory = new Inventory();
        List<String> snapshot = inventory.items();

        inventory.add(Inventory.KEY);

        assertTrue(snapshot.isEmpty());
    }

    /**
     * Verifies that null and blank item names are rejected.
     */
    @Test
    void blankItemNamesAreRejected() {
        Inventory inventory = new Inventory();

        assertThrows(IllegalArgumentException.class, () -> inventory.add(null));
        assertThrows(IllegalArgumentException.class, () -> inventory.add("  "));
        assertTrue(inventory.isEmpty());
    }
}
