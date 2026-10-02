package config;

import model.Inventory;
import model.Maze;
import model.Npc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests NPC configuration loading through its public entry point and isolated resource fixtures. */
class NpcLoaderTest {
    @TempDir
    Path resources;

    /** Verifies bundled properties supply each marker's stats, riddle, answer and canonical rewards. */
    @Test
    void loadsConfiguredNpcsAtMatchingMarkers() {
        Maze maze = MazeLoader.loadDefault();
        List<Npc> npcs = NpcLoader.loadDefault(maze);
        assertEquals(2, npcs.size());
        Npc first = npcs.get(0);
        Npc second = npcs.get(1);
        assertEquals(maze.find('1'), first.position());
        assertEquals(6, first.health());
        assertEquals(2, first.attack());
        assertEquals(List.of(Inventory.HERB, Inventory.KEY), first.drops());
        assertFalse(first.riddleOffered());
        assertFalse(first.resolved());
        assertEquals("What has hands but cannot clap?", first.offerRiddle());
        assertTrue(first.accepts("clock"));
        assertEquals(maze.find('2'), second.position());
        assertEquals(8, second.health());
        assertEquals(3, second.attack());
        assertEquals(List.of(Inventory.HERB, Inventory.WEAPON), second.drops());
        assertEquals("What has to be broken before you can use it?", second.offerRiddle());
        assertTrue(second.accepts("egg"));
    }

    /** Verifies only markers present in the supplied maze are loaded, in map scan order. */
    @Test
    void followsMazeMarkersRatherThanPropertyOrder() {
        Maze maze = new Maze(List.of("P21X"));
        List<Npc> npcs = NpcLoader.loadDefault(maze);
        assertEquals(List.of(maze.find('2'), maze.find('1')),
                npcs.stream().map(Npc::position).toList());
        assertEquals(1, NpcLoader.loadDefault(new Maze(List.of("P2X"))).size());
        assertTrue(NpcLoader.loadDefault(new Maze(List.of("PX"))).isEmpty());
    }

    /** Verifies loading again does not reuse damaged, resolved or previously offered encounter state. */
    @Test
    void eachLoadCreatesIndependentEncounters() {
        Maze maze = MazeLoader.loadDefault();
        List<Npc> first = NpcLoader.loadDefault(maze);
        first.get(0).hit(6);
        first.get(0).offerRiddle();
        List<Npc> second = NpcLoader.loadDefault(maze);
        assertNotSame(first.get(0), second.get(0));
        assertEquals(6, second.get(0).health());
        assertFalse(second.get(0).resolved());
        assertFalse(second.get(0).riddleOffered());
        assertEquals(8, first.get(1).health());
        assertFalse(first.get(1).resolved());
    }

    /** Verifies UTF-8 text, surrounding spaces and repeated reward tokens survive configuration loading. */
    @Test
    void supportsUtf8AndRepeatedTrimmedDrops() throws Exception {
        String config = validConfig().replace("Question?", "Caf\u00e9 question?")
                .replace("drops=herb,key", "drops= herb, weapon, key, herb ");
        try (URLClassLoader loader = fixtureLoader(config)) {
            List<?> npcs = loadFixture(loader);
            Object npc = npcs.get(0);
            assertEquals(List.of(Inventory.HERB, Inventory.WEAPON, Inventory.KEY, Inventory.HERB),
                    npc.getClass().getMethod("drops").invoke(npc));
            assertEquals("Caf\u00e9 question?", npc.getClass().getMethod("offerRiddle").invoke(npc));
        }
    }

    /** Verifies every required property rejects both omission and whitespace-only values. */
    @Test
    void rejectsMissingAndBlankProperties() throws Exception {
        for (String key : List.of("health", "attack", "riddle", "answer", "drops")) {
            String property = "(?m)^npc\\.1\\." + key + "=.*$";
            assertInvalid(validConfig().replaceAll(property, ""), IllegalArgumentException.class);
            assertInvalid(validConfig().replaceAll(property, "npc.1." + key + "=   "),
                    IllegalArgumentException.class);
        }
    }

