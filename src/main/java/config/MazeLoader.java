package config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import model.Maze;

/**
 * Loads the default prison maze from a classpath resource.
 *
 * <p>The loader only reads map data. Maze structure and marker validation
 * remain the responsibility of {@link Maze}.</p>
 *
 * @author Lia Huang
 * @author Minh
 */
public final class MazeLoader {
    private static final String DEFAULT_MAZE_RESOURCE = "/maze.txt";

    /**
     * Prevents construction of this utility class.
     */
    private MazeLoader() {
    }

    /**
     * Loads the bundled default prison maze.
     *
     * <p>The resource is read as UTF-8. All rows are passed directly to the
     * Maze constructor so the same validation rules are used for loaded maps
     * and maps created in tests.</p>
     *
     * @author Minh
     * @return a new validated Maze built from {@code /maze.txt}
     * @throws IllegalStateException if the resource is missing or cannot be read
     * @throws IllegalArgumentException if the loaded map fails Maze validation
     */
    public static Maze loadDefault() {
        return load(DEFAULT_MAZE_RESOURCE);
    }

    /**
     * Loads a named bundled maze with the same validation as the default map.
     *
     * <p>Reads UTF-8 rows and closes the resource before returning. Validation
     * stays in the Maze model; no player or encounter state is created here.</p>
     * @author Minh
     * @param resource absolute classpath path of a bundled map
     * @return a fresh validated maze
     * @throws IllegalArgumentException if the path or map contents are invalid
     * @throws IllegalStateException if the resource is missing or unreadable
     */
    public static Maze load(String resource) {
        if (resource == null || !resource.startsWith("/") || resource.isBlank()) {
            throw new IllegalArgumentException("An absolute maze resource path is required.");
        }
        InputStream stream =
                MazeLoader.class.getResourceAsStream(resource);

        if (stream == null) {
            throw new IllegalStateException(
                    "Missing maze resource: " + resource
            );
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        stream,
                        StandardCharsets.UTF_8
                ))) {

            List<String> rows = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                rows.add(line);
            }

            return new Maze(rows);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read maze: " + resource,
                    exception
            );
        }
    }
}