package model;

/**
 * Immutable level definition; encounter state is loaded separately for each game.
 * @author Minh
 * @param name display name for the level
 * @param maze validated immutable map
 * @param npcResource absolute classpath path to this level's NPC properties
 */
public record Level(String name, Maze maze, String npcResource) {
    /**
     * Validates a level definition without creating mutable NPC state.
     * @author Minh
     * @param name non-blank display name
     * @param maze non-null validated map
     * @param npcResource absolute classpath resource path
     * @throws IllegalArgumentException if any required value is invalid
     */
    public Level {
        if (name == null || name.isBlank() || maze == null || npcResource == null
                || !npcResource.startsWith("/") || npcResource.length() == 1) {
            throw new IllegalArgumentException("Level requires a name, maze and absolute NPC resource path.");
        }
    }
}