    /** Verifies malformed, out-of-range and non-positive combat values cannot create NPCs. */
    @Test
    void rejectsInvalidCombatValues() throws Exception {
        for (String key : List.of("health", "attack")) {
            for (String value : List.of("invalid", "2147483648", "0", "-1")) {
                assertInvalid(validConfig().replaceAll("(?m)^npc\\.1\\." + key + "=.*$",
                        "npc.1." + key + "=" + value), IllegalArgumentException.class);
            }
        }
    }

    /** Verifies unknown rewards and empty comma-separated tokens are rejected rather than ignored. */
    @Test
    void rejectsUnsupportedAndEmptyDropTokens() throws Exception {
        for (String drops : List.of("gold", "herb,", ",key", "herb,,key")) {
            assertInvalid(validConfig().replace("drops=herb,key", "drops=" + drops),
                    IllegalArgumentException.class);
        }
    }

    /** Verifies a maze marker without a corresponding configuration entry fails explicitly. */
    @Test
    void rejectsUnconfiguredMarker() {
        assertThrows(IllegalArgumentException.class,
                () -> NpcLoader.loadDefault(new Maze(List.of("P3X"))));
    }

    /** Verifies an absent classpath resource reports a loading failure. */
    @Test
    void rejectsMissingResource() throws Exception {
        assertInvalid(null, IllegalStateException.class);
    }

    /**
     * Provides one valid fixture that individual validation tests can change independently.
     *
     * @return properties for marker 1 with positive stats and two supported rewards
     */
    private String validConfig() {
        return "npc.1.health=6\nnpc.1.attack=2\nnpc.1.riddle=Question?\n"
                + "npc.1.answer=answer\nnpc.1.drops=herb,key\n";
    }

    /**
     * Loads production classes with an isolated resource directory, leaving bundled resources untouched.
     *
     * @param config UTF-8 fixture contents, or null to simulate a missing resource
     * @return a class loader that the caller must close
     * @throws IOException if the temporary fixture cannot be written
     */
    private URLClassLoader fixtureLoader(String config) throws IOException {
        Path fixture = Files.createTempDirectory(resources, "npc-config-");
        if (config != null) {
            Files.writeString(fixture.resolve("npcs.properties"), config, StandardCharsets.UTF_8);
        }
        URL classes = NpcLoader.class.getProtectionDomain().getCodeSource().getLocation();
        return new URLClassLoader(new URL[]{fixture.toUri().toURL(), classes},
                ClassLoader.getPlatformClassLoader());
    }

    /**
     * Invokes the public loader using model types from the same isolated class loader.
     *
     * @param loader loader containing the fixture and production classes
     * @return NPC objects created from the fixture
     * @throws ReflectiveOperationException if loading or invoking the production classes fails
     */
    private List<?> loadFixture(URLClassLoader loader) throws ReflectiveOperationException {
        Class<?> mazeClass = loader.loadClass("model.Maze");
        Object maze = mazeClass.getConstructor(List.class).newInstance(List.of("P1X"));
        return (List<?>) loader.loadClass("config.NpcLoader")
                .getMethod("loadDefault", mazeClass).invoke(null, maze);
    }

    /**
     * Checks a public loading failure without depending on private helper methods.
     *
     * @param config fixture contents, or null for a missing resource
     * @param expected expected underlying exception type
     * @throws Exception if fixture setup or cleanup fails
     */
    private void assertInvalid(String config, Class<? extends Throwable> expected) throws Exception {
        try (URLClassLoader loader = fixtureLoader(config)) {
            InvocationTargetException failure = assertThrows(InvocationTargetException.class,
                    () -> loadFixture(loader));
            assertInstanceOf(expected, failure.getCause());
        }
    }
}
