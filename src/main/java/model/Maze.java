package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an immutable text-based prison maze.
 *
 * <p>The maze uses {@code -} and {@code |} for walls, {@code .} for floor,
 * {@code P} for the player start, {@code X} for the exit, and digits
 * {@code 1} to {@code 9} for NPC markers.</p>
 *
 * @author Lia Huang
 */
public class Maze {
    private final List<String> rows;

    /**
     * Creates a maze from rectangular text rows.
     *
     * <p>The layout must contain exactly one player start and one exit.
     * Each numbered NPC marker may appear at most once. The supplied rows
     * are copied so later changes to the caller's list cannot change the maze.</p>
     *
     * @param rows non-null, non-empty map rows
     * @throws IllegalArgumentException if the layout is empty, not rectangular,
     *                                  contains unsupported symbols, or has
     *                                  invalid start, exit, or NPC markers
     */
    public Maze(List<String> rows) {
        if (rows == null
                || rows.isEmpty()
                || rows.get(0) == null
                || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Maze must not be empty.");
        }

        int width = rows.get(0).length();

        for (String row : rows) {
            if (row == null
                    || row.length() != width
                    || !row.matches("[-|.PX1-9]+")) {
                throw new IllegalArgumentException(
                        "Maze must be rectangular with valid symbols."
                );
            }
        }

        String cells = String.join("", rows);

        requireSingleMarker(cells, 'P');
        requireSingleMarker(cells, 'X');

        for (char marker = '1'; marker <= '9'; marker++) {
            if (count(cells, marker) > 1) {
                throw new IllegalArgumentException(
                        "NPC markers must be unique: " + marker
                );
            }
        }

        this.rows = List.copyOf(rows);
    }

    /**
     * Returns the number of columns in the maze.
     *
     * @return maze width
     */
    public int width() {
        return rows.get(0).length();
    }

    /**
     * Returns the number of rows in the maze.
     *
     * @return maze height
     */
    public int height() {
        return rows.size();
    }

    /**
     * Returns the tile at a map position.
     *
     * <p>Positions outside the map are treated as walls so callers can use
     * the same check for map boundaries and wall collisions.</p>
     *
     * @param position zero-based map position
     * @return the tile at the position, or {@code |} when outside the map
     * @throws IllegalArgumentException if position is null
     */
    public char at(Position position) {
        if (position == null) {
            throw new IllegalArgumentException(
                    "Position must not be null."
            );
        }

        if (position.x() < 0
                || position.y() < 0
                || position.x() >= width()
                || position.y() >= height()) {
            return '|';
        }

        return rows.get(position.y()).charAt(position.x());
    }

    /**
     * Checks whether a position is blocked by a wall or map boundary.
     *
     * @param position position to check
     * @return {@code true} when the position contains {@code -} or {@code |},
     *         including positions outside the map
     */
    public boolean isWall(Position position) {
        char tile = at(position);
        return tile == '-' || tile == '|';
    }

    /**
     * Finds the first occurrence of a marker in the maze.
     *
     * @param marker tile marker to find
     * @return zero-based position of the marker
     * @throws IllegalArgumentException if the marker is not present
     */
    public Position find(char marker) {
        for (int y = 0; y < height(); y++) {
            int x = rows.get(y).indexOf(marker);

            if (x >= 0) {
                return new Position(x, y);
            }
        }

        throw new IllegalArgumentException(
                "Missing marker: " + marker
        );
    }

    /**
     * Returns all numbered NPC markers in map scan order.
     *
     * @return immutable list of NPC marker digits
     */
    public List<Character> npcMarkers() {
        List<Character> markers = new ArrayList<>();

        for (String row : rows) {
            for (char tile : row.toCharArray()) {
                if (tile >= '1' && tile <= '9') {
                    markers.add(tile);
                }
            }
        }

        return List.copyOf(markers);
    }

    /**
     * Checks that a marker appears exactly once in the maze.
     *
     * @param cells complete maze contents
     * @param marker marker to validate
     * @throws IllegalArgumentException if the marker does not appear exactly once
     */
    private static void requireSingleMarker(
            String cells,
            char marker
    ) {
        if (count(cells, marker) != 1) {
            throw new IllegalArgumentException(
                    "Maze needs exactly one " + marker + "."
            );
        }
    }

    /**
     * Counts occurrences of a marker.
     *
     * @param cells complete maze contents
     * @param marker marker to count
     * @return number of occurrences
     */
    private static long count(
            String cells,
            char marker
    ) {
        return cells.chars()
                .filter(cell -> cell == marker)
                .count();
    }
}