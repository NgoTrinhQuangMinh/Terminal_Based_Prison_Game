# Activity 1 - User Stories

This file contains the agreed user stories for the four core features of the text-based prison escape RPG.

Each story includes a priority, an estimated effort and acceptance criteria. The stories describe required behaviour and user value. Detailed classes, methods and development dependencies are handled later in the M2 development issues.

---

# 1. Map / Level

**Owner:** Lia Huang  
**Reviewer:** Pat Kupkee

## MAP-1 - Explore the Prison

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want the prison to have connected areas that I can explore, so that I can move through the level and work towards the exit.

### Acceptance Criteria

- [ ] The prison map contains multiple connected locations.
- [ ] The map can identify which locations are connected.
- [ ] Connected locations can be used by the movement system for valid movement.
- [ ] The map structure can be read without changing the map state.

---

## MAP-2 - Clear Paths and Boundaries

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want to know which areas I can enter and which paths are blocked, so that I can understand where I am able to go.

### Acceptance Criteria

- [ ] Walkable areas and blocked areas can be distinguished.
- [ ] Locations outside the prison map are treated as outside the playable area.
- [ ] The map provides the information needed to check whether a location is blocked.
- [ ] Checking the map does not change the map state.

---

## MAP-3 - Start and Exit Points

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want the prison to have a clear starting point and exit, so that I know where the game begins and where I need to reach to escape.

### Acceptance Criteria

- [ ] The prison map contains one player starting point.
- [ ] The prison map contains one exit point.
- [ ] The game can identify both locations from the map data.
- [ ] Invalid map data with a missing or duplicate start or exit is rejected.

---

## MAP-4 - Configurable Prison Layout

**Priority:** High  
**Estimated time:** 3 hours

### User Story

> As a game designer, I want the prison layout to be stored in a separate resource file, so that I can change the level without changing the main game code.

### Acceptance Criteria

- [ ] The prison layout can be loaded from a project resource file.
- [ ] Changes to the resource file can change the level layout without changing the main game code.
- [ ] The loaded layout uses the same map structure as the rest of the game.
- [ ] Missing or invalid map data is reported clearly.

---

# 2. Movement

**Owner:** Pat Kupkee  
**Reviewer:** Trinh Quang Minh Ngo

## US-MOV-01 - Choose a Movement Direction

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to choose a direction to move in so that I can navigate through the prison.

### Acceptance Criteria

- [ ] The player can choose to move forward.
- [ ] The player can choose to move backward.
- [ ] The player can choose to move left.
- [ ] The player can choose to move right.
- [ ] Each movement choice is interpreted as the intended direction.
- [ ] Invalid movement input does not move the player.
- [ ] Tests verify that the four supported directions are handled correctly.

---

## US-MOV-02 - Move to an Accessible Location

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want a valid movement command to move me to the next accessible location so that I can explore the prison.

### Acceptance Criteria

- [ ] A movement command to an accessible adjacent location moves the player there.
- [ ] The player's current location is updated after successful movement.
- [ ] A single movement command moves the player by one location.
- [ ] The player remains in the same location when the requested destination is not accessible.
- [ ] Tests verify the player's location after successful movement.

---

## US-MOV-03 - Prevent Invalid Movement

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want walls and the prison boundaries to prevent invalid movement so that I cannot leave the playable area or move through inaccessible areas.

### Acceptance Criteria

- [ ] The player cannot move through a wall.
- [ ] The player cannot move outside the boundaries of the prison map.
- [ ] A blocked movement attempt does not change the player's location.
- [ ] The game provides feedback when movement is blocked.
- [ ] Blocked movement does not cause an error or crash.
- [ ] Tests cover both wall and boundary movement.

---

## US-MOV-04 - Progress Through the Prison Exit

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want the prison exit to restrict access until I have obtained the required key so that movement through the prison provides a clear progression goal.

### Acceptance Criteria

- [ ] The player can approach the prison exit through normal movement.
- [ ] Attempting to enter the exit without the required key does not move the player onto the exit.
- [ ] The game tells the player that the exit is locked when the key is missing.
- [ ] The player can enter the exit once the required key has been obtained.
- [ ] Successfully entering the exit marks the game as won.
- [ ] Tests verify both the locked and unlocked exit cases.

---

## US-MOV-05 - Encounter NPCs When Moving

**Priority:** Medium  
**Estimated time:** 1 hour

### User Story

> As a player, I want to know when I have moved onto a location containing an unresolved NPC so that I can decide how to interact with them.

### Acceptance Criteria

- [ ] Moving onto a location containing an unresolved NPC identifies the NPC to the player.
- [ ] The game provides information about the encounter and the available interaction options.
- [ ] Moving onto an NPC location does not automatically start combat.
- [ ] Resolved NPCs do not trigger a new encounter when the player returns to their location.
- [ ] NPC encounter progress is preserved when the player moves away and returns.
- [ ] Tests verify NPC feedback when entering an NPC location.

---

# 3. Enemies / NPCs

**Owner:** Trinh Quang Minh Ngo  
**Reviewer:** Xinran Tian

## ENM-01 - Locate NPCs and Check Encounter Strength

