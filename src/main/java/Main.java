import config.DifficultyLoader;
import engine.GameEngine;
import model.Difficulty;
import ui.GameLauncher;
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
     * Offers bundled difficulty choices before launching immediate terminal controls.
     *
     * <p>The optional {@code --line} argument selects the original line-based
     * interface with the Normal campaign for scripted input and consoles
     * without terminal support.</p>
     *
     * @param args optional --line flag for line-based input
     * @author Minh
     */
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--line")) {
            GameEngine engine = DifficultyLoader.load(Difficulty.NORMAL);
            new LineConsoleUI(engine, System.in, System.out).run();
        } else {
            GameLauncher.run();
        }
    }
}
