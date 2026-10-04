# Linked maps

Each difficulty now contains two stages: the prison and the courtyard. Both the
continuous UI and `--line` play these stages in order. The screen identifies
the active level and the total number of levels.

- Obtain an exit key by fighting an NPC or answering its riddle.
- Enter `X` to consume one key and move to the next map's `P` marker.
- Keep the same player, health, attack, equipment and remaining inventory.
- Encounters belong to the current map, even when maps reuse NPC marker numbers.
- Unlock the final exit to win. Earlier exits do not end the game.
- Progress is forward-only; there is no return door to completed maps.

The courtyard's riddle answer is `piano`. Its key NPC has 3 health / 1 attack on
Easy and 6 health / 2 attack on Normal and Hard. All difficulties can be completed
through either combat or riddles. Spare keys remain usable on later doors.

## Configuration

Each `src/main/resources/difficulty/<difficulty>/levels.properties` defines an
ordered campaign. Add stages by increasing `levels` and supplying these fields:

```properties
levels=2
level.1.name=Prison
level.1.map=/maze.txt
level.1.npcs=/npcs.properties
level.2.name=Courtyard
level.2.map=/levels/courtyard.txt
level.2.npcs=/difficulty/normal/courtyard-npcs.properties
```

Paths are absolute classpath resources. `CampaignLoader` validates maps on load;
`GameEngine.campaign(...)` loads fresh NPCs on entry to each level. If the next
NPC resource is missing or invalid, movement reports an error and leaves the
current level, player and key unchanged.

Existing `GameEngine(Maze)` and `GameEngine(Maze, List<Npc>)` constructors still
create standalone sessions for model tests and automatic tester scenarios.

## Verification

Run `./gradlew.bat test runTester installDist` on Windows or `./gradlew` elsewhere.
Regression tests cover locked doors, state preservation, one-key consumption,
new riddles, failed transitions, independent sessions, final victory and UI redraws.
Docker package checks complete both maps using line-mode combat and continuous
UI riddles on all three difficulties.
