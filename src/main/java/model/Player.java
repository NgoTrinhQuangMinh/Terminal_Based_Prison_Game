package model;

/**
 * Represents the player, their current location in the maze and the
 * items they are carrying.
 *
 * @author Pat Kupkee
 * @author Xinran Tian
 */
public class Player {

    private Position position;
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
}