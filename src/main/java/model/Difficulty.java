package model;

/** Ready-to-play single-map choices with their matching encounter resources.
 * @author Minh
 */
public enum Difficulty {
    EASY("Easy", "Short route and a weaker NPC.", "/difficulty/easy/maze.txt", "/difficulty/easy/npcs.properties"),
    NORMAL("Normal", "The original prison map and balanced encounters.", "/maze.txt", "/npcs.properties"),
    HARD("Hard", "Larger maze and a tougher NPC.", "/difficulty/hard/maze.txt", "/difficulty/hard/npcs.properties");

    private final String label;
    private final String description;
    private final String mazeResource;
    private final String npcResource;

    /** Defines a menu choice and the paired resources used to start it.
     * @author Minh
     * @param label player-facing name
     * @param description explanation of the challenge
     * @param mazeResource absolute map resource path
     * @param npcResource absolute matching NPC resource path
     */
    Difficulty(String label, String description, String mazeResource, String npcResource) {
        this.label = label;
        this.description = description;
        this.mazeResource = mazeResource;
        this.npcResource = npcResource;
    }

    /** Returns the display name without changing configuration.
     * @author Minh
     * @return difficulty label
     */
    public String label() { return label; }

    /** Describes the challenge before the player makes a selection.
     * @author Minh
     * @return brief menu description
     */
    public String description() { return description; }

    /** Identifies the bundled maze to load for this selection.
     * @author Minh
     * @return absolute maze resource path
     */
    public String mazeResource() { return mazeResource; }

    /** Identifies the NPC configuration matched to the selected maze.
     * @author Minh
     * @return absolute NPC resource path
     */
    public String npcResource() { return npcResource; }
}
