package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests maze validation, tile lookup, walls and marker discovery.
 *
 * @author Lia Huang
 */
class MazeTest {

    /**
     * Creates a small valid maze used by the tests.
     *
     * @return valid maze rows
     */
    private static List<String> validRows() {
        return List.of(
                "-----",
                "|P1X|",
                "|...|",
                "-----"
        );
    }

    @Test
    void createsValidMazeAndReturnsDimensions() {
        Maze maze = new Maze(validRows());

        assertEquals(5, maze.width());
        assertEquals(4, maze.height());
    }

    @Test
    void rejectsEmptyMaze() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(List.of())
        );
    }

    @Test
    void rejectsEmptyRows() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(List.of(""))
        );
    }

    @Test
    void rejectsNonRectangularMaze() {
        List<String> rows = List.of(
                "-----",
                "|PX|"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(rows)
        );
    }

    @Test
    void rejectsUnsupportedSymbols() {
        List<String> rows = List.of(
                "-----",
                "|P@X|",
                "-----"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(rows)
        );
    }

    @Test
    void requiresExactlyOnePlayerStart() {
        List<String> missing = List.of(
                "-----",
                "|..X|",
                "-----"
        );

        List<String> repeated = List.of(
                "-----",
                "|PPX|",
                "-----"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(missing)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(repeated)
        );
    }

    @Test
    void requiresExactlyOneExit() {
        List<String> missing = List.of(
                "-----",
                "|P..|",
                "-----"
        );

        List<String> repeated = List.of(
                "-----",
                "|PXX|",
                "-----"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(missing)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(repeated)
        );
    }

    @Test
    void rejectsRepeatedNpcMarker() {
        List<String> rows = List.of(
                "------",
                "|P11X|",
                "------"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Maze(rows)
        );
    }

    @Test
    void readsTilesAtValidPositions() {
        Maze maze = new Maze(validRows());

        assertEquals(
                'P',
                maze.at(new Position(1, 1))
        );

        assertEquals(
                '1',
                maze.at(new Position(2, 1))
        );

        assertEquals(
                'X',
                maze.at(new Position(3, 1))
        );

        assertEquals(
                '.',
                maze.at(new Position(2, 2))
        );
    }

    @Test
    void treatsOutOfBoundsPositionsAsWalls() {
        Maze maze = new Maze(validRows());

        assertEquals(
                '|',
                maze.at(new Position(-1, 1))
        );

        assertEquals(
                '|',
                maze.at(new Position(5, 1))
        );

        assertEquals(
                '|',
                maze.at(new Position(1, -1))
        );

        assertEquals(
                '|',
                maze.at(new Position(1, 4))
        );

        assertTrue(
                maze.isWall(new Position(-1, 1))
        );

        assertTrue(
                maze.isWall(new Position(5, 1))
        );
    }

    @Test
    void recognisesHorizontalAndVerticalWalls() {
        Maze maze = new Maze(validRows());

        assertTrue(
                maze.isWall(new Position(0, 0))
        );

        assertTrue(
                maze.isWall(new Position(0, 1))
        );

        assertFalse(
                maze.isWall(new Position(1, 1))
        );

        assertFalse(
                maze.isWall(new Position(2, 2))
        );
    }

    @Test
    void findsStartExitAndNpcMarkers() {
        Maze maze = new Maze(validRows());

        assertEquals(
                new Position(1, 1),
                maze.find('P')
        );

        assertEquals(
                new Position(3, 1),
                maze.find('X')
        );

        assertEquals(
                new Position(2, 1),
                maze.find('1')
        );

        assertEquals(
                List.of('1'),
                maze.npcMarkers()
        );
    }

    @Test
    void reportsMissingMarker() {
        Maze maze = new Maze(validRows());

        assertThrows(
                IllegalArgumentException.class,
                () -> maze.find('9')
        );
    }

    @Test
    void returnsNpcMarkersInMapOrder() {
        Maze maze = new Maze(
                List.of(
                        "------",
                        "|P2.X|",
                        "|.1..|",
                        "------"
                )
        );

        assertEquals(
                List.of('2', '1'),
                maze.npcMarkers()
        );
    }

    @Test
    void copiesInputRows() {
        List<String> rows =
                new ArrayList<>(validRows());

        Maze maze =
                new Maze(rows);

        rows.set(
                1,
                "|P.X|"
        );

        assertEquals(
                '1',
                maze.at(new Position(2, 1))
        );
    }
}