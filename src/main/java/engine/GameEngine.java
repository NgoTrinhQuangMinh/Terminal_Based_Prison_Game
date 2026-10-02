package engine;

import java.util.List;

import command.Command;
import model.Maze;
import model.Npc;
import model.Player;
import model.Position;

/**
 * Holds the state of one game session and reports whether it has ended.
 *
 * <p>A session owns the maze, the player and the NPCs for a single game.
 * Game rules such as movement, combat and item use are added by other
 * features and operate on this shared state.</p>
 *
 * @author Xinran Tian
 */
public class GameEngine {

    /** The maze marker for the player's starting position. */
    public static final char START = 'P';

    private final Maze maze;
    private final Player player;
    private final List<Npc> npcs;
    private boolean won;
    private boolean quit;

    /**
     * Creates a new game session without any NPCs.
     *
     * @param maze the maze to play in
     * @throws IllegalArgumentException if the maze is null
     */
    public GameEngine(Maze maze) {
        this(maze, List.of());
    }

    /**
     * Creates a new game session with the given NPCs.
     *
     * <p>The player is created at the maze's start marker with full health
     * and an empty inventory. The NPC list is copied, so later changes to
     * the caller's list do not affect the session.</p>
     *
     * @param maze the maze to play in
     * @param npcs the NPCs placed in the maze
     * @throws IllegalArgumentException if the maze or NPC list is null
     */
    public GameEngine(Maze maze, List<Npc> npcs) {
        if (maze == null || npcs == null) {
            throw new IllegalArgumentException("Maze and NPCs must not be null");
        }
        this.maze = maze;
        this.player = new Player(maze.find(START));
        this.npcs = List.copyOf(npcs);
    }

    /**
     * Returns the maze used by this session.
     *
     * @return the session's maze
     */
    public Maze maze() {
        return maze;
    }

    /**
     * Returns the player of this session.
     *
     * @return the session's player
     */
    public Player player() {
        return player;
    }

    /**
     * Returns the NPCs in this session.
     *
     * @return an unmodifiable list of the session's NPCs
     */
    public List<Npc> npcs() {
        return npcs;
    }

    /**
     * Checks whether the player has escaped.
     *
     * @return true if the game has been won, false otherwise
     */
    public boolean won() {
        return won;
    }

    /**
     * Marks the game as won. Used when the player escapes through the exit.
     */
    public void markWon() {
        won = true;
    }

    /**
     * Ends the game because the player chose to quit.
     */
    public void quit() {
        quit = true;
    }

    /**
     * Checks whether play has ended.
     *
     * @return true if the game was won, the player quit, or the player's
     *         health is zero; false otherwise
     */
    public boolean finished() {
        return won || quit || player.health() == 0;
    }

    /**
     * Attempts to move the player in the direction represented by a command.
     *
     * <p>The command is converted into a coordinate offset and the
     * destination is checked by the maze before the player's position
     * is updated. Invalid or unsupported commands do not change the
     * player's position.</p>
     *
     * @param command movement command to execute; may be null
     * @return feedback describing the result of the movement attempt
     */
    public String move(Command command) {
        if (command == null || command == Command.UNKNOWN) {
            return "Unknown movement command.";
        }

        return switch (command) {
            case LEFT -> moveBy(-1, 0);
            case RIGHT -> moveBy(1, 0);
            case FORWARD -> moveBy(0, -1);
            case BACKWARD -> moveBy(0, 1);
            default -> "Unknown movement command.";
        };
    }

    /**
     * Attempts to move the player by the supplied coordinate offset.
     *
     * <p>The destination is calculated from the player's current position.
     * The maze determines whether the destination is blocked. If it is
     * blocked, the player's current position is unchanged.</p>
     *
     * @param dx horizontal movement offset
     * @param dy vertical movement offset
     * @return feedback describing the result of the movement attempt
     */
    private String moveBy(int dx, int dy) {
        Position destination = player.position().move(dx, dy);

        if (maze.isWall(destination)) {
            return "Movement blocked.";
        }

        player.moveTo(destination);
        return "Movement successful.";
    }
}