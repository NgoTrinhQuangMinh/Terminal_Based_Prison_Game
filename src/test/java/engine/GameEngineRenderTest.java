package engine;

import java.util.List;

import model.Inventory;
import model.Maze;
import model.Npc;
import model.Position;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the text rendering of the current game map and player status.
 *
 * @author Lia Huang
 */
class GameEngineRenderTest {

    /**
     * Creates a small map with one NPC marker and an exit.
     *
     * @return a valid test maze
     */
    private Maze newMaze() {
        return new Maze(List.of(
                "-----",
                "|P1X|",
                "|...|",
                "-----"
        ));
    }

    /**
     * Creates an unresolved NPC on marker 1.
     *
     * @return a test NPC
     */
    private Npc newNpc() {
        return new Npc(
                new Position(2, 1),
                6,
                2,
                "What has hands but cannot clap?",
                "clock",
                List.of(Inventory.KEY)
        );
    }

    /**
     * Verifies the complete initial rendered map, legend and status.
     */
    @Test
    void rendersPlayerNpcExitAndStatus() {
        GameEngine engine =
                new GameEngine(
                        newMaze(),
                        List.of(newNpc())
                );

        assertEquals(
                "-----\n"
                        + "|@NX|\n"
                        + "|...|\n"
                        + "-----\n"
                        + "Legend: @ = player | N = unresolved NPC | X = exit\n"
                        + "Health: 10/10 | Attack: 3 | Key: no",
                engine.render()
        );
    }

    /**
     * Verifies that the player marker takes priority when the player
     * occupies the same tile as an unresolved NPC.
     */
    @Test
    void playerMarkerTakesPriorityOnNpcTile() {
        GameEngine engine =
                new GameEngine(
                        newMaze(),
                        List.of(newNpc())
                );

        engine.player().moveTo(
                new Position(2, 1)
        );

        List<String> lines =
                engine.render()
                        .lines()
                        .toList();

        assertEquals(
                "|.@X|",
                lines.get(1)
        );
    }

    /**
     * Verifies that a resolved NPC is rendered as normal floor.
     */
    @Test
    void resolvedNpcIsRenderedAsFloor() {
        Npc npc = newNpc();
        npc.resolve();

        GameEngine engine =
                new GameEngine(
                        newMaze(),
                        List.of(npc)
                );

        List<String> lines =
                engine.render()
                        .lines()
                        .toList();

        assertEquals(
                "|@.X|",
                lines.get(1)
        );
    }

    /**
     * Verifies that the rendered status reflects the current player state.
     */
    @Test
    void renderShowsCurrentHealthAttackAndKeyState() {
        GameEngine engine =
                new GameEngine(newMaze());

        engine.player()
                .inventory()
                .add(Inventory.KEY);

        engine.player()
                .inventory()
                .add(Inventory.WEAPON);

        engine.player().use("weapon");
        engine.player().damage(2);

        assertTrue(
                engine.render()
                        .endsWith(
                                "Health: 8/10 | Attack: 5 | Key: yes"
                        )
        );
    }

    /**
     * Verifies that rendering does not change the current game state.
     */
    @Test
    void renderingDoesNotChangeGameState() {
        Npc npc = newNpc();

        GameEngine engine =
                new GameEngine(
                        newMaze(),
                        List.of(npc)
                );

        Position playerBefore =
                engine.player().position();

        int npcHealthBefore =
                npc.health();

        List<String> inventoryBefore =
                engine.player()
                        .inventory()
                        .items();

        engine.render();

        assertEquals(
                playerBefore,
                engine.player().position()
        );

        assertEquals(
                npcHealthBefore,
                npc.health()
        );

        assertEquals(
                inventoryBefore,
                engine.player()
                        .inventory()
                        .items()
        );

        assertFalse(
                npc.resolved()
        );
    }
}