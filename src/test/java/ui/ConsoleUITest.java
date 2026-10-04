package ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import engine.GameEngine;
import model.Inventory;
import model.Maze;
import model.Npc;
import model.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the terminal input/output loop using scripted input.
 *
 * @author Xinran Tian
 */
class ConsoleUITest {

    private static GameEngine newGame() {
        Maze maze = new Maze(List.of(
                "-------",
                "|P1..X|",
                "-------"
        ));
        Npc npc = new Npc(new Position(2, 1), 3, 2,
                "What has hands but cannot clap?", "clock",
                List.of(Inventory.KEY));
        return new GameEngine(maze, List.of(npc));
    }

    /**
     * Runs the UI with the given lines of input and returns everything printed.
     *
     * @param engine the game session to play
     * @param lines the commands typed by the player
     * @return the text printed by the UI
     */
    private static String play(GameEngine engine, String... lines) {
        String script = lines.length == 0 ? "" : String.join("\n", lines) + "\n";
        ByteArrayOutputStream captured = new ByteArrayOutputStream();

        new ConsoleUI(engine,
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8)).run();

        return captured.toString(StandardCharsets.UTF_8);
    }

    /**
     * Verifies that the introduction and starting map are shown.
     */
    @Test
    void showsIntroductionAndStartingState() {
        GameEngine engine = newGame();

        String output = play(engine);

        assertTrue(output.contains(ConsoleUI.TITLE));
        assertTrue(output.contains(GameEngine.OBJECTIVE_TEXT));
        assertTrue(output.contains(GameEngine.HELP_TEXT));
        assertTrue(output.contains("|@N..X|"));
        assertTrue(output.contains("Health: 10/10"));
    }

    /**
     * Verifies that each command's feedback and the updated map are shown.
     */
    @Test
    void showsFeedbackAndUpdatedStateAfterEachCommand() {
        GameEngine engine = newGame();

        String output = play(engine, "inventory", "right");

        assertTrue(output.contains("Inventory: empty"));
        assertTrue(output.contains("|.@..X|"));
        assertEquals(new Position(2, 1), engine.player().position());
    }

    /**
     * Verifies that the loop ends without error when the input runs out.
     */
    @Test
    void stopsWhenInputEnds() {
        GameEngine engine = newGame();

        String output = play(engine, "right");

        assertFalse(engine.finished());
        assertTrue(output.contains("Thanks for playing."));
    }

    /**
     * Verifies that commands after quitting are not run.
     */
    @Test
    void stopsReadingAfterQuit() {
        GameEngine engine = newGame();

        play(engine, "quit", "right", "right");

        assertTrue(engine.finished());
        assertEquals(new Position(1, 1), engine.player().position());
    }

    /**
     * Verifies that a winning playthrough ends with the winning message.
     */
    @Test
    void winningPlaythroughShowsWinningMessage() {
        GameEngine engine = newGame();

        String output = play(engine, "d", "fight", "d", "d", "d");

        assertTrue(engine.won());
        assertTrue(output.contains("You escaped!"));
        assertTrue(output.contains("Congratulations, you escaped the prison!"));
    }

    /**
     * Verifies that losing all health ends with the defeat message.
     */
    @Test
    void defeatShowsDefeatMessage() {
        Maze maze = new Maze(List.of(
                "-----",
                "|P1X|",
                "-----"
        ));
        Npc strongNpc = new Npc(new Position(2, 1), 50, 10, "Riddle?",
                "answer", List.of(Inventory.HERB));
        GameEngine engine = new GameEngine(maze, List.of(strongNpc));

        String output = play(engine, "d", "fight", "fight");

        assertTrue(engine.finished());
        assertFalse(engine.won());
        assertTrue(output.contains("You were defeated."));
    }

    /**
     * Verifies that unknown input is reported and the game continues.
     */
    @Test
    void unknownInputIsReportedAndGameContinues() {
        GameEngine engine = newGame();

        String output = play(engine, "dance", "right");

        assertTrue(output.contains("Unknown command."));
        assertEquals(new Position(2, 1), engine.player().position());
    }

    /**
     * Verifies that missing arguments are rejected.
     */
    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConsoleUI(null, System.in, System.out));
        assertThrows(IllegalArgumentException.class,
                () -> new ConsoleUI(newGame(), null, System.out));
        assertThrows(IllegalArgumentException.class,
                () -> new ConsoleUI(newGame(), System.in, null));
    }
}
