# Skeleton Development Issue Drafts

This file contains S03–S20 only. Each issue has two alternative descriptions matching the supplied templates: a proposal for discussion and an implementation task. They describe planned work toward `feature/game-skeleton`, not completed work. Use the two versions for the same issue as it is refined; they do not require 36 separate board issues.

**Priority:** P0 = foundational work that unblocks other features; P1 = essential skeleton functionality.

S01 (component responsibilities) and S02 (build/test setup) are assumed prerequisites and are not drafted here. S-numbers are planning IDs, not real GitHub issue numbers; replace them with actual issue links when publishing. Dependencies below explicitly name the components each issue uses, including important model prerequisites already inherited through another issue. Blocks lists the reverse relationships. Follow the complete prerequisite chain; do not treat an indirect dependency as optional. Every implementation issue includes relevant tests; checkboxes are intentionally unchecked.

**Dependency correction:** S12 also depends on S13 because movement uses active-NPC lookup for encounter feedback. S13 can be verified with controlled model state before movement is implemented, so this does not create a cycle.

## Development order and parallel work

For this plan, finish and integrate the listed prerequisites before implementing and verifying a dependent issue against real components. Design discussion and test planning can happen earlier. If coding ahead against stubs, keep the issue blocked until its real dependencies are integrated and its acceptance checks pass.

The following groups give a valid order. Issues in the same row may proceed in parallel. This is not a requirement to wait for an entire row: start an issue as soon as its own Dependencies section is satisfied.

| Stage | Issues eligible to run in parallel | Required earlier work |
| --- | --- | --- |
| 1 | S03 Position; S10 command parsing | S01 and S02 |
| 2 | S04 Maze; S06 Player; S08 NPC | S03 |
| 3 | S05 maze loading; S07 inventory | S04 for S05; S06 for S07 |
| 4 | S09 NPC loading | S04, S07, S08 |
| 5 | S11 session initialisation | S04, S06, S09 |
| 6 | S13 active-NPC selection; S14 rewards; S18 rendering | Session and model prerequisites listed for each issue |
| 7 | S12 movement; S15 combat; S16 riddles | S13 for movement; S13 and S14 for combat/riddles, plus their model prerequisites |
| 8 | S17 command dispatch | S10, S11, S12, S15, S16, S07 |
| 9 | S19 terminal integration; S20 engine integration tests | S05, S17, S18, plus S11 for S19 |

Examples: **S04 Maze → S05 MazeLoader**; **S08 NPC → S09 NpcLoader → S11 session → S13 selection → S15 combat**. Combat also requires player state and reward handling as explicitly listed under S15.

S09 requires S07's item definitions, not herb-use behaviour itself. At the current issue granularity, S07 is a prerequisite; making just the item definitions a separate task could unblock NPC loading earlier.

S19 is not a prerequisite for S20 because the specified integration tests run engine commands without terminal input. Both may proceed together after their shared dependencies. Several engine issues edit `GameEngine.java`; coordinate method ownership and merge small changes to reduce conflicts even when the features are logically independent.

## Contents

