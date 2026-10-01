package model;

/**
 * Represents the player, their current location in the maze, their
 * health and attack, and the items they are carrying.
 *
 * @author Pat Kupkee
 * @author Xinran Tian
 */
public class Player {

    /** The highest health the player can have. */
    public static final int MAX_HEALTH = 10;

    /** The player's attack without a weapon. */
    public static final int BASE_ATTACK = 3;

    /** The extra attack given by an equipped weapon. */
    public static final int WEAPON_BONUS = 2;

    /** The most health a single herb can restore. */
    public static final int HERB_HEALING = 4;

    private Position position;
    private int health = MAX_HEALTH;
    private boolean weaponEquipped;
    private final Inventory inventory = new Inventory();

    /**
     * Creates a player at the supplied starting position.
     *
     * @param position the player's initial position
     */
    public Player(Position position) {
        this.position = position;
    }

    /**
     * Returns the player's current position.
     *
     * @return the player's current position
     */
    public Position position() {
        return position;
    }

    /**
     * Updates the player's current position.
     *
     * <p>This method does not perform collision, boundary or exit
     * validation. The appropriate game logic is responsible for
     * validating a destination before updating the player's position.</p>
     *
     * @param position the new position for the player
     */
    public void moveTo(Position position) {
        this.position = position;
    }

    /**
     * Returns the inventory holding the items the player is carrying.
     *
     * @return the player's inventory
     */
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Returns the player's remaining health.
     *
     * @return the current health, between 0 and {@link #MAX_HEALTH}
     */
    public int health() {
        return health;
    }

    /**
     * Returns the player's attack, including any equipped weapon bonus.
     *
     * @return the current attack value
     */
    public int attack() {
        return BASE_ATTACK + (weaponEquipped ? WEAPON_BONUS : 0);
    }

    /**
     * Checks whether the player has equipped a weapon.
     *
     * @return true if a weapon is equipped, false otherwise
     */
    public boolean weaponEquipped() {
        return weaponEquipped;
    }

    /**
     * Reduces the player's health by the given amount.
     *
     * <p>Health never drops below zero. Negative amounts are ignored so
     * damage can never heal the player.</p>
     *
     * @param amount the damage taken
     */
    public void damage(int amount) {
        health = Math.max(0, health - Math.max(0, amount));
    }

    /**
     * Uses an item from the inventory and describes the result.
     *
     * <p>A herb restores up to {@link #HERB_HEALING} health and is only
     * consumed if healing happens. A weapon is equipped once and its
     * bonus does not stack. Item names are matched ignoring case and
     * surrounding spaces. Invalid requests leave the player unchanged.</p>
     *
     * @param item the item name typed by the player, such as "herb" or
     *             "weapon"; may be null or blank
     * @return feedback describing what happened
     */
    public String use(String item) {
        if (item == null || item.isBlank()) {
            return "Use what? Try: use herb, use weapon.";
        }
        String name = item.trim().toLowerCase();
        if (name.equals("herb") || name.equals(Inventory.HERB.toLowerCase())) {
            return useHerb();
        }
        if (name.equals("weapon") || name.equals("sword")) {
            return equipWeapon();
        }
        return "You can't use \"" + item.trim() + "\". Try: use herb, use weapon.";
    }

    /**
     * Restores health with a herb if one is held and healing is needed.
     *
     * @return feedback describing what happened
     */
    private String useHerb() {
        if (!inventory.has(Inventory.HERB)) {
            return "You have no herb.";
        }
        if (health == MAX_HEALTH) {
            return "Your health is already full. Herb kept.";
        }
        int healed = Math.min(HERB_HEALING, MAX_HEALTH - health);
        health += healed;
        inventory.remove(Inventory.HERB);
        return "You use a herb and restore " + healed + " health.";
    }

    /**
     * Equips the weapon if one is held and it is not already equipped.
     *
     * @return feedback describing what happened
     */
    private String equipWeapon() {
        if (!inventory.has(Inventory.WEAPON)) {
            return "You have no weapon.";
        }
        if (weaponEquipped) {
            return "Your sword is already equipped.";
        }
        weaponEquipped = true;
        return "Sword equipped. Attack increased by " + WEAPON_BONUS + ".";
    }
}
