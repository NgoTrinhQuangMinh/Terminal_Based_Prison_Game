package model;

/**
 * Represents the player and their current location in the maze.
 *
 * @author Pat Kupkee
 */
public class Player {

    private Position position;

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
}