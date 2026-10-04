package config;

import engine.GameEngine;
import model.Difficulty;

/** Builds independent game sessions from the selected bundled difficulty.
 * @author Minh
 */
public final class DifficultyLoader {
    /** Prevents construction of this configuration utility.
     * @author Minh
     */
    private DifficultyLoader() { }

    /** Loads matching map and NPC resources before constructing the session.
     *
     * <p>No state is shared between calls. Invalid configuration is reported to
     * the caller instead of silently falling back to a different difficulty.</p>
     * @author Minh
     * @param difficulty chosen challenge, which must not be null
     * @return fresh session with full player health and unresolved encounters
     * @throws IllegalArgumentException if selection or configuration is invalid
     * @throws IllegalStateException if a bundled resource cannot be read
     */
    public static GameEngine load(Difficulty difficulty) {
        if (difficulty == null) { throw new IllegalArgumentException("Choose a difficulty first."); }
        return GameEngine.campaign(CampaignLoader.load(difficulty.campaignResource()));
    }
}
