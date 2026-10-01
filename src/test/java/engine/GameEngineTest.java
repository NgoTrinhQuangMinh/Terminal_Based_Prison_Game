package engine;

import command.Command;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import model.Maze;
import model.Npc;
import model.Player;
import model.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests game session setup and the won/finished status.
 *
 * @author Xinran Tian
 */
class GameEngineTest {

    private static Maze newMaze() {
        return new Maze(List.of(
                "-----",
                "|P1X|",
                "|...|",
                "-----"
        ));
    }

    private static Npc newNpc() {
        return new Npc(new Position(2, 1), 6, 2,
                "What has hands but cannot clap?", "clock",
                List.of(Inventory.KEY));
    }

    /**
     * Verifies that a new session keeps the maze it was given.
     */
    @Test
    void newSessionKeepsMaze() {
        Maze maze = newMaze();

        GameEngine engine = new GameEngine(maze);

        assertSame(maze, engine.maze());
    }

    /**
     * Verifies that the player starts at the maze's start marker.
     */
    @Test
    void playerStartsAtStartMarker() {
        GameEngine engine = new GameEngine(newMaze());

        assertEquals(new Position(1, 1), engine.player().position());
    }

    /**
     * Verifies that the player starts with full health and no items.
     */
    @Test
    void playerStartsFreshWithFullHealthAndEmptyInventory() {
        GameEngine engine = new GameEngine(newMaze());

        assertEquals(Player.MAX_HEALTH, engine.player().health());
        assertTrue(engine.player().inventory().isEmpty());
    }

    /**
     * Verifies that a fresh game is neither won nor finished.
     */
    @Test
    void newGameIsNotWonOrFinished() {
        GameEngine engine = new GameEngine(newMaze());

        assertFalse(engine.won());
        assertFalse(engine.finished());
    }

    /**
     * Verifies that winning finishes the game.
     */
    @Test
    void winningFinishesTheGame() {
        GameEngine engine = new GameEngine(newMaze());

        engine.markWon();

        assertTrue(engine.won());
        assertTrue(engine.finished());
    }

    /**
     * Verifies that quitting finishes the game without winning it.
     */
    @Test
    void quittingFinishesTheGameWithoutWinning() {
        GameEngine engine = new GameEngine(newMaze());

        engine.quit();

        assertFalse(engine.won());
        assertTrue(engine.finished());
    }

    /**
     * Verifies that the game finishes when the player's health reaches zero.
     */
    @Test
    void playerDeathFinishesTheGame() {
        GameEngine engine = new GameEngine(newMaze());

        engine.player().damage(Player.MAX_HEALTH);

        assertFalse(engine.won());
        assertTrue(engine.finished());
    }

    /**
     * Verifies that a session without NPCs has an empty NPC list.
     */
    @Test
    void sessionWithoutNpcsHasNoNpcs() {
        GameEngine engine = new GameEngine(newMaze());

        assertTrue(engine.npcs().isEmpty());
    }

    /**
     * Verifies that the session holds the NPCs it was given.
     */
    @Test
    void sessionHoldsGivenNpcs() {
        Npc npc = newNpc();

        GameEngine engine = new GameEngine(newMaze(), List.of(npc));

        assertEquals(List.of(npc), engine.npcs());
    }

    /**
     * Verifies that changing the caller's list does not change the session.
     */
    @Test
    void npcListIsCopiedAndUnmodifiable() {
        List<Npc> npcs = new ArrayList<>();
        npcs.add(newNpc());

        GameEngine engine = new GameEngine(newMaze(), npcs);
        npcs.clear();

        assertEquals(1, engine.npcs().size());
        assertThrows(UnsupportedOperationException.class,
                () -> engine.npcs().add(newNpc()));
    }

    /**
     * Verifies that separate sessions do not share player state.
     */
    @Test
    void separateSessionsDoNotSharePlayerState() {
        GameEngine first = new GameEngine(newMaze());
        GameEngine second = new GameEngine(newMaze());

        first.player().damage(4);
        first.player().inventory().add(Inventory.KEY);
        first.quit();

        assertEquals(Player.MAX_HEALTH, second.player().health());
        assertTrue(second.player().inventory().isEmpty());
        assertFalse(second.finished());
    }

    /**
     * Verifies that a missing maze or NPC list is rejected.
     */
    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameEngine(null));
        assertThrows(IllegalArgumentException.class,
                () -> new GameEngine(newMaze(), null));
    }

    /**
     * Verifies that the player can move right into an accessible location.
     */
    @Test
    void playerMovesRight() {
        GameEngine engine = new GameEngine(newMaze());

        String feedback = engine.move(Command.RIGHT);

        assertEquals(new Position(2, 1), engine.player().position());
        assertEquals("Movement successful.", feedback);
    }

    /**
     * Verifies that the player can move backward into an accessible location.
     */
    @Test
    void playerMovesBackward() {
        GameEngine engine = new GameEngine(newMaze());

        String feedback = engine.move(Command.BACKWARD);

        assertEquals(new Position(1, 2), engine.player().position());
        assertEquals("Movement successful.", feedback);
    }

    /**
     * Verifies that the player can move left after first moving right.
     */
    @Test
    void playerMovesLeft() {
        GameEngine engine = new GameEngine(newMaze());

        engine.move(Command.RIGHT);
        String feedback = engine.move(Command.LEFT);

        assertEquals(new Position(1, 1), engine.player().position());
        assertEquals("Movement successful.", feedback);
    }

    /**
     * Verifies that the player can move forward after first moving backward.
     */
    @Test
    void playerMovesForward() {
        GameEngine engine = new GameEngine(newMaze());

        engine.move(Command.BACKWARD);
        String feedback = engine.move(Command.FORWARD);

        assertEquals(new Position(1, 1), engine.player().position());
        assertEquals("Movement successful.", feedback);
    }

    /**
     * Verifies that movement into a wall is prevented.
     */
    @Test
    void movementIntoWallIsBlocked() {
        GameEngine engine = new GameEngine(newMaze());

        Position startingPosition = engine.player().position();

        String feedback = engine.move(Command.LEFT);

        assertEquals(startingPosition, engine.player().position());
        assertEquals("Movement blocked.", feedback);
    }

    /**
     * Verifies that movement outside the playable area is prevented.
     */
    @Test
    void movementOutsidePlayableAreaIsBlocked() {
        Maze maze = new Maze(List.of(
                "P..",
                "...",
                "..X"
        ));

        GameEngine engine = new GameEngine(maze);

        Position startingPosition = engine.player().position();

        String feedback = engine.move(Command.LEFT);

        assertEquals(startingPosition, engine.player().position());
        assertEquals("Movement blocked.", feedback);
    }

    /**
     * Verifies that an unsupported command does not move the player.
     */
    @Test
    void unknownCommandDoesNotMovePlayer() {
        GameEngine engine = new GameEngine(newMaze());

        Position startingPosition = engine.player().position();

        String feedback = engine.move(Command.UNKNOWN);

        assertEquals(startingPosition, engine.player().position());
        assertEquals("Unknown movement command.", feedback);
    }

    /**
     * Verifies that a null command does not move the player.
     */
    @Test
    void nullCommandDoesNotMovePlayer() {
        GameEngine engine = new GameEngine(newMaze());

        Position startingPosition = engine.player().position();

        String feedback = engine.move(null);

        assertEquals(startingPosition, engine.player().position());
        assertEquals("Unknown movement command.", feedback);
    }
}