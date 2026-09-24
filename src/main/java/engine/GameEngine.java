package engine;

import command.Command;
import config.NpcLoader;
import java.util.List;
import model.Npc;
import model.Level;
import model.Maze;
import model.Player;
import model.Position;

/** Minimal combat and riddle rules, independent of terminal input/output. */
public class GameEngine {
    public static final String KEY = Player.KEY;
    public static final String HERB = Player.HERB;
    public static final String HELP = "Move: w/a/s/d or forward/left/backward/right\n"
            + "Forward is up the map; backward is down.\n"
            + "On N: fight (f), or talk (t) then answer <your answer>.\n"
            + "Drops enter your inventory automatically. Use herb to heal; use weapon to equip.\n"
            + "Other commands: inventory (i), look, help, quit (q).";
    private Maze maze;
    private final List<Level> levels;
    private int levelIndex;
    private final Player player;
    private List<Npc> npcs;
    private boolean won;
    private boolean quit;

    /**
     * Initialises a fresh game session for the supplied maze.
     *
     * <p>Creates the player at the P marker and loads fresh NPCs from the bundled configuration. The engine retains the supplied maze and owns the session's mutable player and NPC state.</p>
     *
     * @param maze validated maze containing the player start and numbered NPC markers
     * @throws IllegalArgumentException if a required marker or NPC configuration value is invalid
     * @throws IllegalStateException if the NPC resource cannot be loaded
     */
    public GameEngine(Maze maze) {
        this(List.of(new Level("Maze", maze, "/npcs.properties")));
    }

    /**
     * Starts a forward-only campaign with fresh player and first-level encounter state.
     * @author Minh
     * @param levels non-empty ordered level definitions; copied before play
     * @throws IllegalArgumentException if no levels are supplied or NPC data is invalid
     * @throws IllegalStateException if the first NPC resource cannot be loaded
     */
    public GameEngine(List<Level> levels) {
        if (levels == null || levels.isEmpty()) { throw new IllegalArgumentException("At least one level is required."); }
        this.levels = List.copyOf(levels);
        Level first = this.levels.get(0);
        maze = first.maze();
        npcs = NpcLoader.load(maze, first.npcResource());
        player = new Player(maze.find('P'));
    }

    /**
     * Returns the current level's one-based position in the campaign.
     * @author Minh
     * @return current level number, starting at one
     */
    public int levelNumber() { return levelIndex + 1; }

    /**
     * Returns the number of levels required to finish this campaign.
     * @author Minh
     * @return total level count
     */
    public int levelCount() { return levels.size(); }

    /**
     * Returns the configured display name of the current level.
     * @author Minh
     * @return current level name
     */
    public String levelName() { return levels.get(levelIndex).name(); }

    /**
     * Exposes the current session's player model.
     *
     * <p>Returns the live mutable player rather than a copy; callers can inspect its state and must respect the model's ownership rules.</p>
     *
     * @return the player owned by this game session
     */
    public Player player() { return player; }
    /**
     * Reports whether the player has escaped successfully.
     *
     * <p>Quitting or losing all health does not by itself set the victory flag.</p>
     *
     * @return true once the engine has recorded a successful exit
     */
    public boolean won() { return won; }
    /**
     * Checks whether the session has reached an end condition.
     *
     * <p>A recorded victory, a quit request or zero player health ends play. This query does not modify state.</p>
     *
     * @return true if the session was won, was quit, or the player has zero health
     */
    public boolean finished() { return won || quit || player.health() == 0; }

    /**
     * Checks whether the current encounter has an offered riddle.
     *
     * <p>Only an unresolved NPC on the player's current tile can provide an answer target.</p>
     *
     * @return true if the current active NPC has offered its riddle
     */
    public boolean canAnswerRiddle() {
        Npc npc = currentNpc();
        return npc != null && npc.riddleOffered();
    }

    /**
     * Executes one command against the current game session.
     *
     * <p>Rejects further actions after the game has ended. Separates the first command token from the remaining argument, dispatches movement, encounters and item use, and returns feedback. Help, look and inventory are read-only; quit records the end of the session. No terminal input or output is performed here.</p>
     *
     * @param input raw command text with an optional argument; null is treated as unknown input
     * @return feedback describing the result or why the input was rejected
     */
    public String execute(String input) {
        if (finished()) { return "The game has ended."; }
        String[] parts = input == null ? new String[0] : input.trim().split("\\s+", 2);
        String argument = parts.length == 2 ? parts[1].trim() : "";
        return switch (Command.parse(input)) {
            case LEFT -> move(-1, 0);
            case RIGHT -> move(1, 0);
            case FORWARD -> move(0, -1);
            case BACKWARD -> move(0, 1);
            case FIGHT -> fight();
            case TALK -> talk();
            case ANSWER -> answer(argument);
            case USE -> player.use(argument);
            case INVENTORY -> "Inventory: " + (player.inventory().isEmpty() ? "empty" : String.join(", ", player.inventory()));
            case HELP -> HELP;
            case LOOK -> "Obtain a key in each level and reach its exit (X). Each door consumes one key. Escape the final level to win.";
            case QUIT -> { quit = true; yield "Goodbye."; }
            case UNKNOWN -> "Unknown command. Type help for controls.";
        };
    }

