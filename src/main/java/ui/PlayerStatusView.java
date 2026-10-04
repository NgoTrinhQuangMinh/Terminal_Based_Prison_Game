package ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import model.Inventory;
import model.Player;

/**
 * Builds the text shown to the player for their inventory and status.
 *
 * <p>This class only formats text from the current state. It never changes
 * the player or their inventory.</p>
 *
 * @author Xinran Tian
 */
public final class PlayerStatusView {

    private PlayerStatusView() {
    }

    /**
     * Describes the items in an inventory.
     *
     * <p>Items are listed in the order they were first collected. Repeated
     * items are grouped with a count, for example
     * {@code "Inventory: Healing herb x2, Exit key"}.</p>
     *
     * @param inventory the inventory to describe
     * @return the inventory text, or {@code "Inventory: empty"} if no items are held
     */
    public static String inventoryText(Inventory inventory) {
        List<String> items = inventory.items();
        if (items.isEmpty()) {
            return "Inventory: empty";
        }
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String item : items) {
            counts.merge(item, 1, Integer::sum);
        }
        StringJoiner joiner = new StringJoiner(", ", "Inventory: ", "");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            int count = entry.getValue();
            joiner.add(count > 1 ? entry.getKey() + " x" + count : entry.getKey());
        }
        return joiner.toString();
    }

    /**
     * Describes the player's health, attack and whether they hold the exit key.
     *
     * <p>For example {@code "Health: 8/10 | Attack: 5 | Key: yes"}.</p>
     *
     * @param player the player to describe
     * @return a single status line for the player
     */
    public static String statusLine(Player player) {
        return "Health: " + player.health() + "/" + Player.MAX_HEALTH
                + " | Attack: " + player.attack()
                + " | Key: " + (player.inventory().has(Inventory.KEY) ? "yes" : "no");
    }
}
