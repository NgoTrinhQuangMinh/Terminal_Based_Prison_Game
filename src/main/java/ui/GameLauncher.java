package ui;

import config.CampaignLoader;
import engine.GameEngine;
import java.io.IOException;
import model.Difficulty;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/** Opens one terminal for difficulty selection and continuous gameplay.
 * @author Minh
 */
public final class GameLauncher {
    /** Prevents utility construction.
 * @author Minh
 */
    private GameLauncher() { }

    /**
     * Opens the native terminal, presents difficulty choices and starts the selected map set.
     *
     * <p>Closes the terminal on exit. Reports configuration and terminal failures instead
     * of silently substituting a different difficulty.</p>
     * @author Minh
     */
    public static void run() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).dumb(false).build()) {
            run(terminal);
        } catch (IOException | IllegalStateException | IllegalArgumentException exception) {
            System.err.println("Could not start the game: " + exception.getMessage());
            System.err.println("Run play.bat in Windows Terminal or PowerShell.");
        }
    }

    /**
     * Selects a difficulty and plays using the same terminal reader so buffered keys are retained.
     * @author Minh
     * @param terminal terminal owned by the caller
     * @return the played session, or null if selection was cancelled
     * @throws IOException if reading the menu fails
     * @throws IllegalStateException if bundled configuration cannot be loaded
     * @throws IllegalArgumentException if bundled configuration is invalid
     */
    static GameEngine run(Terminal terminal) throws IOException {
        Difficulty difficulty = DifficultyMenu.select(terminal);
        if (difficulty == null) {
            terminal.writer().println("Goodbye."); terminal.flush();
            return null;
        }
        GameEngine game = new GameEngine(CampaignLoader.load(difficulty));
        new ConsoleUI(game).run(terminal);
        return game;
    }
}
