# Continuous terminal UI (#43)

The default interface updates one terminal screen and handles keys without Enter.
Gameplay still uses `GameEngine.execute()`; the automatic tester is unchanged.

## Run

Use Java 17 or newer in a real terminal. On Windows, run `./play.bat` from
PowerShell or Windows Terminal. The script builds the application distribution
and launches it directly so JLine can access the terminal.

On Linux/macOS, run `./gradlew installDist`, then:

```sh
./build/install/comp2120-wed10_a3_u7842952_u7922002_u7986490_u8001214/bin/comp2120-wed10_a3_u7842952_u7922002_u7986490_u8001214
```

Use a terminal at least 70 columns by 24 rows (100 by 35 recommended).
If it is too small, resize it and press a key to refresh. Movement is blocked
until the map fits; Ctrl+C/Ctrl+D still quit. Escape cancels answer input.

## Controls

| Key | Action |
| --- | --- |
| WASD or arrow keys | Move |
| F | Fight the current NPC |
| T | Talk and enter riddle-answer mode |
| H | Use a healing herb |
| E | Equip a weapon |
| I | Show/hide inventory |
| Q | Quit from normal controls |
| Ctrl+C / Ctrl+D | Quit from either input mode |

While answering, ordinary letters (including Q and movement keys) become text.
Enter submits, Backspace edits and Escape cancels. Incorrect answers can be
retried; correct answers return to normal controls. Answers are limited to 80
characters. On victory or defeat, press any key to return to the terminal.
Terminal input settings, cursor visibility and the normal screen are restored
when the loop ends, including when a gameplay or rendering exception occurs.

## Line-based compatibility

Use `./gradlew run --args="--line" --console=plain` for the original line-based
interface, including IDE consoles and redirected command scripts. Continuous
mode should be launched from the installed distribution, not Gradle's input pipe.

## Verification

Run `./gradlew test runTester`. Controller tests cover controls, answer editing,
input isolation and quit behaviour. Stream-backed xterm tests cover the key loop,
combat victory, riddle rewards, EOF, undersized terminals and error cleanup.
Existing line-interface tests remain in `LineConsoleUITest`.

This feature supports the current single map. Campaigns, difficulty selection
and Docker packaging are separate issues.
