package config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import model.Level;

/** Loads an ordered sequence of linked levels from classpath properties.
 * @author Minh
 */
public final class CampaignLoader {
    /** Prevents utility construction.
 * @author Minh
 */
    private CampaignLoader() { }

    /**
     * Loads the bundled forward-only campaign.
     * @author Minh
     * @return immutable ordered level definitions with validated maps
     * @throws IllegalStateException if a required resource cannot be read
     * @throws IllegalArgumentException if campaign or map data is invalid
     */
    public static List<Level> loadDefault() { return load("/levels.properties"); }

    /**
     * Reads a manifest and loads each map; mutable NPCs are created when a level is entered.
     * @author Minh
     * @param resource absolute classpath manifest path
     * @return immutable non-empty sequence of levels
     * @throws IllegalStateException if a resource is missing or unreadable
     * @throws IllegalArgumentException if count, paths or map data are invalid
     */
    public static List<Level> load(String resource) {
        if (resource == null || !resource.startsWith("/") || resource.length() == 1) {
            throw new IllegalArgumentException("An absolute campaign resource path is required.");
        }
        var stream = CampaignLoader.class.getResourceAsStream(resource);
        if (stream == null) { throw new IllegalStateException("Missing campaign: " + resource); }
        Properties config = new Properties();
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            config.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read campaign: " + resource, exception);
        }
        int count = Integer.parseInt(required(config, "levels"));
        if (count < 1) { throw new IllegalArgumentException("Campaign must contain at least one level."); }
        List<Level> levels = new ArrayList<>();
        for (int index = 1; index <= count; index++) {
            String prefix = "level." + index + ".";
            levels.add(new Level(required(config, prefix + "name"),
                    MazeLoader.load(required(config, prefix + "map")), required(config, prefix + "npcs")));
        }
        return List.copyOf(levels);
    }

    /**
     * Reads a mandatory trimmed property without modifying the source.
     * @author Minh
     * @param config manifest properties
     * @param key property name
     * @return non-blank property value
     * @throws IllegalArgumentException if the property is absent or blank
     */
    private static String required(Properties config, String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) { throw new IllegalArgumentException("Missing property: " + key); }
        return value.trim();
    }
}
