package config;

import engine.GameEngine;
import model.Difficulty;
import model.Inventory;
import model.Maze;
import model.Position;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Verifies bundled difficulty resources through complete real gameplay routes.
 * @author Minh
 */
class DifficultyLoaderTest {
    /**
     * Finds a walkable route without passing a locked exit on the way to an encounter.
     * @author Minh
     * @param maze level to explore
     * @param start current player position
     * @param goal destination tile
     * @return movement commands leading to the destination
     */
    private List<String> route(Maze maze, Position start, Position goal) {
        String[] commands = {"d", "a", "s", "w"};
        int[][] offsets = {{1,0},{-1,0},{0,1},{0,-1}};
        Map<Position, Position> previous = new HashMap<>();
        Map<Position, String> moves = new HashMap<>();
        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.add(start); previous.put(start, start);
        while (!queue.isEmpty() && !previous.containsKey(goal)) {
            Position current = queue.remove();
            for (int i = 0; i < offsets.length; i++) {
                Position next = current.move(offsets[i][0], offsets[i][1]);
                if (maze.isWall(next) || previous.containsKey(next)) { continue; }
                if (maze.at(next) == 'X' && !next.equals(goal)) { continue; }
                previous.put(next, current); moves.put(next, commands[i]); queue.add(next);
            }
        }
        assertTrue(previous.containsKey(goal), "Unreachable required tile: " + goal);
        List<String> result = new ArrayList<>();
        for (Position tile = goal; !tile.equals(start); tile = previous.get(tile)) {
            result.add(0, moves.get(tile));
        }
        return result;
    }


    /** Completes every difficulty through both supported encounter routes.
     * @author Minh
     */
    @Test
    void everyDifficultySupportsCombatAndRiddleEscape() {
        for (Difficulty difficulty : Difficulty.values()) {
            for (boolean riddle : new boolean[] {true, false}) {
                GameEngine game = DifficultyLoader.load(difficulty);
                var maze = game.maze();
                route(maze, game.player().position(), maze.find('1')).forEach(game::execute);
                if (riddle) {
                    game.execute("talk");
                    String answer = switch (difficulty) {
                        case EASY -> "sun";
                        case NORMAL -> "clock";
                        case HARD -> "light";
                    };
                    assertTrue(game.execute("answer " + answer).contains("Correct!"));
                } else {
                    for (int turn = 0; turn < 10 && !game.player().inventory().has(Inventory.KEY)
                            && !game.finished(); turn++) { game.execute("fight"); }
                    assertFalse(game.finished(), "Combat must be survivable: " + difficulty);
                }
                assertTrue(game.player().inventory().has(Inventory.KEY));
                route(maze, game.player().position(), maze.find('X')).forEach(game::execute);
                assertTrue(game.won(), "Map must be escapable: " + difficulty);
                if (riddle) { assertEquals(10, game.player().health()); }
            }
        }
    }

    /** Checks progressively larger maps, stronger key encounters and independent sessions.
     * @author Minh
     */
    @Test
    void distinctChallengesAndFreshState() {
        var easy = DifficultyLoader.load(Difficulty.EASY);
        var normal = DifficultyLoader.load(Difficulty.NORMAL);
        var hard = DifficultyLoader.load(Difficulty.HARD);
        assertTrue(easy.maze().width() < normal.maze().width());
        assertTrue(normal.maze().width() < hard.maze().width());
        assertTrue(easy.npcs().get(0).health() < normal.npcs().get(0).health());
        assertTrue(normal.npcs().get(0).health() < hard.npcs().get(0).health());
        for (Difficulty difficulty : Difficulty.values()) {
            var first = DifficultyLoader.load(difficulty);
            first.npcs().get(0).resolve();
            first.player().damage(3);
            var second = DifficultyLoader.load(difficulty);
            assertFalse(second.npcs().get(0).resolved());
            assertEquals(10, second.player().health());
            assertNotSame(first.maze(), second.maze());
        }
    }

    /** Ensures bad paths and missing choices fail explicitly without fallback.
     * @author Minh
     */
    @Test
    void invalidResourcesAreReported() {
        assertThrows(IllegalArgumentException.class, () -> DifficultyLoader.load(null));
        assertThrows(IllegalArgumentException.class, () -> MazeLoader.load(null));
        assertThrows(IllegalArgumentException.class, () -> NpcLoader.load(null, "/npcs.properties"));
        assertThrows(IllegalStateException.class, () -> MazeLoader.load("/missing-map.txt"));
        assertThrows(IllegalStateException.class, () -> NpcLoader.load(MazeLoader.loadDefault(), "/missing-npcs.properties"));
    }
}
