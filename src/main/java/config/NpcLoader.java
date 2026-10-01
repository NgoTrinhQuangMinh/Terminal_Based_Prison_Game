package config;

import java.util.Properties;

/** Loads NPC stats, riddles, answers and drops from a properties file. */
public final class NpcLoader {
    /**
     * Prevents construction of the NPC-loading utility.
     *
     * <p>NPC creation is accessed through the static configuration-loading method.</p>
     */
    private NpcLoader() { }


    /**
     * Reads a mandatory non-blank configuration value.
     *
     * <p>Trims surrounding whitespace after checking that the property is present and contains non-whitespace text. The supplied Properties object is not modified.</p>
     *
     * @param config properties loaded from the NPC configuration
     * @param key required property name
     * @return the trimmed property value
     * @throws IllegalArgumentException if the property is absent or blank
     */
    private static String required(Properties config, String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) { throw new IllegalArgumentException("Missing property: " + key); }
        return value.trim();
    }
}
