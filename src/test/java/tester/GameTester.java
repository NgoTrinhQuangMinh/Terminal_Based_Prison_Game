package tester;

import java.util.ArrayList;
import java.util.List;

import config.MazeLoader;
import config.NpcLoader;
import engine.GameEngine;
import model.Inventory;
import model.Maze;
import model.Player;
import model.Position;

/**
 * Runs repeatable gameplay scenarios through the same command interface used
 * by a human player.
 *
 * <p>Each scenario creates a fresh game from the bundled configuration, sends
 * commands through {@link GameEngine#execute(String)}, checks the resulting
 * feedback and public game state, and records a clear pass or fail result.</p>
 *
 * @author Lia Huang
 */
public final class GameTester {

    private static final Position START = new Position(1, 1);
    private static final Position NPC_ONE = new Position(6, 3);
    private static final Position NPC_TWO = new Position(7, 5);

    private static final List<String> START_TO_NPC_ONE = List.of(
            "right", "right", "backward", "backward",
            "right", "right", "right"
    );

    private static final List<String> START_TO_NPC_TWO = List.of(
            "right", "right", "backward", "backward",
            "right", "right", "right", "right",
            "backward", "backward"
    );

    private static final List<String> NPC_ONE_TO_EXIT = List.of(
            "right", "right", "right", "forward", "forward"
    );

    private static final List<String> START_TO_EXIT = List.of(
            "right", "right", "backward", "backward",
            "right", "right", "right", "right",
            "right", "right", "forward", "forward"
    );

    /**
     * Prevents construction of this utility class.
     */
    private GameTester() {
    }

    /**
     * Runs every automatic gameplay scenario and prints the results.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        List<ScenarioResult> results = runAll();
        printResults(results);
    }

    /**
     * Runs all configured gameplay scenarios with fresh game state.
     *
     * @return immutable scenario results in execution order
     */
    public static List<ScenarioResult> runAll() {
        return List.of(
                blockedMovementScenario(),
                validMovementScenario(),
                invalidCommandScenario(),
                lockedExitScenario(),
                riddleEscapeScenario(),
                combatEscapeScenario(),
                itemUseScenario()
        );
    }

    /**
     * Checks that a wall blocks movement and leaves the player in place.
     *
     * @return scenario result
     */
    private static ScenarioResult blockedMovementScenario() {
        GameEngine engine = newGame();
        String feedback = engine.execute("forward");

        boolean passed = START.equals(engine.player().position())
                && "Movement blocked.".equals(feedback)
                && !engine.finished();

        return result(
                "Blocked movement",
                passed,
                "Wall movement leaves the player at the start."
        );
    }

    /**
     * Checks that a valid movement command updates the player position.
     *
     * @return scenario result
     */
    private static ScenarioResult validMovementScenario() {
        GameEngine engine = newGame();
        String feedback = engine.execute("right");

        boolean passed =
                new Position(2, 1).equals(engine.player().position())
                        && "Movement successful.".equals(feedback);

        return result(
                "Valid movement",
                passed,
                "A valid command moves the player to the next floor tile."
        );
    }

    /**
     * Checks that an unknown command gives guidance without changing state.
     *
     * @return scenario result
     */
    private static ScenarioResult invalidCommandScenario() {
        GameEngine engine = newGame();
        String feedback = engine.execute("jump");

        boolean passed = START.equals(engine.player().position())
                && feedback.startsWith("Unknown command.")
                && engine.player().health() == Player.MAX_HEALTH
                && engine.player().inventory().isEmpty();

        return result(
                "Invalid command",
                passed,
                "Unknown input reports guidance and leaves game state unchanged."
        );
    }

    /**
     * Checks that the exit cannot be entered before the key is collected.
     *
     * @return scenario result
     */
    private static ScenarioResult lockedExitScenario() {
        GameEngine engine = newGame();

        List<String> feedback =
                runCommands(engine, START_TO_EXIT);

        String last =
                feedback.get(feedback.size() - 1);

        boolean passed = !engine.won()
                && !engine.finished()
                && "The exit is locked. You need the key.".equals(last)
                && !engine.player()
                        .inventory()
                        .has(Inventory.KEY);

        return result(
                "Locked exit",
                passed,
                "The exit remains locked until the player has the key."
        );
    }

    /**
     * Checks a complete non-combat route from the start to a successful escape.
     *
     * @return scenario result
     */
    private static ScenarioResult riddleEscapeScenario() {
        GameEngine engine = newGame();

        runCommands(
                engine,
                START_TO_NPC_ONE
        );

        String riddle =
                engine.execute("talk");

        String answer =
                engine.execute("answer clock");

        List<String> exitFeedback =
                runCommands(
                        engine,
                        NPC_ONE_TO_EXIT
                );

        String last =
                exitFeedback.get(
                        exitFeedback.size() - 1
                );

        boolean passed =
                NPC_ONE.equals(
                        engine.npcs()
                                .get(0)
                                .position()
                )
                        && riddle.contains(
                                "What has hands but cannot clap?"
                        )
                        && answer.startsWith(
                                "NPC: Correct!"
                        )
                        && engine.player()
                                .inventory()
                                .has(Inventory.KEY)
                        && engine.player().health()
                                == Player.MAX_HEALTH
                        && engine.won()
                        && engine.finished()
                        && "You escaped!".equals(last);

        return result(
                "Riddle escape",
                passed,
                "The player solves the first riddle, receives the key and escapes."
        );
    }