    /**
     * Attempts to move the player by a coordinate offset.
     *
     * <p>Blocks walls, out-of-bounds destinations and the exit when no key is held. A permitted move updates position, enters the next level or wins at the final exit, or reports an active NPC's stats. Arrival alone does not start combat.</p>
     *
     * @param dx horizontal movement offset in columns
     * @param dy vertical movement offset in rows
     * @return movement, blocking, encounter, level transition or victory feedback
     */
    private String move(int dx, int dy) {
        Position next = player.position().move(dx, dy);
        if (maze.isWall(next)) { return "A wall blocks your way."; }
        if (maze.at(next) == 'X' && !player.has(KEY)) { return "The exit is locked. An NPC holds its key."; }
        if (maze.at(next) == 'X') { return enterExit(next); }
        player.moveTo(next);
        Npc npc = currentNpc();
        if (npc != null) {
            return "NPC: Health " + npc.health() + ", Attack " + npc.attack() + ". Choose fight or talk, or move away.";
        }
        return "You move through the maze.";
    }

    /**
     * Unlocks an exit and enters the next level, or wins after the final level.
     *
     * <p>Loads the next encounters before modifying live state. A loading failure leaves the
     * player, key and current level intact so the player may retry. Successful transitions
     * retain health, equipment and other inventory, consume one key and relocate to P.</p>
     * @author Minh
     * @param exit validated exit coordinate; the caller has already checked key ownership
     * @return transition, configuration failure or final victory feedback
     */
    private String enterExit(Position exit) {
        if (levelIndex + 1 == levels.size()) {
            player.consume(KEY);
            player.moveTo(exit);
            won = true;
            return "You unlock the exit and escape the maze. You win!";
        }
        Level nextLevel = levels.get(levelIndex + 1);
        List<Npc> nextNpcs;
        try {
            nextNpcs = NpcLoader.load(nextLevel.maze(), nextLevel.npcResource());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return "Could not enter the next level: " + exception.getMessage();
        }
        player.consume(KEY);
        maze = nextLevel.maze();
        npcs = nextNpcs;
        levelIndex++;
        player.moveTo(maze.find('P'));
        return "You unlock the door. Entered level " + levelNumber() + "/" + levelCount()
                + ": " + levelName() + ". Find its exit key.";
    }

    /**
     * Finds the active encounter on the player's current tile.
     *
     * <p>Searches the existing session collection and ignores resolved NPCs. Returning the same stored object preserves encounter progress when the player leaves and returns.</p>
     *
     * @return the first unresolved NPC at the player position, or null if none exists
     */
    private Npc currentNpc() {
        return npcs.stream().filter(n -> !n.resolved() && n.position().equals(player.position()))
                .findFirst().orElse(null);
    }

    /**
     * Performs one player-first combat exchange.
     *
     * <p>Requires an active NPC at the player location. A defeated NPC grants rewards and does not counterattack; a surviving NPC damages the player. Reports player death or the remaining combat stats without reading terminal input.</p>
     *
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

    /**
     * Offers the current active NPC's riddle.
     *
     * <p>Marks the riddle as offered and returns its text with answer instructions. Talking does not damage either participant or award items.</p>
     *
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
     * @param attempt non-null answer text from command argument parsing
     * @return feedback for an invalid target, missing question, unsuccessful attempt or successful resolution
     */
    private String answer(String attempt) {
        Npc npc = currentNpc();
        if (npc == null) { return "There is no NPC here to answer."; }
        if (!npc.riddleOffered()) { return "Talk to the NPC to hear its riddle first."; }
        if (attempt.isBlank()) { return "Type answer <your answer>."; }
        if (!npc.accepts(attempt)) { return "NPC: Incorrect. Try again, or choose to fight."; }
        npc.resolve();
        return "NPC: Correct! " + awardDrops(npc);
    }

    /**
     * Adds all rewards from an NPC to the player inventory.
     *
     * <p>Preserves configured order and duplicates and does not automatically use or equip items. The caller must ensure this is called only once for a completed encounter; this helper does not enforce that condition itself.</p>
     *
     * @param npc NPC whose configured drops are to be awarded
     * @return feedback listing the collected item names
     */
    private String awardDrops(Npc npc) {
        npc.drops().forEach(player::collect);
        return "Drops collected: " + String.join(", ", npc.drops()) + ".";
    }

    /**
     * Builds a textual map and player-status display.
     *
     * <p>Overlays unresolved NPCs and the player on the stored maze, with the player taking precedence. Original start and numeric NPC markers appear as floor. Appends a legend, health, attack and key status without changing the session.</p>
     *
     * @return a multiline string ready for terminal display
     */
    public String render() {
        StringBuilder output = new StringBuilder("Level " + levelNumber() + "/" + levelCount() + ": " + levelName() + "\n");
        for (int y = 0; y < maze.height(); y++) {
            for (int x = 0; x < maze.width(); x++) {
                Position position = new Position(x, y);
                char symbol = maze.at(position);
                if (symbol == 'P' || Character.isDigit(symbol)) { symbol = '.'; }
                for (Npc npc : npcs) {
                    if (!npc.resolved() && npc.position().equals(position)) { symbol = 'N'; }
                }
                if (player.position().equals(position)) { symbol = '@'; }
                output.append(symbol);
            }
            output.append('\n');
        }
        return output + "@ You  - | Wall  N NPC  X Exit\nHealth: " + player.health()
                + "/" + Player.MAX_HEALTH + " | Attack: " + player.attack() + " | Key: " + (player.has(KEY) ? "yes" : "no");
    }
}
