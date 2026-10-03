package engine;

import java.util.List;

import command.Command;
import model.Maze;
import model.Npc;
import model.Player;
import model.Position;
import model.Inventory;
import ui.PlayerStatusView;

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

    /** Lists the commands the player can type. */
    public static final String HELP_TEXT =
            "Commands: forward/w, backward/s, left/a, right/d, fight/f, talk/t, "
            + "answer <text>, use <item>, inventory/i, look, help, quit/q.";

    /** Explains the goal of the game. */
    public static final String OBJECTIVE_TEXT =
            "Get the exit key from an NPC by fighting it or answering its riddle, "
            + "then reach the exit (X) to escape.";

    /** Feedback given for any command after the game has ended. */
    public static final String GAME_OVER_TEXT = "The game is over.";

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
     * Runs one line of player input and returns the resulting feedback.
     *
     * <p>This is the single entry point shared by the terminal UI and the
     * automatic game tester. The first word selects the command and the rest
     * of the line is passed on as its argument, so multi-word answers and
     * item names work. Unknown input changes nothing. Once the game has
     * finished, no further commands are run.</p>
     *
     * @param input the line typed by the player; may be null
     * @return feedback describing what happened
     */
    public String execute(String input) {
        if (finished()) {
            return GAME_OVER_TEXT;
        }
        Command command = Command.parse(input);
        String argument = Command.argument(input);

        return switch (command) {
            case LEFT, RIGHT, FORWARD, BACKWARD -> move(command);
            case FIGHT -> fight();
            case TALK -> talk();
            case ANSWER -> answer(argument);
            case USE -> player.use(argument);
            case INVENTORY -> PlayerStatusView.inventoryText(player.inventory());
            case HELP -> HELP_TEXT;
            case LOOK -> OBJECTIVE_TEXT;
            case QUIT -> {
                quit();
                yield "You give up on escaping. Goodbye.";
            }
            case UNKNOWN -> "Unknown command. Type help to see the commands.";
        };
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
     * entering it. After a successful movement, an unresolved NPC at the
     * destination is reported.</p>
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

        Npc npc = currentNpc();
        if (npc != null) {
            return "NPC encountered. Health: " + npc.health()
                    + ", Attack: " + npc.attack()
                    + ". Choose fight or talk.";
        }

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
     * Offers the current active NPC's riddle.
     *
     * <p>Marks the riddle as offered and returns its text with answer instructions. Talking does not damage either participant or award items.</p>
     *
     * @author Minh
     * @return the riddle and instructions, or feedback when no active NPC is present
     */
    private String talk() {
        Npc npc = currentNpc();
        if (npc == null) { return "There is no NPC here to talk to."; }
        return "NPC: " + npc.offerRiddle() + "\nType answer <your answer>.";
    }

    /**
     * Checks a proposed answer for the current NPC encounter.
     *
     * <p>Requires a current unresolved NPC and an already offered riddle. Blank and incorrect attempts award nothing. A correct answer resolves that NPC before granting its configured drops, without combat damage.</p>
     *
     * @author Minh
     * @param attempt non-null answer text from command argument parsing
     * @return feedback for an invalid target, missing question, unsuccessful attempt or successful resolution
     */
    private String answer(String attempt) {
        Npc npc = currentNpc();
        if (npc == null) {
            return "There is no NPC here to answer.";
        }
        if (!npc.riddleOffered()) {
            return "Talk to the NPC to hear its riddle first.";
        }
        if (attempt.isBlank()) {
            return "Type answer <your answer>.";
        }
        if (!npc.accepts(attempt)) {
            return "NPC: Incorrect. Try again, or choose to fight.";
        }
        npc.resolve();
        return "NPC: Correct! " + awardDrops(npc);
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