**Priority:** High  
**Estimated time:** 1.5 hours

### User Story

> As a player, I want to locate NPCs and see their combat strength when I encounter them, so that I can decide how to approach each encounter.

### Acceptance Criteria

- [ ] Given an unresolved NPC on another tile, when the map is displayed, then its location is distinguishable from empty floor.
- [ ] Given an unresolved NPC, when the player enters its tile, then feedback displays that NPC's current health and attack and identifies the encounter choices.
- [ ] Entering the tile alone does not damage either participant or award items.

---

## ENM-02 - Attack an NPC

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want to attack an NPC and receive clear combat feedback, so that I can understand the risk and progress of a fight.

### Acceptance Criteria

- [ ] Given a player with attack 3 and health 10 and an NPC with health 6 and attack 2, when the player fights once, then the NPC has health 3 and the player has health 8.
- [ ] A non-final exchange reports damage dealt, remaining NPC health and counterattack damage.
- [ ] When an attack reduces NPC health to zero, then defeat is reported and the NPC does not counterattack.
- [ ] When a counterattack reduces player health to zero, then game over is reported and further game commands do not continue play.

---

## ENM-03 - Solve an NPC Riddle

**Priority:** High  
**Estimated time:** 3 hours

### User Story

> As a player, I want to hear and solve an NPC's riddle, so that I can complete the encounter without taking combat damage.

### Acceptance Criteria

- [ ] Given an unresolved NPC on the player's tile, when the player talks, then that NPC's configured riddle is displayed.
- [ ] Talking leaves both participants' health and the player's inventory unchanged.
- [ ] Given a riddle that has not yet been offered, when an answer command is submitted, then the player is told to talk first and receives no reward.
- [ ] Given an offered riddle with answer `clock`, when the player submits `CLOCK` with surrounding spaces, then the encounter is resolved without player damage.
- [ ] Given an offered riddle, when the player submits an incorrect or empty answer, then no reward is granted and the encounter remains unresolved; an incorrect answer produces retry feedback.
- [ ] After an incorrect answer, the player can submit the correct answer without hearing the riddle again.

---

## ENM-04 - Configure NPC Combat Stats

**Priority:** Medium  
**Estimated time:** 1.5 hours

### User Story

> As a game designer, I want to adjust each NPC's health and attack without editing game code, so that I can balance encounter difficulty.

### Acceptance Criteria

- [ ] Given one NPC's configured health is changed from 6 to 8, when a new game starts, then its encounter displays health 8 while other NPCs retain their own values.
- [ ] Given its attack is 3 and it survives the player's attack, when it counterattacks, then a player with health 10 is reduced to 7.
- [ ] Given missing, non-integer or non-positive combat stats, when the configuration loads, then it is rejected.

---

## ENM-05 - Configure NPC Riddles

**Priority:** Medium  
**Estimated time:** 1 hour

### User Story

> As a game designer, I want to change each NPC's riddle and accepted answer without editing game code, so that encounters offer different puzzles.

### Acceptance Criteria

- [ ] Given an NPC's riddle and answer are changed, when a new game starts and the player talks to it, then the revised question is displayed and the revised answer resolves its encounter.
- [ ] Given two NPCs with different answers, when the player answers one NPC, then only that NPC's answer is used and the other encounter remains unchanged.
- [ ] Given a missing or blank riddle or answer, when configuration loads, then it is rejected.

---

## ENM-06 - Preserve Encounter Progress

**Priority:** Medium  
**Estimated time:** 1.5 hours

### User Story

> As a player, I want to leave an unresolved NPC and return later, so that I can postpone an encounter without losing its progress.

### Acceptance Criteria

- [ ] Given an unresolved encounter and an adjacent walkable tile, when the player moves away, then movement succeeds without an automatic attack.
- [ ] Given an NPC damaged by a previous attack, when the player leaves and returns, then the NPC retains its remaining health.
- [ ] Given an offered riddle, when the player leaves and returns during the same game, then the player can still answer that NPC's riddle.

---

## ENM-07 - Receive NPC Rewards

**Priority:** High  
**Estimated time:** 1.5 hours

### User Story

> As a player, I want to receive an NPC's rewards automatically when I complete its encounter, so that my success gives me useful items without an extra collection step.

### Acceptance Criteria

- [ ] Given an NPC configured to drop a herb and a key, when it is defeated, then both items enter the player's inventory and feedback lists them.
- [ ] In a fresh equivalent encounter, solving its riddle grants the same items.
- [ ] After resolution, further fight, talk or answer commands on that tile do not grant more rewards.
- [ ] After the player leaves a resolved NPC's tile, that NPC is no longer shown as an active encounter.

---

## ENM-08 - Target the Current NPC

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want NPC interactions to apply only to the NPC on my current tile, so that commands have predictable targets.

### Acceptance Criteria

- [ ] Given no unresolved NPC on the player's tile, when the player fights, talks or submits an answer, then feedback reports that no NPC is available for that action.
- [ ] These commands leave the player's health and inventory unchanged and do not alter distant NPCs.
- [ ] Given the player heard a riddle and then moved away, when its correct answer is submitted on an empty tile, then no reward is granted.

