package ui;

import org.junit.jupiter.api.Test;

import java.util.List;

import model.Inventory;
import model.Player;
import model.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the inventory and status text shown to the player.
 *
 * @author Xinran Tian
 */
class PlayerStatusViewTest {

    private Player newPlayer() {
        return new Player(new Position(0, 0));
    }

    /**
     * Verifies that an empty inventory is clearly shown as empty.
     */
    @Test
    void emptyInventoryIsShownAsEmpty() {
        assertEquals("Inventory: empty",
                PlayerStatusView.inventoryText(new Inventory()));
    }

    /**
     * Verifies that all held items are listed in the order collected.
     */
    @Test
    void inventoryListsAllItemsInOrder() {
        Inventory inventory = new Inventory();
        inventory.add(Inventory.WEAPON);
        inventory.add(Inventory.KEY);

        assertEquals("Inventory: Sword, Exit key",
                PlayerStatusView.inventoryText(inventory));
    }

    /**
     * Verifies that repeated items are grouped with a count.
     */
    @Test
    void repeatedItemsAreGroupedWithCount() {
        Inventory inventory = new Inventory();
        inventory.add(Inventory.HERB);
        inventory.add(Inventory.KEY);
        inventory.add(Inventory.HERB);

        assertEquals("Inventory: Healing herb x2, Exit key",
                PlayerStatusView.inventoryText(inventory));
    }

    /**
     * Verifies that describing the inventory does not change it.
     */
    @Test
    void viewingInventoryDoesNotChangeIt() {
        Inventory inventory = new Inventory();
        inventory.add(Inventory.HERB);

        PlayerStatusView.inventoryText(inventory);

        assertEquals(List.of(Inventory.HERB), inventory.items());
    }

    /**
     * Verifies the status line for a new player without the key.
     */
    @Test
    void statusLineShowsNoKeyForNewPlayer() {
        assertEquals("Health: 10/10 | Attack: 3 | Key: no",
                PlayerStatusView.statusLine(newPlayer()));
    }

    /**
     * Verifies that the status line updates as soon as the key is received.
     */
    @Test
    void statusLineUpdatesAfterKeyIsReceived() {
        Player player = newPlayer();

        player.inventory().add(Inventory.KEY);

        assertEquals("Health: 10/10 | Attack: 3 | Key: yes",
                PlayerStatusView.statusLine(player));
    }

    /**
     * Verifies that the status line reflects current health and attack.
     */
    @Test
    void statusLineReflectsHealthAndAttack() {
        Player player = newPlayer();
        player.inventory().add(Inventory.WEAPON);
        player.use("weapon");
        player.damage(2);

        assertEquals("Health: 8/10 | Attack: 5 | Key: no",
                PlayerStatusView.statusLine(player));
    }
}
