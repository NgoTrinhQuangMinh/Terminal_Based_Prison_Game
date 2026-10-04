package ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import engine.GameEngine;

/**
 * Runs the game in the terminal by reading commands line by line.
 *
 * <p>This class only handles input and output. Every command is passed to
 * {@link GameEngine#execute(String)}, so all game rules stay in the engine.</p>
 *
 * @author Xinran Tian
 */
public class LineConsoleUI {

    /** The title shown when the game starts. */
    public static final String TITLE = "=== Prison Escape ===";

    /** The text shown before each line of input. */
    public static final String PROMPT = "> ";

    private final GameEngine engine;
    private final Scanner input;
    private final PrintStream output;

    /**
     * Creates a terminal UI for a game session.
     *
     * @param engine the game session to play
     * @param input where the player's commands are read from
     * @param output where the game text is printed
     * @throws IllegalArgumentException if any argument is null
     */
    public LineConsoleUI(GameEngine engine, InputStream input, PrintStream output) {
        if (engine == null || input == null || output == null) {
            throw new IllegalArgumentException("Engine, input and output must not be null");
        }
        this.engine = engine;
        this.input = new Scanner(input);
        this.output = output;
    }

    /**
     * Plays the game until it finishes or there is no more input.
     *
     * <p>Shows the introduction and the starting map, then for each line
     * typed by the player shows the command's feedback and the updated map.
     * A closing message is shown when the loop ends.</p>
     */
    public void run() {
        output.println(TITLE);
        output.println(GameEngine.OBJECTIVE_TEXT);
        output.println(GameEngine.HELP_TEXT);
        output.println();
        output.println(engine.render());

        while (!engine.finished()) {
            output.print(PROMPT);
            output.flush();
            if (!input.hasNextLine()) {
                break;
            }
            output.println(engine.execute(input.nextLine()));
            output.println();
            output.println(engine.render());
        }

        output.println();
        output.println(closingMessage());
    }

    /**
     * Chooses the message shown when the game loop ends.
     *
     * @return a message for winning, losing, or leaving the game
     */
    private String closingMessage() {
        if (engine.won()) {
            return "Congratulations, you escaped the prison!";
        }
        if (engine.player().health() == 0) {
            return "You were defeated. Better luck next time.";
        }
        return "Thanks for playing.";
    }
}
