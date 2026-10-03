package engine;

import java.util.List;

import command.Command;
import model.Maze;
import model.Npc;
import model.Player;
import model.Position;
import model.Inventory;

/**
 * Holds the state of one game session and reports whether it has ended.
 *
 * <p>A session owns the maze, the player and the NPCs for a single game.
 * Game rules such as movement, combat and item use are added by other
 * features and operate on this shared state.</p>
 *
 * @author Xinran Tian
 * @author Minh
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
     * The maze determines whether the destination is blocked. If the
     * destination is the exit, the player must have the required key before
     * entering it.</p>
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

        if (maze.at(destination) == 'X') {
            if (!player.inventory().has(Inventory.KEY)) {
                return "The exit is locked. You need the key.";
            }

            player.moveTo(destination);
            markWon();
            return "You escaped!";
        }

        player.moveTo(destination);
        return "Movement successful.";
    }

    /**
     * Finds the active encounter on the player's current tile.
     *
     * <p>Searches the existing session collection and ignores resolved NPCs. Returning the same stored object preserves encounter progress when the player leaves and returns.</p>
     *
     *
     * @author Minh
     * @return the first unresolved NPC at the player position, or null if none exists
     */
    private Npc currentNpc() {
        return npcs.stream().filter(n -> !n.resolved() && n.position().equals(player.position()))
                .findFirst().orElse(null);
    }

    /**
     * Adds all rewards from an NPC to the player inventory.
     *
     * <p>Preserves configured order and duplicates and does not automatically use or equip items. The caller must ensure this is called only once for a completed encounter; this helper does not enforce that condition itself.</p>
     *
     * @author Minh
     * @param npc NPC whose configured drops are to be awarded
     * @return feedback listing the collected item names
     */
    private String awardDrops(Npc npc) {
        npc.drops().forEach(player.inventory()::add);
        return "Drops collected: " + String.join(", ", npc.drops()) + ".";
    }

    /**
     * Performs one player-first combat exchange.
     *
     * <p>Requires an active NPC at the player location. A defeated NPC grants rewards and does not counterattack; a surviving NPC damages the player. Reports player death or the remaining combat stats without reading terminal input.</p>
     *
     * @author Minh
     * @return feedback for an unavailable target, combat exchange, NPC defeat or player death
     */
    private String fight() {
        Npc npc = currentNpc();
        if (npc == null) { return "There is no NPC here to fight."; }
        npc.hit(player.attack());
        if (npc.resolved()) { return "You defeat the NPC. " + awardDrops(npc); }
        player.damage(npc.attack());
        if (player.health() == 0) { return "You have fallen. Game over."; }
        return "You deal " + player.attack() + " damage. NPC has " + npc.health()
                + " health and hits you for " + npc.attack() + ".";
    }
}