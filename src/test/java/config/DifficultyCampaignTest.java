package config;

import engine.GameEngine;
import model.Difficulty;
import model.Level;
import model.Maze;
import model.Player;
import model.Position;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/** Validates all shipped map sets through complete real gameplay routes.
 * @author Minh
 */
class DifficultyCampaignTest {
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

    /**
     * Completes a campaign using either known riddle answers or combat with earned equipment.
     * @author Minh
     * @param difficulty bundled map set
     * @param riddles true for peaceful completion; false for combat
     */
    private void complete(Difficulty difficulty, boolean riddles) {
        List<Level> levels = CampaignLoader.load(difficulty);
        GameEngine game = new GameEngine(levels);
        List<String> answers = switch (difficulty) {
            case EASY -> List.of("sun", "book");
            case NORMAL -> List.of("clock", "piano");
            case HARD -> List.of("towel", "silence", "light");
        };
        for (int i = 0; i < levels.size(); i++) {
            Maze maze = levels.get(i).maze();
            for (String move : route(maze, game.player().position(), maze.find('1'))) { game.execute(move); }
            if (riddles) {
                game.execute("talk");
                assertTrue(game.execute("answer " + answers.get(i)).contains("Correct!"));
            } else {
                for (int turn = 0; turn < 10 && !game.player().has(Player.KEY) && !game.finished(); turn++) {
                    game.execute("fight");
                }
                assertFalse(game.finished(), "Combat route must be survivable: " + difficulty);
                game.execute("use weapon"); game.execute("use herb");
            }
            assertTrue(game.player().has(Player.KEY));
            for (String move : route(maze, game.player().position(), maze.find('X'))) { game.execute(move); }
            assertFalse(game.player().has(Player.KEY));
            if (i + 1 < levels.size()) {
                assertEquals(i + 2, game.levelNumber()); assertFalse(game.finished());
            }
        }
        assertTrue(game.won(), "Campaign must be escapable: " + difficulty);
        if (riddles) { assertEquals(Player.MAX_HEALTH, game.player().health()); }
    }

    /** Verifies all difficulties have complete peaceful routes and level-specific answers.
     * @author Minh
     */
    @Test void allCampaignsSupportRiddleCompletion() {
        for (Difficulty difficulty : Difficulty.values()) { complete(difficulty, true); }
    }

    /** Verifies combat plus earned items can complete every bundled campaign.
     * @author Minh
     */
    @Test void allCampaignsSupportCombatCompletion() {
        for (Difficulty difficulty : Difficulty.values()) { complete(difficulty, false); }
    }

    /** Verifies map sets and NPC stats differ meaningfully and selected labels remain visible.
     * @author Minh
     */
    @Test void choicesHaveDistinctMapsAndChallenge() {
        List<Level> easy = CampaignLoader.load(Difficulty.EASY);
        List<Level> normal = CampaignLoader.load(Difficulty.NORMAL);
        List<Level> hard = CampaignLoader.load(Difficulty.HARD);
        assertEquals(2, easy.size()); assertEquals(2, normal.size()); assertEquals(3, hard.size());
        assertTrue(easy.get(0).maze().width() < normal.get(0).maze().width());
        int easyHealth = NpcLoader.load(easy.get(0).maze(), easy.get(0).npcResource()).get(0).health();
        int normalHealth = NpcLoader.load(normal.get(0).maze(), normal.get(0).npcResource()).get(0).health();
        int hardHealth = NpcLoader.load(hard.get(0).maze(), hard.get(0).npcResource()).get(0).health();
        assertTrue(easyHealth < normalHealth && normalHealth < hardHealth);
        for (Difficulty difficulty : Difficulty.values()) {
            assertTrue(new GameEngine(CampaignLoader.load(difficulty)).render().contains(difficulty.label()));
        }
        assertThrows(IllegalArgumentException.class, () -> CampaignLoader.load((Difficulty) null));
    }
}
