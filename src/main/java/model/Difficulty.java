package model;

/** Bundled campaign choices with descriptions shown before play.
 * @author Minh
 */
public enum Difficulty {
    EASY("Easy", "2 short maps; weaker NPCs and simple routes.", "/campaigns/easy/levels.properties"),
    NORMAL("Normal", "2 maps; balanced combat and exploration.", "/campaigns/normal/levels.properties"),
    HARD("Hard", "3 larger maps; tougher NPCs. Use herbs and equipment.", "/campaigns/hard/levels.properties");

    private final String label;
    private final String description;
    private final String resource;

    /**
     * Defines a selectable difficulty and its bundled campaign resource.
     * @author Minh
     * @param label display name
     * @param description player-facing explanation of the challenge
     * @param resource absolute classpath manifest path
     */
    Difficulty(String label, String description, String resource) {
        this.label = label; this.description = description; this.resource = resource;
    }

    /**
     * Returns the name displayed in the selection menu.
     * @author Minh
     * @return difficulty name
     */
    public String label() { return label; }

    /**
     * Explains the map count and challenge before a player chooses.
     * @author Minh
     * @return concise difficulty description
     */
    public String description() { return description; }

    /**
     * Identifies the bundled campaign associated with this choice.
     * @author Minh
     * @return absolute manifest resource path
     */
    public String resource() { return resource; }
}
