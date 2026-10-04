# Additional Development Issue Drafts

These three issues describe the continuous UI, difficulty selection and Dockerisation implemented in the test repository. Each has two alternative descriptions: a feature proposal and an implementation task. Use the appropriate template when creating or refining the same issue; these are not six separate issues.

The descriptions are written as work to implement, with unchecked acceptance criteria. No Git issue numbers have been assigned here. Dependencies use plain component names so each template can be posted independently.

**Priority:** P1 = player-facing functionality in this development sequence; P2 = packaging and development tooling that can follow the playable application.

| Issue | Priority | Prerequisites | Reference branch |
| --- | --- | --- | --- |
| Implement a continuous terminal UI | P1 | Command execution, rendering, player/NPC state and linked-map progression | `feature/game-ui` |
| Add difficulty selection and bundled map sets | P1 | Continuous terminal UI, linked-map progression and campaign/resource loading | `feature/difficulty-selection` |
| Dockerise the interactive game | P2 | Runnable application distribution and continuous terminal UI; difficulty selection for the complete version being packaged | `feature/docker-support` |

Linked-map progression is a separate prerequisite, not extra implementation work bundled into these three issues. The UI issue includes adapting the display and input state to transitions that the engine already supports. Difficulty selection chooses existing campaign definitions; it does not implement the transition rules again.

---

# Implement a continuous terminal UI

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Let players control the game with immediate key presses while viewing a continuously updated terminal screen. Provide a separate text-entry mode for NPC riddle answers.

## Why?

Players should be able to move and interact without submitting a complete command line for every action. A persistent map, status display and feedback area make the current situation easier to follow, including when moving between linked maps.

## Possible Approach

Use JLine for terminal input, key bindings and screen updates. Keep key translation and answer editing in a controller that delegates gameplay to the existing engine. Render the current engine state after each action and reset stale dialogue input when the level changes.

## Impact

- Terminal UI and application launch path.
- Input handling, answer editing and inventory visibility.
- Read-only engine access for whether a riddle can be answered.
- Terminal dependencies, launch instructions and UI tests.

## Questions

- Are WASD/arrow movement and the proposed interaction shortcuts suitable for the team?
- Should entering a riddle automatically switch to answer mode, with Escape returning to normal controls?
- Is a resize prompt acceptable when the map and controls cannot fit in the terminal?

**Template 2 — Implementation task**



**Blocks:** difficulty selection within the continuous application; verification of interactive container gameplay.

**Parallel work:** Terminal layout and controller tests can be developed together once engine contracts are available. Docker build preparation can proceed separately, but full interactive verification requires this UI.

## Technical Notes

Use `ConsoleUI` for JLine terminal lifecycle and rendering, and `GameControls` for input modes and key translation. Add only the read-only engine query needed to determine whether answering is available. Use the current engine renderer rather than caching the original map.

Use a mutable frame list with JLine's display updater so clearing the screen after a transition is supported. Keep gameplay rules in the engine. Provide `play.bat` for native Windows launching. Add detailed JavaDocs and author attribution for contributed classes and methods while preserving existing attribution.

---

# Add difficulty selection and bundled map sets

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Offer Easy, Normal and Hard at startup, each with a ready-to-play set of linked maps. Players can choose their challenge without creating or editing map files.

## Why?

Bundled difficulty choices make the game accessible immediately and provide increasing challenge through map layout, campaign length and NPC combat statistics.

## Possible Approach

Associate each difficulty with a campaign manifest containing ordered map and NPC resources. Present a terminal menu before constructing the engine, then launch the selected campaign using the same terminal and existing continuous UI.

## Impact

- Application entry point and startup menu.
- Difficulty definitions and campaign loading.
- Bundled map layouts and NPC configuration resources.
- Menu tests and complete campaign playthrough tests.

## Questions

- Are two short maps for Easy, two balanced maps for Normal and three larger maps for Hard an appropriate initial selection?
- Should Enter select Normal by default?
- Do the Hard maps encourage using herbs and equipment while remaining completable through riddles?

**Template 2 — Implementation task**

## Summary

Add a startup difficulty menu and ship distinct Easy, Normal and Hard campaigns, using the existing linked-map engine and continuous terminal UI.

## Motivation

Players need an immediate choice of suitable content rather than having to author configuration files before playing.

## Acceptance Criteria

- [ ] Show Easy, Normal and Hard before starting a session, including each campaign's map count and a short description of its challenge.
- [ ] Pressing 1, 2 or 3 selects the corresponding difficulty without requiring Enter. Enter defaults to Normal.
- [ ] Unsupported input leaves the menu active. Q, Escape, Ctrl+C, Ctrl+D and end-of-input cancel without constructing a game.
- [ ] Easy contains two short maps with weaker NPCs; Normal contains the existing two-map campaign; Hard contains three larger maps with tougher NPCs.
- [ ] Every required map and NPC resource is bundled with the application. Selecting any difficulty requires no manual file editing.
- [ ] The selected difficulty remains visible in level names during play.
- [ ] Reuse linked-map rules: each exit consumes one key, health/equipment/other inventory persist, and only escaping the final map wins.
- [ ] Menu selection and gameplay share the terminal without losing queued input; terminal attributes are restored on cancellation and exit.
- [ ] Configuration failures report the problem rather than silently loading a different difficulty.
- [ ] Test every campaign to completion through both riddles and combat with available items. Verify required NPCs and exits are reachable and sessions have independent state.
- [ ] Add menu tests for every selection, the default, invalid input, cancellation and handoff to gameplay; existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- continuous terminal UI — gameplay loop and terminal input/output integration.
- linked-map progression — ordered levels, transitions, key consumption and final victory.
- campaign configuration loading — loading ordered level definitions from a named manifest.
- maze and NPC configuration loading — validated layouts and fresh level-specific encounters.
- NPC combat, riddles and inventory effects — complete routes used to balance and verify each difficulty.

