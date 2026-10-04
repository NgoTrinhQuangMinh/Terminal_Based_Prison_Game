# Corrected Issue Branch Implementation Report

The S03–S20 branches have been rebuilt and published with APIs introduced by their owning issues. These are new reconstructed commits, not historical development records.

## Starting point

`scaffold-from-scratch` contains the existing build configuration and a minimal no-op Main entry point. It contains no gameplay classes, future methods, placeholder interfaces or TODO bodies. Git records files rather than empty package directories; those appear with their first classes.

## How the design grows

- S03 introduces Position; S04 introduces Maze; S05 introduces MazeLoader and its resource.
- S06 introduces Player position, health and base attack because those are explicitly in that issue. Player does not exist on the starting branch, and inventory/equipment APIs do not exist until S07.
- S08 introduces the NPC model; S09 introduces NPC configuration loading.
- S10 introduces command definitions and parsing as its own independent feature.
- S11 introduces GameEngine session state and status accessors. Its movement, combat, riddle, dispatch and rendering methods are absent until their respective issues.
- S19 introduces ConsoleUI and replaces the minimal Main with application wiring.
- S20 adds the exact skeleton regression tests. It remains independent of S19.

## Branches and minimum prerequisites

Independent issues remain sibling branches. A prerequisite merge combines only necessary completed issues; earlier prerequisites are inherited rather than repeated as parents.

| Issue | Branch | Immediate prerequisites | Commits | Branch checks |
| --- | --- | --- | --- | --- |
| S03 | `s03-position` | `scaffold-from-scratch` | 1 | 2 checks passed |
| S04 | `s04-maze-model` | `s03-position` | 2 | 13 checks passed |
| S05 | `s05-maze-loader` | `s04-maze-model` | 1 | 15 checks passed |
| S06 | `s06-player-state` | `s03-position` | 2 | 7 checks passed |
| S07 | `s07-inventory-items` | `s06-player-state` | 2 | 13 checks passed |
| S08 | `s08-npc-model` | `s03-position` | 3 | 10 checks passed |
| S09 | `s09-npc-loader` | `s04-maze-model`, `s07-inventory-items`, `s08-npc-model` | 2 | 36 checks passed |
| S10 | `s10-commands` | `scaffold-from-scratch` | 1 | 4 checks passed |
| S11 | `s11-game-session` | `s09-npc-loader` | 2 | 39 checks passed |
| S12 | `s12-movement-exit` | `s13-active-npc` | 1 | 47 checks passed |
| S13 | `s13-active-npc` | `s11-game-session` | 1 | 43 checks passed |
| S14 | `s14-rewards` | `s11-game-session` | 1 | 41 checks passed |
| S15 | `s15-combat` | `s13-active-npc`, `s14-rewards` | 1 | 50 checks passed |
| S16 | `s16-riddles` | `s13-active-npc`, `s14-rewards` | 2 | 49 checks passed |
| S17 | `s17-command-dispatch` | `s10-commands`, `s12-movement-exit`, `s15-combat`, `s16-riddles` | 1 | 66 checks passed |
| S18 | `s18-rendering` | `s11-game-session` | 1 | 43 checks passed |
| S19 | `s19-terminal-startup` | `s05-maze-loader`, `s17-command-dispatch`, `s18-rendering` | 2 | 73 checks passed |
| S20 | `s20-integration-tests` | `s05-maze-loader`, `s17-command-dispatch`, `s18-rendering` | 2 | 72 checks passed |

## Validation and final result

- Every one of the 28 implementation commits compiles, as do the starting point and all 18 branch tips.
- Source checks confirm that methods appear only in their owning issue or a prerequisite; no future TODO APIs remain.
- Branch-specific behavioural checks pass using only classes and APIs available on that branch.
- All nine exact skeleton JUnit tests pass; the final Gradle test run also passes.
- All branch file sets and minimum prerequisite ancestry have been audited.
- `skeleton-complete` merges S19 and S20 and has exactly the same tracked Git tree as `feature/game-skeleton`: `0896ded25069439ed6b8770b259f2cd4e833cdd2`.
- All 20 local and remote branch tips were replaced using an atomic push with exact force-with-lease checks. No existing feature branch was modified. Branch names have no codex/ prefix.
- The original published history is recoverable from `build/issue-rebuild-v2/previous-published-history.bundle`.
- Temporary verification scripts and snapshots are ignored under `build/issue-rebuild-v2/`; they are not committed to the feature branches.
- The user-story and issue-planning documents remain local and unchanged. The current working branch remains feature/game-skeleton.

## Inspect the result

```powershell
git log --graph --oneline --all
git switch scaffold-from-scratch
git switch s06-player-state
git switch skeleton-complete
git diff feature/game-skeleton skeleton-complete
```

## Implementation commits

### S03

- `de05d795` feat(S03): implement immutable coordinate movement

### S04

- `b66cc6c6` feat(S04): validate and retain immutable maze rows
- `c493cb3f` feat(S04): implement maze queries and marker discovery

### S05

- `0c3a9c5c` feat(S05): load the bundled UTF-8 maze resource

### S06

- `ca8867a7` feat(S06): initialise player position and movement state
- `8915532f` feat(S06): implement player health and base attack

### S07

- `ee686280` feat(S07): implement item collection and inventory queries
- `85c75d69` feat(S07): implement healing and non-stacking weapon use

### S08

- `bb881de8` feat(S08): validate NPC data and expose encounter state
- `58808730` feat(S08): apply NPC damage and resolve encounters
- `d09c06d1` feat(S08): offer riddles and compare answers

### S09

- `58d02e4a` feat(S09): validate required NPC configuration properties
- `a5765f82` feat(S09): construct configured NPCs at maze markers

### S10

- `5ddf42a7` feat(S10): parse player actions and command aliases

### S11

- `b4e1c65a` feat(S11): initialise fresh player and NPC session state
- `df6e0766` feat(S11): expose victory and session completion status

### S12

- `f02b647b` feat(S12): implement movement, encounter feedback and keyed exit

### S13

- `ecf2650f` feat(S13): select unresolved NPCs at the player position

### S14

- `8a3775a2` feat(S14): collect and report configured NPC rewards

### S15

- `246c80ae` feat(S15): implement player-first NPC combat and rewards

### S16

- `b71f5a41` feat(S16): offer the current NPC riddle
- `d0c5bdc2` feat(S16): resolve correct answers and award encounter drops

### S17

- `278539c3` feat(S17): dispatch player commands through shared game rules

### S18

- `bbf5d035` feat(S18): render the map, live encounters and player status

### S19

- `d9b13582` feat(S19): run the line-based terminal command loop
- `5e3c4dea` feat(S19): wire the default game application entry point

### S20

- `b98fcd9f` test(S20): verify configuration loading and invalid maps
- `3f9ea5c8` test(S20): verify complete NPC and escape scenarios
