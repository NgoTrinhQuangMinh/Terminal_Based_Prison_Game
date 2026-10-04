# Run Maze Escape with Docker (#42)

This is an interactive terminal game, not a web application. No ports are needed.
The image includes the difficulty menu and all Easy, Normal and Hard maps.

## Start the game

Start Docker Desktop with **Linux containers** enabled, then run from this folder
in Windows Terminal, PowerShell, or another interactive terminal:

```sh
docker compose run --build --rm game
```

Use a terminal around **100 columns by 35 rows** so the larger maps fit. The game
will ask you to resize if necessary. Choose **1 / 2 / 3** for Easy / Normal / Hard,
**Enter** for Normal, or **Q** to cancel. Gameplay uses WASD or arrow keys; the
on-screen controls explain combat, riddles, equipment and inventory.

Each difficulty starts one bundled map. Collect its key through combat or a
riddle, then reach the exit to win. Progress is not saved after exit.

## Without Compose

```sh
docker build -t terminal-prison-game:local .
docker run --rm -it terminal-prison-game:local
```

Keep **both `-i` and `-t`**: the continuous UI needs input and a terminal. Run in a
real terminal rather than an IDE output pane. For Compose, use `run` as shown
above rather than `up -d`, which starts a detached session with no playable input.

## Build details

- The build stage uses Java 17 and the project's Gradle wrapper.
- The build stage runs unit/integration tests before packaging the application.
  Docker may reuse a previously successful layer when the build inputs are unchanged.
- The runtime contains Java 17, terminal capability support and the built application.
- The application runs as an unprivileged user.
- The build context excludes Git history, local build output and planning documents.
- The first build requires network access for base images, Gradle and Maven dependencies.

If Docker cannot connect to `dockerDesktopLinuxEngine`, start Docker Desktop and
wait until its Linux engine is running. Exit the game with **Q**, or use **Ctrl+C**
during play. Containers launched with `--rm` are removed when they exit.


## Scripted line mode

The Normal campaign is available with line-based input:

```sh
docker run --rm -i terminal-prison-game:local --line
```

Supply line-based commands on standard input. Omit `-t` for piped scripts.
No host Java or Gradle installation is needed. Docker builds and packages both
interfaces; Java sources and gameplay rules are unchanged by issue #42.