**Blocks:** packaging and verifying the complete difficulty-enabled application.

**Parallel work:** Map/NPC resource authoring and menu implementation can proceed in parallel once the campaign format is agreed. Full campaign verification requires both the content and gameplay dependencies.

## Technical Notes

Use `Difficulty` to describe choices, `DifficultyMenu` to collect input, `GameLauncher` to manage selection and launch, and `CampaignLoader` to load the chosen manifest. Keep bundled resources under `campaigns/easy`, `campaigns/normal` and `campaigns/hard`; Normal may reference the existing map resources.

Difficulty is selected through content and campaign configuration, not by duplicating engine rules. Random map generation, a map editor and changing difficulty during an active campaign are outside this issue. Add detailed JavaDocs and author attribution for contributed classes and methods.

---

# Dockerise the interactive game

**Priority:** P2

**Template 1 — Feature proposal**

## Idea

Package the difficulty-enabled game as a Linux Docker image that players can launch in an interactive terminal using Docker or Compose.

## Why?

Players and developers should be able to run a consistent Java environment without installing Java and Gradle directly on their machine. Building the image should also verify the game on Linux.

## Possible Approach

Use a multi-stage build: compile, test and assemble the distribution with Java 17 and the Gradle wrapper, then copy it into a smaller Java runtime image. Configure interactive input and a TTY, and document the terminal launch command.

## Impact

- Dockerfile, build context exclusions and Compose configuration.
- Build and launch documentation.
- Test terminal fixtures that must behave consistently on Windows and Linux.
- Distribution packaging and container runtime verification.

## Questions

- Is a Linux-container workflow acceptable for supported development machines?
- Should local builds remain the initial delivery method, or should publishing images to a registry be a separate issue?
- Is keeping all progress within a single run acceptable for this version?

**Template 2 — Implementation task**

## Summary

Provide a Docker image and Compose service for the interactive game, including all difficulty campaigns, and verify both the Linux build and terminal gameplay.

## Motivation

A reproducible runtime reduces machine-specific setup and ensures the shipped game can run outside the developer's native Windows Java installation.

## Acceptance Criteria

- [ ] A multi-stage Docker build uses Java 17 and the project's Gradle wrapper to run tests and create the application distribution.
- [ ] All existing tests remain enabled during image construction; a failing test prevents a successful image build.
- [ ] The runtime image contains the built game, Java runtime and terminal capability support, and runs the application as a non-root user.
- [ ] All Easy, Normal and Hard campaigns are present in the image and available through the startup menu.
- [ ] `docker compose run --build --rm game` starts an interactive session with input and TTY enabled.
- [ ] `docker run --rm -it maze-escape:local` starts the same game after building that image tag.
- [ ] The build handles Windows CRLF line endings in the Gradle wrapper. Its context excludes Git history, local build output and unrelated planning documents.
- [ ] Virtual-terminal tests run reliably on Windows and Linux while still checking escape sequences, input handling, EOF/cancellation and terminal cleanup.
- [ ] Validate the Compose configuration, build the image successfully, and verify difficulty selection, riddle interaction, a linked-map transition and clean quitting in the actual container.
- [ ] Document Docker Desktop/Linux-engine requirements, build/run commands, terminal sizing and the need for an attached interactive session. Explain that no network ports or persistent save volume are required.
- [ ] Existing native gameplay tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- Java application distribution — working Gradle wrapper, tests, entry point and `installDist` output.
- continuous terminal UI — functional terminal controls and cleanup suitable for an attached TTY.
- difficulty selection and bundled map sets — the complete player-facing version to package and verify.
- linked-map progression — transitions included in the container smoke test.

**Blocks:** distribution of a verified container image and later image publishing or deployment automation.

**Parallel work:** Dockerfile, context exclusions and documentation can be prepared while UI/content work proceeds. Final build and gameplay verification must use the integrated application.

## Technical Notes

Use a Java 17 JDK build stage and JRE runtime stage. Build with `test installDist`, preserve Gradle dependency caching, and execute the generated application launcher. Configure `stdin_open`, `tty` and an appropriate `TERM` value in Compose. This is a terminal application, so do not expose a web port.

Use explicit stream-backed xterm fixtures for automated UI tests rather than platform-dependent terminal-provider selection. Finite scripted input must not be consumed and closed by a native PTY pump before assertions can run. Keep real terminal-provider discovery in the production launcher.

Do not skip tests to make the image build succeed. Registry publication, cloud hosting and saved-game persistence are outside this issue.