- [S03 — Implement position representation](#s03)
- [S04 — Implement maze model and validation](#s04)
- [S05 — Load maze configuration](#s05)
- [S06 — Implement player state](#s06)
- [S07 — Implement player inventory and item use](#s07)
- [S08 — Implement NPC model](#s08)
- [S09 — Load NPC configuration](#s09)
- [S10 — Define and parse player commands](#s10)
- [S11 — Initialise game sessions and expose status](#s11)
- [S12 — Implement movement and exit conditions](#s12)
- [S13 — Select active NPCs and preserve encounter progress](#s13)
- [S14 — Award encounter rewards](#s14)
- [S15 — Implement NPC combat](#s15)
- [S16 — Implement NPC riddle interactions](#s16)
- [S17 — Connect commands to engine actions](#s17)
- [S18 — Render the game map and status](#s18)
- [S19 — Connect the terminal UI and application entry point](#s19)
- [S20 — Verify complete gameplay scenarios](#s20)

---

<a id="s03"></a>

# S03 — Implement position representation

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Represent every map location using an immutable pair of coordinates and support calculating neighbouring locations.

## Why?

Movement, map lookup and NPC targeting need one consistent way to identify and compare positions.

## Possible Approach

Implement `Position` as a Java record with zero-based `x` and `y` coordinates. Its `move(dx, dy)` method returns a new position.

## Impact

`model/Position.java`; later maze, player and NPC models and engine movement.

## Questions

- Are zero-based columns and rows, with increasing y moving downward, clear to everyone working on the map and controls?

**Template 2 — Implementation task**

## Summary

Implement `Position` as a Java record with zero-based `x` and `y` coordinates. Its `move(dx, dy)` method returns a new position.

## Motivation

Movement, map lookup and NPC targeting need one consistent way to identify and compare positions.

## Acceptance Criteria

- [ ] A position exposes its x and y coordinates and positions with the same coordinates compare equal.
- [ ] Moving from (2, 3) by (-1, 1) returns (1, 4) without changing the original position.
- [ ] Positive, negative and zero offsets are supported; map boundaries are handled by the maze rather than Position.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- component responsibilities and contracts — agreed coordinate contract.
- Java build and test infrastructure — working Java build and tests.

**Blocks:** maze model and validation, player position and combat state, NPC model, player movement and exit conditions.

**Parallel work:** Can run alongside command definitions and parsing after component responsibilities and contracts and Java build and test infrastructure.

## Technical Notes

Match `model/Position.java` in the skeleton. Do not add wall checks, map dimensions or mutable setters to this value object.

---

<a id="s04"></a>

# S04 — Implement maze model and validation

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Provide a validated, immutable maze with tile queries, wall checks and marker discovery.

## Why?

Invalid maps should be rejected before play, and movement and configuration loading need reliable map queries.

## Possible Approach

Copy map rows into `Maze`, validate the text layout, and expose dimensions, tile lookup, wall checks, marker lookup and numbered NPC marker discovery.

## Impact

`model/Maze.java`; maze loading, NPC loading, movement and rendering.

## Questions

- Does the team agree on the supported map symbols and the one-start, one-exit rule for the initial game?

**Template 2 — Implementation task**

## Summary

Copy map rows into `Maze`, validate the text layout, and expose dimensions, tile lookup, wall checks, marker lookup and numbered NPC marker discovery.

## Motivation

Invalid maps should be rejected before play, and movement and configuration loading need reliable map queries.

## Acceptance Criteria

- [ ] Accept a non-empty rectangular layout containing only `-`, `|`, `.`, `P`, `X` and digits 1–9.
- [ ] Require exactly one P and one X; reject repeated occurrences of any numbered NPC marker.
- [ ] Return correct dimensions and tile contents; treat out-of-bounds coordinates as walls, and recognise both wall symbols.
- [ ] Locate requested markers, report an absent marker, and return the NPC markers present in the map.
- [ ] Changing the caller's original row list cannot modify the maze.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- position representation — Position coordinates for tile queries and marker lookup.

**Blocks:** maze configuration loading, NPC configuration loading, game session initialisation and status, player movement and exit conditions, map and player status rendering.

**Parallel work:** Can run alongside player position and combat state and NPC model after position representation.

## Technical Notes

Use `Position` from S03. Match `width()`, `height()`, `at()`, `isWall()`, `find()` and `npcMarkers()`. Map solvability checking is outside this issue.

---

<a id="s05"></a>

# S05 — Load maze configuration

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Load the default maze from an editable resource file instead of embedding the layout in Java code.

## Why?

A designer should be able to change the level layout without changing the maze model or engine.

## Possible Approach

Implement `MazeLoader.loadDefault()` to read UTF-8 lines from `/maze.txt` and pass them to the validated `Maze` constructor.

## Impact

`config/MazeLoader.java`, `src/main/resources/maze.txt`, startup and configuration tests.

## Questions

- Is the bundled map suitable for demonstrating both NPC routes and the locked exit?

**Template 2 — Implementation task**

## Summary

Implement `MazeLoader.loadDefault()` to read UTF-8 lines from `/maze.txt` and pass them to the validated `Maze` constructor.

## Motivation

A designer should be able to change the level layout without changing the maze model or engine.

## Acceptance Criteria

- [ ] The bundled maze resource loads into a Maze whose rows, dimensions and markers match the file.
- [ ] The skeleton's default map is included as a resource.
- [ ] A missing or unreadable resource produces an explicit loading failure.
- [ ] Invalid layout content is rejected by Maze validation rather than accepted by the loader.
- [ ] The resource reader is closed after loading.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze model and validation — validated Maze constructor to build the loaded map.

**Blocks:** terminal UI and application startup, complete gameplay integration tests.

**Parallel work:** Can run alongside player, inventory and NPC work once maze model and validation is complete.

## Technical Notes

Keep configuration reading separate from map validation. Use the skeleton's text format and classpath resource path; JSON/YAML conversion and custom file selection are outside scope.

---

<a id="s06"></a>

# S06 — Implement player state

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Represent the player's position and basic combat state independently of terminal input and engine rules.

## Why?

Movement and combat need a single owner for the player's current position, health and attack.

## Possible Approach

Implement Player construction, position access and updates, health and attack access, and damage handling.

## Impact

`model/Player.java`; inventory, engine initialisation, movement, combat and rendering.

## Questions

- Are starting health 10 and base attack 3 suitable for the initial balance?

**Template 2 — Implementation task**

## Summary

Implement Player construction, position access and updates, health and attack access, and damage handling.

## Motivation

Movement and combat need a single owner for the player's current position, health and attack.

## Acceptance Criteria

- [ ] A new player starts at the supplied position with health 10 and attack 3.
- [ ] Updating the player position changes the stored location without applying map rules.
- [ ] Positive damage reduces health, with a lower bound of zero.
- [ ] Negative damage does not heal or otherwise change player health.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- position representation — Position for the player location.

**Blocks:** player inventory and item use, game session initialisation and status, player movement and exit conditions, active NPC selection and encounter persistence, NPC combat, map and player status rendering.

**Parallel work:** Can run alongside maze model and validation and NPC model after position representation.

## Technical Notes

Use the skeleton's `MAX_HEALTH`, base attack and state accessors. Inventory and the weapon bonus are completed in S07; map collision checks belong to S12.

---

<a id="s07"></a>

# S07 — Implement player inventory and item use

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Allow the player to collect items, inspect inventory, heal with herbs and equip a weapon.

## Why?

Encounter rewards need useful effects, and the exit condition needs a reliable check for key ownership.

## Possible Approach

Keep inventory and equipment state in Player. Add collection, membership checks, a read-only inventory snapshot and item-use behaviour.

## Impact

`model/Player.java`; NPC drop loading, rewards, combat, exit checks and inventory display.

## Questions

- Does the team agree that herbs are consumed only when healing occurs and that equipping a sword gives a single non-stacking bonus?

**Template 2 — Implementation task**

## Summary

Keep inventory and equipment state in Player. Add collection, membership checks, a read-only inventory snapshot and item-use behaviour.

## Motivation

Encounter rewards need useful effects, and the exit condition needs a reliable check for key ownership.

## Acceptance Criteria

- [ ] Collecting an item adds it to inventory; membership checks work and callers cannot modify inventory through the returned snapshot.
- [ ] Using a held herb heals up to 4 health, never exceeding 10, and consumes one herb only when healing occurs.
- [ ] At full health a herb is retained; using a missing item reports the problem without changing state.
- [ ] Equipping a held sword changes attack from 3 to 5; equipping it again does not increase attack further.
- [ ] Support the skeleton's herb and weapon names/aliases; unsupported item use returns guidance without changing state.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- player position and combat state — player health, attack and state ownership.

**Blocks:** NPC configuration loading, player movement and exit conditions, encounter reward collection, NPC combat, command dispatch to game actions, map and player status rendering.

**Parallel work:** Can run alongside maze configuration loading and NPC model once player position and combat state is complete.

## Technical Notes

Use the existing key, herb and sword constants. A separate Item or Inventory class and item trading are outside this issue.

---

<a id="s08"></a>

# S08 — Implement NPC model

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Represent each NPC's configured data and mutable encounter state in a dedicated model.

## Why?

Combat and riddles should operate on the same NPC state and agree on whether an encounter has ended.

## Possible Approach

Implement Npc construction and accessors, damage handling, riddle offering and answer comparison, and resolution state.

## Impact

`model/Npc.java`; configuration loading, active-NPC lookup, combat, riddles and rendering.

## Questions

- Should both defeating an NPC and solving its riddle use the same resolved state, as proposed for the skeleton?

**Template 2 — Implementation task**

## Summary

Implement Npc construction and accessors, damage handling, riddle offering and answer comparison, and resolution state.

## Motivation

Combat and riddles should operate on the same NPC state and agree on whether an encounter has ended.

## Acceptance Criteria

- [ ] Store position, positive health and attack, non-blank riddle and answer, and a non-empty immutable copy of drops.
- [ ] New NPCs are unresolved and have not offered their riddle.
- [ ] Offering the riddle returns its text and records that it has been offered.
- [ ] An answer is accepted only after offering the riddle; comparison ignores case and surrounding whitespace.
- [ ] Damage cannot increase health or reduce it below zero; reaching zero resolves the encounter, and explicit resolution is also supported.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- position representation — Position for each NPC location.

**Blocks:** NPC configuration loading, active NPC selection and encounter persistence, encounter reward collection, NPC combat, NPC riddle interactions, map and player status rendering.

**Parallel work:** Can run alongside maze model and validation and player position and combat state after position representation.

## Technical Notes

Match the skeleton's model validation and state methods. Accepting an answer only reports a match; the engine resolves the NPC and awards rewards in S16.

---

<a id="s09"></a>

# S09 — Load NPC configuration

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Create NPC encounters from editable configuration matched to numbered map markers.

## Why?

Designers need to adjust NPC stats, riddles and rewards without editing Java logic.

## Possible Approach

Read `/npcs.properties`, look up `npc.<marker>.*` properties for markers found in the Maze, translate drop names and construct fresh Npc objects.

## Impact

`config/NpcLoader.java`, `src/main/resources/npcs.properties`, `Maze`, `Npc` and Player item constants.

## Questions

- Do the default NPC configurations provide both a key route and a useful weapon reward?

**Template 2 — Implementation task**

## Summary

Read `/npcs.properties`, look up `npc.<marker>.*` properties for markers found in the Maze, translate drop names and construct fresh Npc objects.

## Motivation

Designers need to adjust NPC stats, riddles and rewards without editing Java logic.

## Acceptance Criteria

- [ ] Create one NPC for each numbered marker in the map, at that marker's position.
- [ ] Load each NPC's health, attack, riddle, answer and drops from its matching properties.
- [ ] Map herb, weapon and key tokens to supported item names; preserve repeated reward tokens.
- [ ] Reject missing/blank required properties, invalid combat values and unsupported drops; report resource-loading failures.
- [ ] Repeated loads create fresh NPC state, and the default NPC resource matches the skeleton.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze model and validation — map marker discovery and position lookup.
- player inventory and item use — supported item constants for drop translation.
- NPC model — NPC construction and validation.

**Blocks:** game session initialisation and status.

**Parallel work:** Can run alongside maze configuration loading once its own model/item prerequisites are complete.

## Technical Notes

S04 supplies `npcMarkers()` and `find()`; S08 supplies model validation. S07 is a dependency because the skeleton loader uses Player's item constants, although item-use logic itself is not needed for loading.

---

<a id="s10"></a>

# S10 — Define and parse player commands

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Translate player command text into a consistent set of supported actions.

## Why?

The engine needs to recognise full commands and shortcuts without duplicating parsing rules across actions.

## Possible Approach

Implement the Command enum and `parse()` using the first input token, ignoring case and surrounding whitespace.

## Impact

`command/Command.java`; command dispatch, terminal help and future automated inputs.

## Questions

- Are the proposed aliases and the fixed forward/up and backward/down convention clear to players?

**Template 2 — Implementation task**

## Summary

Implement the Command enum and `parse()` using the first input token, ignoring case and surrounding whitespace.

## Motivation

The engine needs to recognise full commands and shortcuts without duplicating parsing rules across actions.

## Acceptance Criteria

- [ ] Recognise movement commands and WASD aliases, including backward and backwards.
- [ ] Recognise fight/f, talk/t, answer, use/equip, inventory/i, help/?, look/map and quit/q.
- [ ] Parsing ignores letter case and surrounding whitespace and recognises the action when arguments follow it.
- [ ] Null, blank and unrecognised input return UNKNOWN rather than throwing an exception.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- component responsibilities and contracts — agreed commands and aliases.
- Java build and test infrastructure — working Java build and tests.

**Blocks:** command dispatch to game actions.

**Parallel work:** Can run alongside position representation and the later model/configuration work; it does not require an engine.

## Technical Notes

Match the skeleton's enum and alias list. Argument extraction belongs to S17; arrow-key and immediate-input handling are outside the skeleton scope.

---

<a id="s11"></a>

# S11 — Initialise game sessions and expose status

**Priority:** P0

**Template 1 — Feature proposal**

## Idea

Create a fresh game session that owns the map reference, player, NPC collection and session status.

## Why?

Every action must operate on consistent session state, and the UI and tester need to know when play has ended.

## Possible Approach

Implement the GameEngine constructor, player accessor, win accessor and finished predicate. Create the player at P and load NPCs for the supplied Maze.

## Impact

`engine/GameEngine.java`; all engine actions, UI startup and automatic testing.

## Questions

- Does the team agree to keep win/quit flags in the engine and entity state in the models for the initial scope?

**Template 2 — Implementation task**

## Summary

Implement the GameEngine constructor, player accessor, win accessor and finished predicate. Create the player at P and load NPCs for the supplied Maze.

## Motivation

Every action must operate on consistent session state, and the UI and tester need to know when play has ended.

## Acceptance Criteria

- [ ] A new engine retains the supplied Maze, creates a player at P and loads fresh NPCs for that map.
- [ ] A fresh game is not won, not quit and not finished while the player is alive.
- [ ] The player accessor returns the session's player and won() reflects the win flag.
- [ ] finished() is true when the game is won, quit or the player's health is zero.
- [ ] Separate sessions do not share mutable player or NPC state.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze model and validation — map and starting-position lookup.
- player position and combat state — player construction and health state.
- NPC configuration loading — fresh NPC instances loaded for the map.

**Blocks:** player movement and exit conditions, active NPC selection and encounter persistence, encounter reward collection, NPC combat, NPC riddle interactions, command dispatch to game actions, map and player status rendering, terminal UI and application startup.

**Parallel work:** Can run alongside maze configuration loading if map loading is not yet finished; an already-constructed Maze can initialise the engine.

## Technical Notes

The constructor accepts a Maze, so this issue does not depend on MazeLoader. S12 sets victory and S17 handles quitting; public command-based verification of those transitions follows in S20.

---

<a id="s12"></a>

# S12 — Implement movement and exit conditions

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Allow the player to move through the maze, encounter NPCs and escape only after obtaining the key.

## Why?

Movement connects the map to encounters and gives item collection a concrete progression goal.

## Possible Approach

Implement `GameEngine.move(dx, dy)` using Position and Maze queries, check the exit key before moving, and return tile or encounter feedback.

## Impact

`engine/GameEngine.java`; player position, map collision checks, victory state and encounter feedback.

## Questions

- Is requiring the key before entering the exit tile the intended win condition?

**Template 2 — Implementation task**

## Summary

Implement `GameEngine.move(dx, dy)` using Position and Maze queries, check the exit key before moving, and return tile or encounter feedback.

## Motivation

Movement connects the map to encounters and gives item collection a concrete progression goal.

## Acceptance Criteria

- [ ] Movement into either wall type or outside the map leaves the player's position unchanged and returns blocked feedback.
- [ ] Entering a walkable non-exit tile updates the position by the requested offset.
- [ ] Attempting to enter X without the key leaves position unchanged and reports a locked exit.
- [ ] Entering X with the key updates position, marks the game won and reports success.
- [ ] Entering an unresolved NPC tile reports its current health, attack and interaction choices without automatically attacking.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- position representation — neighbouring-position calculation.
- maze model and validation — tile and wall checks.
- player position and combat state — player position updates.
- player inventory and item use — key ownership checks.
- game session initialisation and status — initialised session and victory flag.
- active NPC selection and encounter persistence — active-NPC lookup for arrival feedback.

**Blocks:** command dispatch to game actions.

**Parallel work:** Can run alongside NPC combat, NPC riddle interactions and map and player status rendering once its own prerequisites are complete.

## Technical Notes

S13 is an explicit dependency because the skeleton's move method calls `currentNpc()`. This corrects the earlier table's omitted dependency. Parsing movement commands belongs to S17.

---

<a id="s13"></a>

# S13 — Select active NPCs and preserve encounter progress

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Target only the unresolved NPC on the player's current tile and retain encounter progress during the session.

## Why?

Players should not interact with distant or completed encounters, and leaving a tile should not reset an NPC.

## Possible Approach

Implement `currentNpc()` by searching the existing session NPC collection for an unresolved NPC at the player's position. Reuse the loaded instances.

## Impact

`engine/GameEngine.java`; movement feedback, combat, riddles and encounter persistence.

## Questions

- Is keeping NPCs stationary and preserving their progress within the current game sufficient for this stage?

**Template 2 — Implementation task**

## Summary

Implement `currentNpc()` by searching the existing session NPC collection for an unresolved NPC at the player's position. Reuse the loaded instances.

## Motivation

Players should not interact with distant or completed encounters, and leaving a tile should not reset an NPC.

## Acceptance Criteria

- [ ] An unresolved NPC at the player's position is selected.
- [ ] No active NPC is selected for an empty tile, a distant NPC or a resolved NPC.
- [ ] Changing the player's location away from an NPC and back does not recreate it or reset its health or riddle state.
- [ ] NPC state remains independent: changing one encounter does not alter another.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- player position and combat state — current player position.
- NPC model — NPC position, resolution and persistent encounter state.
- game session initialisation and status — session-owned NPC collection.

**Blocks:** player movement and exit conditions, NPC combat, NPC riddle interactions.

**Parallel work:** Can run alongside encounter reward collection and map and player status rendering after their prerequisites are complete. Validate lookup with controlled player positions; do not wait for player movement and exit conditions.

## Technical Notes

Check selection with controlled positions and model state without requiring the movement implementation. Full leave-and-return command scenarios follow in S20, avoiding a dependency cycle with S12. Saving state between application runs is outside scope.

---

<a id="s14"></a>

# S14 — Award encounter rewards

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Provide one shared operation that adds an NPC's configured rewards to the player's inventory and reports them.

## Why?

Combat and riddle completion should award the same items consistently.

## Possible Approach

Implement `awardDrops(Npc)` by collecting every configured drop through Player and returning a reward message.

## Impact

`engine/GameEngine.java`; player inventory and combat/riddle completion.

## Questions

- Should reward messages list all awarded items, including repeated herbs, as proposed?

**Template 2 — Implementation task**

## Summary

Implement `awardDrops(Npc)` by collecting every configured drop through Player and returning a reward message.

## Motivation

Combat and riddle completion should award the same items consistently.

## Acceptance Criteria

- [ ] Awarding an NPC's rewards adds every configured item to the existing player inventory.
- [ ] Repeated entries in the drop list produce the same number of inventory items.
- [ ] The returned feedback identifies the awarded items.
- [ ] Awarding drops does not consume, heal with or equip the items and does not change the NPC's configured drop list.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- player inventory and item use — inventory collection.
- NPC model — configured NPC drops.
- game session initialisation and status — session player receiving rewards.

**Blocks:** NPC combat, NPC riddle interactions.

**Parallel work:** Can run alongside active NPC selection and encounter persistence and map and player status rendering after their prerequisites are complete. Test item transfer directly; do not wait for combat or riddles.

## Technical Notes

This method assumes its caller has completed the encounter. The skeleton does not put duplicate-award protection inside awardDrops; S13, S15 and S16 ensure completed NPCs cannot trigger it again. Verify that integration in S20.

---

<a id="s15"></a>

# S15 — Implement NPC combat

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Allow a player to resolve the current NPC encounter through turn-by-turn combat.

## Why?

Combat supplies the required basic NPC interaction and makes player and NPC stats meaningful.

## Possible Approach

Implement `fight()` using currentNpc, player attack, NPC damage and counterattack, followed by the shared reward operation on defeat.

## Impact

`engine/GameEngine.java`, Player health/attack, Npc health/resolution and inventory rewards.

## Questions

- Does a player-first exchange with no counterattack after NPC defeat provide the intended combat behaviour?

**Template 2 — Implementation task**

## Summary

Implement `fight()` using currentNpc, player attack, NPC damage and counterattack, followed by the shared reward operation on defeat.

## Motivation

Combat supplies the required basic NPC interaction and makes player and NPC stats meaningful.

## Acceptance Criteria

- [ ] Fighting without a current unresolved NPC reports that no target is available and changes no combat or inventory state.
- [ ] One fight action damages the NPC by the player's current attack; a surviving NPC counterattacks by its configured attack.
- [ ] With player health 10 and attack 3 against NPC health 6 and attack 2, the first exchange leaves player health 8 and NPC health 3.
- [ ] A defeated NPC is resolved, awards its drops and does not counterattack; subsequent fights cannot grant those drops again.
- [ ] Player health reaching zero produces game-over feedback; surviving exchanges report damage and remaining NPC health.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- player position and combat state — player health, attack and damage handling.
- player inventory and item use — equipped-weapon attack behaviour.
- NPC model — NPC health, damage and resolution.
- game session initialisation and status — session state and end-of-game predicate.
- active NPC selection and encounter persistence — current unresolved NPC selection.
- encounter reward collection — rewards after defeat.

**Blocks:** command dispatch to game actions.

**Parallel work:** Can run alongside NPC riddle interactions once shared prerequisites are complete; combat does not depend on riddles.

## Technical Notes

Use the skeleton's fight flow. S11 supplies the finished predicate and S17 prevents further commands after death. Weapon effects come from Player.attack(), not duplicate combat-specific equipment logic.

---

<a id="s16"></a>

# S16 — Implement NPC riddle interactions

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Allow the player to complete the current NPC encounter by hearing and correctly answering its riddle.

## Why?

Players need a non-combat route that earns the encounter's rewards without losing health.

## Possible Approach

Implement `talk()` and `answer(attempt)`. Use the active NPC's riddle state, validate the attempt and resolve the NPC before awarding drops.

## Impact

`engine/GameEngine.java`, Npc riddle/resolution state and player rewards.

## Questions

- Are unlimited retries without health penalties appropriate for the initial riddle route?

**Template 2 — Implementation task**

## Summary

Implement `talk()` and `answer(attempt)`. Use the active NPC's riddle state, validate the attempt and resolve the NPC before awarding drops.

## Motivation

Players need a non-combat route that earns the encounter's rewards without losing health.

## Acceptance Criteria

- [ ] Talking to the current unresolved NPC offers its configured riddle without damaging either participant.
- [ ] Talking or answering when no active NPC is present returns explanatory feedback and awards nothing.
- [ ] Answering before talking asks the player to hear the riddle first; empty or incorrect answers leave the encounter unresolved and award nothing.
- [ ] A correct answer, ignoring case and surrounding whitespace, resolves that NPC and awards its configured drops without player damage.
- [ ] The answer is checked against the current NPC only, and completed encounters cannot award rewards again through talk or answer.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- NPC model — riddle offering, answer checking and resolution.
- game session initialisation and status — initialised session state.
- active NPC selection and encounter persistence — current unresolved NPC selection.
- encounter reward collection — rewards after a correct answer.

**Blocks:** command dispatch to game actions.

**Parallel work:** Can run alongside NPC combat once shared prerequisites are complete; riddles do not depend on combat.

## Technical Notes

Match the skeleton's one-riddle/one-answer behaviour. Input buffering, edit keys and answer-entry modes belong to the later UI implementation and are excluded.

---

<a id="s17"></a>

# S17 — Connect commands to engine actions

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Provide one command execution entry point shared by normal play and future automated testing.

## Why?

All callers should exercise the same movement, encounter, item-use and session-ending rules.

## Possible Approach

Implement `execute(String)` to parse the action, preserve its optional argument, dispatch to the appropriate operation and return feedback.

## Impact

`engine/GameEngine.java`, Command parsing, Player item use and all game actions.

## Questions

- Does the proposed command/help vocabulary match the team's movement and interaction conventions?

**Template 2 — Implementation task**

## Summary

Implement `execute(String)` to parse the action, preserve its optional argument, dispatch to the appropriate operation and return feedback.

## Motivation

All callers should exercise the same movement, encounter, item-use and session-ending rules.

## Acceptance Criteria

- [ ] Movement commands dispatch to the correct offsets, and fight/talk/answer/use dispatch to their matching operations.
- [ ] Multi-word answer or item arguments are passed on without being split into separate actions.
- [ ] Inventory reports its contents or an empty state; help and look explain controls and the objective.
- [ ] Quit marks the session finished and returns goodbye feedback.
- [ ] Null, blank and unknown commands return guidance during active play; once finished, subsequent commands report the ended game without changing state.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- player inventory and item use — item use and inventory queries.
- command definitions and parsing — command parsing.
- game session initialisation and status — finished-state guard and quit flag.
- player movement and exit conditions — movement and victory behaviour.
- NPC combat — combat action.
- NPC riddle interactions — talk and answer actions.

**Blocks:** terminal UI and application startup, complete gameplay integration tests.

**Parallel work:** Can run alongside map and player status rendering if rendering is unfinished; command execution does not require a display.

## Technical Notes

Keep terminal reading outside the engine. Use the skeleton's two-part whitespace split and Command.parse(). This issue integrates existing actions rather than reimplementing their rules.

---

<a id="s18"></a>

# S18 — Render the game map and status

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Produce a textual view of the current map and player status.

## Why?

Players need to see their location, remaining encounters, the exit and their resources after each action.

## Possible Approach

Implement the skeleton's `GameEngine.render()` by combining the static maze with the player's position and unresolved NPCs, then appending a legend and stats.

## Impact

`engine/GameEngine.java`; map display, player status and terminal output.

## Questions

- Are the proposed symbols and health/attack/key status sufficient to understand the initial game?

**Template 2 — Implementation task**

## Summary

Implement the skeleton's `GameEngine.render()` by combining the static maze with the player's position and unresolved NPCs, then appending a legend and stats.

## Motivation

Players need to see their location, remaining encounters, the exit and their resources after each action.

## Acceptance Criteria

- [ ] Render the maze rows with walls and X preserved, while original P and numbered NPC markers are treated as floor.
- [ ] Display unresolved NPCs as N and the player as @, with the player marker taking precedence on a shared tile.
- [ ] Do not display resolved NPCs as active encounters.
- [ ] Include a legend and the player's current/max health, attack and key possession.
- [ ] Rendering does not modify game state or require a live terminal.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze model and validation — map dimensions and tile contents.
- player position and combat state — player position and combat stats.
- player inventory and item use — key ownership and equipped attack.
- NPC model — NPC positions and resolution state.
- game session initialisation and status — session map, player and NPC references.

**Blocks:** terminal UI and application startup, complete gameplay integration tests.

**Parallel work:** Can run alongside active NPC selection and encounter persistence/encounter reward collection and the later action implementations; it reads state without executing actions.

## Technical Notes

Match the skeleton's string renderer. Extracting a separate renderer, adding colour or using a full-screen terminal library is outside this issue.

---

<a id="s19"></a>

# S19 — Connect the terminal UI and application entry point

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Make the configured game playable through a line-based terminal interface.

## Why?

The separate engine features need an entry point and input/output loop that a player can run.

## Possible Approach

Wire Main to load the default maze and construct GameEngine and ConsoleUI. Read full command lines and display feedback plus the refreshed map.

## Impact

`Main.java`, `ui/ConsoleUI.java`, default resources and the application launch path.

## Questions

- Does the startup explanation give players enough information to begin moving and interacting with NPCs?

**Template 2 — Implementation task**

## Summary

Wire Main to load the default maze and construct GameEngine and ConsoleUI. Read full command lines and display feedback plus the refreshed map.

## Motivation

The separate engine features need an entry point and input/output loop that a player can run.

## Acceptance Criteria

- [ ] Starting the application loads the bundled maze, creates a fresh engine and starts ConsoleUI.
- [ ] Display the initial instructions, help and rendered game state.
- [ ] For each input line during play, execute the command and display its feedback and updated state.
- [ ] Stop reading commands when the game finishes or input reaches end-of-file.
- [ ] The UI delegates rules to the engine and uses the skeleton's line-based input without requiring additional terminal libraries.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze configuration loading — default maze loading at startup.
- game session initialisation and status — engine construction and session status.
- command dispatch to game actions — command execution and feedback.
- map and player status rendering — initial and refreshed display.

**Blocks:** No further implementation work in this skeleton plan.

**Parallel work:** Can run alongside complete gameplay integration tests once their respective prerequisites are complete.

## Technical Notes

Use `Scanner` and the skeleton's Main wiring. Immediate key controls, terminal resizing and dedicated riddle text-entry mode are outside the skeleton target.

---

<a id="s20"></a>

# S20 — Verify complete gameplay scenarios

**Priority:** P1

**Template 1 — Feature proposal**

## Idea

Verify that the connected game behaves correctly across complete command sequences.

## Why?

Individual components can pass unit tests while failures remain in their interaction, especially around rewards, state transitions and winning.

## Possible Approach

Add repeatable JUnit scenarios using a fresh engine and bundled resources. Follow the skeleton's MazeLoaderTest and GameEngineTest coverage, with assertions on state and feedback.

## Impact

`src/test/java/config/MazeLoaderTest.java`, `src/test/java/engine/GameEngineTest.java` and the Gradle test suite.

## Questions

- Do the combat and riddle routes demonstrate every essential integration, and which failure would most affect a player's ability to finish?

**Template 2 — Implementation task**

## Summary

Add repeatable JUnit scenarios using a fresh engine and bundled resources. Follow the skeleton's MazeLoaderTest and GameEngineTest coverage, with assertions on state and feedback.

## Motivation

Individual components can pass unit tests while failures remain in their interaction, especially around rewards, state transitions and winning.

## Acceptance Criteria

- [ ] Check default-map loading and invalid-map cases, and verify walls prevent movement.
- [ ] Verify the exit remains locked without the key and that a complete combat route can obtain it and win.
- [ ] Verify a riddle route rejects premature/incorrect answers, grants rewards on success and can reach victory without combat damage.
- [ ] Check one-time drops, healing behaviour, weapon bonuses and combat using each NPC's configured stats.
- [ ] Verify answers cannot target an NPC after leaving its tile, quitting ends play and commands after victory do not move the player.
- [ ] Run the complete test suite successfully with repeatable outcomes and fresh game state per scenario.
- [ ] Relevant tests are added or updated.
- [ ] Existing tests still pass.

## Dependencies

**Depends on (complete and integrate first):**

- maze configuration loading — real map configuration for repeatable scenarios.
- command dispatch to game actions — complete gameplay through the public command entry point.
- map and player status rendering — rendered-state assertions used by the skeleton tests.

**Blocks:** No further implementation work in this skeleton plan.

**Parallel work:** Can run alongside terminal UI and application startup: these JUnit scenarios call the engine directly and do not require ConsoleUI. Terminal-loop verification remains in terminal UI and application startup.

## Technical Notes

Build on tests included with each implementation issue; do not postpone all testing until this issue. These are JUnit regression/integration tests, not the separate automatic game tester requested by the assignment. The latter is outside S03–S20.

