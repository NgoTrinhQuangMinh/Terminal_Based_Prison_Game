package engine;

import config.CampaignLoader;
import model.Level;
import model.Maze;
import model.Player;
import model.Position;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Tests linked-level progression through the public command interface.
 * @author Minh
 */
class LinkedMapsTest {
    /**
     * Creates two small levels reusing marker 1 with different riddle configurations.
     * @author Minh
     * @return independent campaign state
     */
    private GameEngine game() {
        return new GameEngine(List.of(
                new Level("First", new Maze(List.of("P1X")), "/npcs.properties"),
                new Level("Second", new Maze(List.of("P.1.X", ".....")), "/levels/courtyard-npcs.properties")));
    }

    /**
     * Resolves the first encounter and enters the next map.
     * @author Minh
     * @param game campaign to advance
     */
    private void next(GameEngine game) {
        game.execute("d"); game.execute("talk"); game.execute("answer clock"); game.execute("d");
    }

    /** Verifies locked exits do not change levels or end play.
 * @author Minh
 */
    @Test void lockedDoorKeepsCurrentLevel() {
        GameEngine game = game();
        game.execute("d");
        assertTrue(game.execute("d").contains("locked"));
        assertEquals(1, game.levelNumber());
        assertEquals(new Position(1, 0), game.player().position());
        assertFalse(game.finished());
    }

    /** Verifies a transition consumes one key while preserving player identity and resources.
 * @author Minh
 */
    @Test void transitionPreservesPlayerAndConsumesOneKey() {
        GameEngine game = game();
        Player player = game.player();
        player.damage(2); player.collect(Player.WEAPON); player.use("weapon");
        next(game);
        assertSame(player, game.player());
        assertEquals(2, game.levelNumber());
        assertEquals(new Position(0, 0), player.position());
        assertEquals(8, player.health()); assertEquals(5, player.attack());
        assertTrue(player.has(Player.WEAPON)); assertTrue(player.has(Player.HERB));
        assertFalse(player.has(Player.KEY)); assertFalse(game.finished()); assertFalse(game.won());
        assertTrue(game.render().startsWith("Level 2/2: Second\n@.N.X\n.....\n"));
    }

    /** Verifies each map has fresh dialogue and distinct configuration despite repeated markers.
 * @author Minh
 */
    @Test void nextLevelUsesNewRiddleAndNeedsNewKey() {
        GameEngine game = game(); next(game);
        game.execute("d"); game.execute("d");
        assertTrue(game.execute("answer piano").contains("hear its riddle first"));
        assertTrue(game.execute("talk").contains("keys but cannot open doors"));
        assertTrue(game.execute("answer clock").contains("Incorrect"));
        game.execute("d"); assertTrue(game.execute("d").contains("locked"));
        game.execute("a"); game.execute("answer piano");
        game.execute("d"); game.execute("d");
        assertTrue(game.won()); assertTrue(game.finished()); assertFalse(game.player().has(Player.KEY));
        Position exit = game.player().position();
        game.execute("a"); assertEquals(exit, game.player().position());
    }

    /** Verifies transitions preserve spare keys rather than clearing the whole inventory.
 * @author Minh
 */
    @Test void consumesExactlyOneKey() {
        GameEngine game = game();
        game.player().collect(Player.KEY); game.player().collect(Player.KEY);
        game.execute("d"); game.execute("d");
        assertEquals(1, game.player().inventory().stream().filter(Player.KEY::equals).count());
    }

    /** Verifies a missing or malformed destination NPC resource leaves live state unchanged.
 * @author Minh
 */
    @Test void failedTransitionIsAtomic() {
        for (String resource : List.of("/missing-npcs.properties", "/levels-invalid-npcs.properties")) {
            GameEngine game = new GameEngine(List.of(
                    new Level("First", new Maze(List.of("P1X")), "/npcs.properties"),
                    new Level("Broken", new Maze(List.of("P1X")), resource)));
            game.execute("d"); game.execute("talk"); game.execute("answer clock");
            String before = game.render(); List<String> inventory = game.player().inventory();
            assertTrue(game.execute("d").contains("Could not enter the next level"));
            assertEquals(before, game.render()); assertEquals(inventory, game.player().inventory());
            assertFalse(game.finished()); assertEquals(1, game.levelNumber());
            assertTrue(game.execute("fight").contains("no NPC"));
        }
    }

    /** Verifies the bundled campaign is escapable through riddles in both maps.
 * @author Minh
 */
    @Test void bundledCampaignCanBeCompleted() {
        GameEngine game = new GameEngine(CampaignLoader.loadDefault());
        for (String command : "d d s s d d d".split(" ")) { game.execute(command); }
        game.execute("talk"); game.execute("answer clock");
        for (String command : "d d d w w".split(" ")) { game.execute(command); }
        assertEquals(2, game.levelNumber()); assertFalse(game.finished());
        game.execute("d"); game.execute("d"); game.execute("d");
        game.execute("talk"); game.execute("answer piano");
        game.execute("d"); game.execute("d"); game.execute("d");
        assertTrue(game.won()); assertEquals(10, game.player().health());
    }

    /** Verifies configurations can be reused without sharing mutable encounters between games.
 * @author Minh
 */
    @Test void separateCampaignsHaveFreshEncounters() {
        List<Level> levels = CampaignLoader.loadDefault();
        GameEngine first = new GameEngine(levels); GameEngine second = new GameEngine(levels);
        for (GameEngine game : List.of(first, second)) {
            for (String command : "d d s s d d d".split(" ")) { game.execute(command); }
        }
        first.execute("fight");
        assertEquals(10, second.player().health());
        assertTrue(second.execute("fight").contains("NPC has 3 health"));
    }
}