---

## ENM-09 - Enter and Edit Riddle Answers

**Priority:** Medium  
**Estimated time:** 2 hours

### User Story

> As a player, I want to enter, correct or cancel a riddle answer without triggering other actions, so that I can respond without accidental movement or combat.

### Acceptance Criteria

- [ ] Given answer entry is active, when the player types letters such as `w`, `a`, `s`, `d` or `f`, then they are added to the answer instead of triggering game actions.
- [ ] The player can delete a mistyped character and submit the corrected non-blank answer.
- [ ] Cancelling clears the input and returns to normal controls without resolving the NPC or changing player health or inventory.
- [ ] An incorrect answer keeps answer entry active; a correct answer exits it.

---

## ENM-10 - Configure NPC Rewards

**Priority:** Medium  
**Estimated time:** 1.5 hours

### User Story

> As a game designer, I want to choose each NPC's reward items without editing game code, so that I can control the resources available through encounters.

### Acceptance Criteria

- [ ] Given an NPC's rewards are changed to two herbs and a weapon, when a new game starts and that encounter is resolved, then the player receives exactly those items.
- [ ] Changing one NPC's rewards does not change another NPC's configured rewards.
- [ ] Given a missing or empty reward list or an unsupported item name, when configuration loads, then it is rejected.

---

# 4. Inventory

**Owner:** Xinran Tian  
**Reviewer:** Lia Huang

## US-INV-01 - Receive Items from Encounters

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to receive items after resolving an encounter with an NPC so that I can collect the resources I need to escape.

### Acceptance Criteria

- [ ] When an NPC encounter is resolved by combat or by answering its riddle, the NPC's items are added to the player's inventory.
- [ ] The game tells the player which items were received.
- [ ] If an NPC gives the same item more than once, such as two herbs, the inventory contains the matching number of that item.
- [ ] Items from the same NPC can only be received once.
- [ ] Tests verify that items are added correctly and are not awarded twice.

---

## US-INV-02 - View Inventory

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to view the items I am carrying so that I can decide what to use next.

### Acceptance Criteria

- [ ] The player can view their inventory with an inventory command and a short alias such as `i`.
- [ ] All held items are listed.
- [ ] An empty inventory is clearly shown as empty.
- [ ] Viewing the inventory does not change the game state.
- [ ] Tests verify the output for both an empty and a non-empty inventory.

---

## US-INV-03 - Use a Healing Item

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to use a healing herb to restore my health so that I can survive further encounters.

### Acceptance Criteria

- [ ] Using a herb restores a fixed amount of health.
- [ ] Health never goes above the player's maximum health.
- [ ] One herb is removed from the inventory after it is used.
- [ ] If the player is already at full health, the herb is kept and the game explains why.
- [ ] Tests verify healing, the maximum health limit and the full-health case.

---

## US-INV-04 - Equip a Weapon

**Priority:** Medium  
**Estimated time:** 1 hour

### User Story

> As a player, I want to equip a weapon I have collected so that I can deal more damage to guards.

### Acceptance Criteria

- [ ] Equipping a held weapon increases the player's attack.
- [ ] Equipping the same weapon again does not increase attack further.
- [ ] The player cannot equip a weapon they do not have.
- [ ] The player's updated attack is shown after equipping.
- [ ] Tests verify the attack bonus and that it does not stack.

---

## US-INV-05 - Handle Invalid Item Use

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want clear feedback when I try to use an item I don't have or type an item name incorrectly so that I understand what went wrong.

### Acceptance Criteria

- [ ] Trying to use an item that is not in the inventory shows a message and does not change the game state.
- [ ] Using an unknown item name shows which items can be used.
- [ ] Using the `use` command without an item name shows how to use it.
- [ ] None of these cases cause an error or crash.
- [ ] Tests cover each invalid case.

---

## US-INV-06 - See Key Items in the Status Display

**Priority:** Low  
**Estimated time:** 30 minutes

### User Story

> As a player, I want to see whether I am holding the exit key in the status display so that I know when I can head to the exit.

### Acceptance Criteria

- [ ] The status display shows whether the player has the key.
- [ ] The status updates immediately after the key is received.
- [ ] Tests verify the status before and after receiving the key.

---

# INVEST Check

The stories in this file are written to follow the INVEST principles:

- **Independent:** Each story focuses on one main behaviour. Cross-feature dependencies are handled during M2 planning.
- **Negotiable:** The stories describe user needs and expected behaviour without fixing the Java class or method design.
- **Valuable:** Each story supports exploration, movement, encounters, resource use or progression through the prison.
- **Estimable:** Every story has an initial time estimate.
- **Small:** The stories are split into focused pieces of behaviour that can be planned and tested separately.
- **Testable:** Each story has clear acceptance criteria.

## Development Notes

Technical dependencies such as `Position`, `Maze`, `Player`, NPC state, inventory state, command handling and game-session state are handled when these stories are converted into M2 development issues.

The key check at the prison exit belongs to Movement. Key storage and display belong to Inventory. NPC combat, riddles and rewards should be coordinated between the Enemies and Inventory work to avoid duplicate logic.
