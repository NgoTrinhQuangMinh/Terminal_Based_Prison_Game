# Difficulty selection (#44)

Run `./play.bat` in a Windows terminal, or run the installed application launcher
as described in CONTINUOUS-UI.md. Choose a difficulty before the game starts:

| Key | Difficulty | Bundled map | Key NPC health / attack |
| --- | --- | --- | --- |
| 1 | Easy | Short garden route, 7 by 4 | 3 / 1 |
| 2 or Enter | Normal | Original prison, 11 by 7 | 6 / 2 |
| 3 | Hard | Outer walls, 13 by 9 | 12 / 2 |

The descriptions appear in the menu and the selected name stays in the game
header. Unsupported keys show guidance and keep the menu open. Q, Escape,
Ctrl+C, Ctrl+D or end-of-input cancel before a game session is created.

Each selection starts a **two-map campaign**, with matching NPC stats, riddles and rewards.
Collect a key through combat or a riddle, unlock the prison door, and continue
into the courtyard. Unlock the courtyard exit to win. Each door consumes one key;
health, equipment and other inventory carry forward.
All choices are bundled; no custom files or map editing are needed. Difficulty
cannot change during a session. See [LINKED-MAPS.md](LINKED-MAPS.md) for progression.

`--line` starts the Normal campaign with scripted command input and no menu.
Scripts must complete both maps before expecting victory.

## Implementation

`Difficulty` identifies each campaign manifest. `DifficultyLoader` uses
`CampaignLoader` to read ordered maps and constructs a fresh campaign engine.
Normal starts with the original resources; Easy and Hard start with their
existing difficulty maps. All campaigns finish at the courtyard.
The loaders report missing or invalid resources rather than substituting Normal.

`GameLauncher` shares a single JLine terminal between `DifficultyMenu` and
`ConsoleUI`, preserving buffered input. The menu restores terminal attributes
on selection, cancellation or failure, and the existing gameplay UI restores
screen and input state when play ends. Gameplay rules are unchanged.

## Validation

Run `./gradlew test runTester installDist` (or `./gradlew.bat` on Windows).
Tests cover numeric/default selection, invalid-key retry, all cancellation keys,
input handoff, visible difficulty, independent sessions and loader errors.
Complete combat and riddle routes verify every bundled map can be won.
