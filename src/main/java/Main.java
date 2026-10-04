import config.MazeLoader;
import config.NpcLoader;
import engine.GameEngine;
import model.Maze;
import ui.ConsoleUI;
import ui.LineConsoleUI;
import java.util.Arrays;

/**
 * Starts the prison escape game in the terminal.
 *
 * @author Trinh Quang Minh Ngo
 * @author Xinran Tian
 * @author Minh
 */
public class Main {

    /**
     * Loads the default maze and NPCs and launches immediate terminal controls.
     *
     * <p>The optional {@code --line} argument selects the original line-based
     * interface for scripted input and consoles without terminal support.</p>
     *
     * @param args optional --line flag for line-based input
     * @author Minh
     */
    public static void main(String[] args) {
        Maze maze = MazeLoader.loadDefault();
        GameEngine engine = new GameEngine(maze, NpcLoader.loadDefault(maze));

        if (Arrays.asList(args).contains("--line")) {
            new LineConsoleUI(engine, System.in, System.out).run();
        } else {
            new ConsoleUI(engine).run();
        }
    }
}