    /**
     * Checks a complete combat route from the start to a successful escape.
     *
     * @return scenario result
     */
    private static ScenarioResult combatEscapeScenario() {
        GameEngine engine = newGame();

        runCommands(
                engine,
                START_TO_NPC_ONE
        );

        String firstFight =
                engine.execute("fight");

        String secondFight =
                engine.execute("fight");

        List<String> exitFeedback =
                runCommands(
                        engine,
                        NPC_ONE_TO_EXIT
                );

        String last =
                exitFeedback.get(
                        exitFeedback.size() - 1
                );

        boolean passed =
                firstFight.contains("NPC has 3 health")
                        && secondFight.startsWith(
                                "You defeat the NPC."
                        )
                        && engine.player().health() == 8
                        && engine.player()
                                .inventory()
                                .has(Inventory.KEY)
                        && engine.won()
                        && engine.finished()
                        && "You escaped!".equals(last);

        return result(
                "Combat escape",
                passed,
                "The player defeats the first NPC, receives the key and escapes."
        );
    }

    /**
     * Checks rewards and item use through normal player commands.
     *
     * @return scenario result
     */
    private static ScenarioResult itemUseScenario() {
        GameEngine engine = newGame();

        runCommands(
                engine,
                START_TO_NPC_TWO
        );

        String fight =
                engine.execute("fight");

        String riddle =
                engine.execute("talk");

        String answer =
                engine.execute("answer egg");

        String herb =
                engine.execute("use herb");

        String weapon =
                engine.execute("use weapon");

        boolean passed =
                NPC_TWO.equals(
                        engine.npcs()
                                .get(1)
                                .position()
                )
                        && fight.contains(
                                "hits you for 3"
                        )
                        && riddle.contains(
                                "What has to be broken before you can use it?"
                        )
                        && answer.startsWith(
                                "NPC: Correct!"
                        )
                        && herb.startsWith(
                                "You use a herb"
                        )
                        && weapon.startsWith(
                                "Sword equipped."
                        )
                        && engine.player().health()
                                == Player.MAX_HEALTH
                        && engine.player().attack() == 5
                        && engine.player()
                                .weaponEquipped()
                        && !engine.player()
                                .inventory()
                                .has(Inventory.HERB)
                        && engine.player()
                                .inventory()
                                .has(Inventory.WEAPON);

        return result(
                "Rewards and item use",
                passed,
                "NPC rewards can be collected and used through player commands."
        );
    }

    /**
     * Creates a fresh game from the normal bundled configuration.
     *
     * @return a new independent game session
     */
    private static GameEngine newGame() {
        Maze maze =
                MazeLoader.loadDefault();

        return new GameEngine(
                maze,
                NpcLoader.loadDefault(maze)
        );
    }

    /**
     * Sends a sequence of player commands through the public engine interface.
     *
     * @param engine game session receiving the commands
     * @param commands commands to execute in order
     * @return feedback returned for each command
     */
    private static List<String> runCommands(
            GameEngine engine,
            List<String> commands
    ) {
        List<String> feedback =
                new ArrayList<>();

        for (String command : commands) {
            feedback.add(
                    engine.execute(command)
            );
        }

        return List.copyOf(feedback);
    }

    /**
     * Creates a result with a simple pass or fail detail.
     *
     * @param name scenario name
     * @param passed whether the scenario passed
     * @param detail short explanation of what the scenario checks
     * @return scenario result
     */
    private static ScenarioResult result(
            String name,
            boolean passed,
            String detail
    ) {
        return new ScenarioResult(
                name,
                passed,
                detail
        );
    }

    /**
     * Prints each scenario result followed by a total summary.
     *
     * @param results scenario results to print
     */
    private static void printResults(
            List<ScenarioResult> results
    ) {
        long passed =
                results.stream()
                        .filter(
                                ScenarioResult::passed
                        )
                        .count();

        long failed =
                results.size() - passed;

        System.out.println(
                "=== Automatic Game Tester ==="
        );

        for (ScenarioResult result : results) {
            String status =
                    result.passed()
                            ? "PASS"
                            : "FAIL";

            System.out.println(
                    "[" + status + "] "
                            + result.name()
            );

            System.out.println(
                    "  " + result.detail()
            );
        }

        System.out.println();

        System.out.println(
                "Summary: "
                        + passed
                        + " passed, "
                        + failed
                        + " failed."
        );
    }

    /**
     * Stores the outcome of one automatic gameplay scenario.
     *
     * @param name scenario name
     * @param passed whether the scenario passed
     * @param detail short explanation of the scenario
     */
    public record ScenarioResult(
            String name,
            boolean passed,
            String detail
    ) {

        /**
         * Validates the scenario result fields.
         *
         * @throws IllegalArgumentException if the name or detail is blank
         */
        public ScenarioResult {
            if (name == null
                    || name.isBlank()
                    || detail == null
                    || detail.isBlank()) {

                throw new IllegalArgumentException(
                        "Scenario name and detail must not be blank."
                );
            }
        }
    }
}