package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the items the player is carrying.
 *
 * <p>Items are identified by name. Duplicate items are stored as separate
 * entries, so collecting two herbs results in two herb entries. Item
 * effects such as healing or equipping are handled elsewhere.</p>
 *
 * @author Xinran Tian
 */
public class Inventory {

    /** Name of the key required to unlock the prison exit. */
    public static final String KEY = "Exit key";

    /** Name of the item used to restore health. */
    public static final String HERB = "Healing herb";

    /** Name of the weapon that increases the player's attack. */
    public static final String WEAPON = "Sword";

    private final List<String> items = new ArrayList<>();

    /**
     * Adds an item to the inventory.
     *
     * @param item the name of the item to add
     * @throws IllegalArgumentException if the item name is null or blank
     */
    public void add(String item) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        items.add(item);
    }

    /**
     * Checks whether the inventory holds at least one of the given item.
     *
     * @param item the name of the item to look for
     * @return true if the item is held, false otherwise
     */
    public boolean has(String item) {
        return items.contains(item);
    }

    /**
     * Counts how many of the given item are held.
     *
     * @param item the name of the item to count
     * @return the number of matching items, or 0 if none are held
     */
    public int count(String item) {
        int count = 0;
        for (String held : items) {
            if (held.equals(item)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks whether the inventory holds no items.
     *
     * @return true if the inventory is empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns the held items in the order they were added.
     *
     * <p>The returned list is an unmodifiable copy, so callers cannot
     * change the inventory through it.</p>
     *
     * @return an unmodifiable snapshot of the held items
     */
    public List<String> items() {
        return List.copyOf(items);
    }
}
