# Run Maze Escape with Docker

This is an interactive terminal game, not a web application. No ports are needed.
The image includes the difficulty menu and all Easy, Normal and Hard map sets.

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

Each exit consumes one key and leads to the next map. Health, equipment and other
items carry over. Escaping the last map wins. Progress is not saved after exit.

## Without Compose

```sh
docker build -t maze-escape:local .
docker run --rm -it maze-escape:local
```

Keep **both `-i` and `-t`**: the continuous UI needs input and a terminal. Run in a
real terminal rather than an IDE output pane. For Compose, use `run` as shown
above rather than `up -d`, which starts a detached session with no playable input.

## Build details

- The build stage uses Java 17 and the project's Gradle wrapper.
- Every image build runs the unit/integration tests and creates the application distribution.
- The runtime contains Java 17, terminal capability support and the built application.
- The application runs as an unprivileged user.
- The build context excludes Git history, local build output and planning documents.
- The first build requires network access for base images, Gradle and Maven dependencies.

If Docker cannot connect to `dockerDesktopLinuxEngine`, start Docker Desktop and
wait until its Linux engine is running. Exit the game with **Q**, or use **Ctrl+C**
during play. Containers launched with `--rm` are removed when they exit.
