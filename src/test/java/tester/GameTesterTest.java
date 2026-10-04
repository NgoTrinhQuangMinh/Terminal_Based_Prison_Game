package tester;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the automatic gameplay tester itself.
 *
 * @author Lia Huang
 */
class GameTesterTest {

    /**
     * Verifies that every configured gameplay scenario passes against the
     * bundled game configuration.
     */
    @Test
    void allAutomaticScenariosPass() {
        List<GameTester.ScenarioResult> results =
                GameTester.runAll();

        assertFalse(
                results.isEmpty()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                GameTester.ScenarioResult::passed
                        ),
                () ->
                        "Failed scenarios: "
                                + results.stream()
                                        .filter(
                                                result ->
                                                        !result.passed()
                                        )
                                        .map(
                                                GameTester
                                                        .ScenarioResult
                                                        ::name
                                        )
                                        .toList()
        );
    }

    /**
     * Verifies that the expected gameplay areas are covered by named
     * scenarios and that scenario names are unique.
     */
    @Test
    void scenariosHaveUniqueNamesAndExpectedCoverage() {
        List<GameTester.ScenarioResult> results =
                GameTester.runAll();

        Set<String> names =
                new HashSet<>();

        for (GameTester.ScenarioResult result : results) {
            assertTrue(
                    names.add(result.name())
            );

            assertFalse(
                    result.detail().isBlank()
            );
        }

        assertEquals(
                7,
                results.size()
        );

        assertTrue(
                names.contains(
                        "Blocked movement"
                )
        );

        assertTrue(
                names.contains(
                        "Valid movement"
                )
        );

        assertTrue(
                names.contains(
                        "Invalid command"
                )
        );

        assertTrue(
                names.contains(
                        "Locked exit"
                )
        );

        assertTrue(
                names.contains(
                        "Riddle escape"
                )
        );

        assertTrue(
                names.contains(
                        "Combat escape"
                )
        );

        assertTrue(
                names.contains(
                        "Rewards and item use"
                )
        );
    }
}