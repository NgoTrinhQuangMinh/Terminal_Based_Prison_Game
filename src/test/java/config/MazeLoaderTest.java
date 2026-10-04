package config;

import model.Maze;
import model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests loading the bundled prison maze resource.
 *
 * @author Lia Huang
 */
class MazeLoaderTest {

    @Test
    void loadsDefaultMazeFromResource() {
        Maze maze = MazeLoader.loadDefault();

        assertEquals(11, maze.width());
        assertEquals(7, maze.height());

        assertEquals(
                new Position(1, 1),
                maze.find('P')
        );

        assertEquals(
                new Position(9, 1),
                maze.find('X')
        );

        assertEquals(
                List.of('1', '2'),
                maze.npcMarkers()
        );
    }

    @Test
    void loadedMazeMatchesResourceLayout() {
        Maze maze = MazeLoader.loadDefault();

        assertTrue(
                maze.isWall(new Position(0, 0))
        );

        assertTrue(
                maze.isWall(new Position(4, 1))
        );

        assertEquals(
                '.',
                maze.at(new Position(2, 1))
        );

        assertEquals(
                '1',
                maze.at(new Position(6, 3))
        );

        assertEquals(
                '2',
                maze.at(new Position(7, 5))
        );
    }

    @Test
    void eachLoadCreatesSeparateMazeInstance() {
        Maze first = MazeLoader.loadDefault();
        Maze second = MazeLoader.loadDefault();

        assertNotSame(first, second);

        assertEquals(
                first.width(),
                second.width()
        );

        assertEquals(
                first.height(),
                second.height()
        );

        assertEquals(
                first.find('P'),
                second.find('P')
        );

        assertEquals(
                first.find('X'),
                second.find('X')
        );
    }
}