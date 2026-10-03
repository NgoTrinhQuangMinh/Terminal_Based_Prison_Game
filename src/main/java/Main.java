import config.MazeLoader;
import config.NpcLoader;
import engine.GameEngine;
import model.Maze;
import ui.ConsoleUI;

/**
 * Starts the prison escape game in the terminal.
 *
 * @author Trinh Quang Minh Ngo
 * @author Xinran Tian
 */
public class Main {

    /**
     * Loads the default maze and NPCs, then runs the game using standard
     * input and output.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Maze maze = MazeLoader.loadDefault();
        GameEngine engine = new GameEngine(maze, NpcLoader.loadDefault(maze));

        new ConsoleUI(engine, System.in, System.out).run();
    }
}
