# Terminal Based Prison Game

A Java 17 terminal RPG. Escape a prison by exploring a maze, fighting NPCs or
solving their riddles, collecting items and reaching the exit with its key.
Choose Easy, Normal or Hard before playing; each difficulty has a two-map campaign.
Unlock the prison door to enter the courtyard, then unlock its exit to win.
Each door consumes one key. Health, equipment and unused items carry forward.

## Play

Clone this repository and run commands from its root:

```sh
git clone https://github.com/NgoTrinhQuangMinh/Terminal_Based_Prison_Game.git
cd Terminal_Based_Prison_Game
```

On Windows with Java 17 installed, use PowerShell or Windows Terminal:

```powershell
./play.bat
```

On Linux or macOS:

```sh
chmod +x gradlew
./gradlew installDist
./build/install/prison-game/bin/prison-game
```

The continuous UI needs a real terminal; 100 columns by 35 rows is recommended.
Use WASD/arrows to move, F to fight, T to answer a riddle, H to heal, E to equip,
I to view inventory and Q to quit. Answer mode uses Enter to submit and Escape
to cancel. Use `./play.bat --line` on Windows or
`./gradlew run --args="--line" --console=plain` for scripted line input.

## Docker

With Docker running (Linux containers on Windows):

```sh
docker compose run --build --rm game
```

No host Java installation is needed for Docker. See [DOCKER.md](DOCKER.md).

## Development and verification

```sh
./gradlew clean test runTester installDist
```

On Windows use `./gradlew.bat` instead. Test reports are in
`build/reports/tests/test/index.html`. Generate API docs with `./gradlew javadoc`.

GitHub Actions runs **build → test → package** on main and pull requests.
Packaging builds the image with tests enabled, checks its non-root user, and
completes combat and continuous UI riddle playthroughs. Test reports and Docker
verification logs are uploaded as workflow artifacts. See [ci/README.md](ci/README.md).

## Documentation and history

- [Continuous UI](CONTINUOUS-UI.md)
- [Difficulty selection](DIFFICULTY-SELECTION.md)
- [Linked maps](LINKED-MAPS.md)
- [Contributing](CONTRIBUTING.md)
- [User stories](Activity-1-User-Stories.md)
- [MIT license](LICENSE.md)

The original WED commit history and historical branches are retained. Existing
issue numbers in imported commits and documents refer to the original project;
they are not automatically recreated as GitHub issues. New development uses
GitHub issues and pull requests against main.
