package config;

import engine.GameEngine;
import model.Level;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Validates campaign definitions and resource failures.
 * @author Minh
 */
class CampaignLoaderTest {
    /** Verifies the shipped manifest loads two different map sizes and immutable definitions.
 * @author Minh
 */
    @Test void loadsDefaultCampaign() {
        List<Level> levels = CampaignLoader.loadDefault();
        assertEquals(2, levels.size());
        assertEquals("Courtyard", levels.get(1).name());
        assertNotEquals(levels.get(0).maze().height(), levels.get(1).maze().height());
        assertThrows(UnsupportedOperationException.class, () -> levels.clear());
    }
    /** Verifies missing manifests and maps report loading failures.
 * @author Minh
 */
    @Test void missingResourcesFail() {
        assertThrows(IllegalStateException.class, () -> CampaignLoader.load("/missing.properties"));
        assertThrows(IllegalStateException.class, () -> MazeLoader.load("/missing.txt"));
    }
    /** Verifies empty campaigns and malformed manifests are rejected before play.
 * @author Minh
 */
    @Test void invalidDefinitionsFail() {
        assertThrows(IllegalArgumentException.class, () -> GameEngine.campaign(List.<Level>of()));
        assertThrows(IllegalArgumentException.class, () -> CampaignLoader.load("/empty-campaign.properties"));
        assertThrows(IllegalArgumentException.class, () -> CampaignLoader.load("/incomplete-campaign.properties"));
        assertThrows(IllegalArgumentException.class, () -> new Level("", MazeLoader.loadDefault(), "/npcs.properties"));
    }
}
