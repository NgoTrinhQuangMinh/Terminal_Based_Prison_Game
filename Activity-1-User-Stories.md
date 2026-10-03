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

# Additional User Stories

The four additional features are:

1. **Continuous UI**
2. **Item Using - Weapon and Herb**
3. **Difficulty Selection**
4. **Answering Riddles**

---

# 1. Continuous UI

The continuous UI provides the player with an ongoing view of the current game state while they play. Rather than requiring the player to repeatedly request information, the interface keeps important information such as the map, player position, NPCs, exit and player status visible and up to date.

## UI-01 - Continuously Display the Prison Map

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want the prison map to remain visible while I play, so that I can understand my surroundings and plan where to move next.

### Acceptance Criteria

- [ ] The prison map is displayed during active gameplay.
- [ ] The displayed map represents the current prison layout.
- [ ] The player can identify the playable and blocked areas from the display.
- [ ] The map remains available without requiring a separate map command after every action.
- [ ] Displaying the map does not change the underlying game state.

### INVEST Check

- **Independent:** Focuses on continuous display of the map.
- **Negotiable:** Describes the required behaviour without prescribing the UI implementation.
- **Valuable:** Helps the player navigate the prison.
- **Estimable:** Limited to two hours.
- **Small:** Focuses only on persistent map visibility.
- **Testable:** The map can be checked during active gameplay.

---

## UI-02 - Display the Player's Current Position

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want my current position to be shown on the map, so that I know where I am in the prison.

### Acceptance Criteria

- [ ] The player's current location is represented on the displayed map.
- [ ] The player marker corresponds to the player's actual location.
- [ ] After successful movement, the player marker moves to the new location.
- [ ] After blocked movement, the player marker remains at the previous location.
- [ ] The displayed player position does not contradict the underlying player state.

### INVEST Check

- **Independent:** Focuses on representing the player's location.
- **Negotiable:** Does not prescribe the marker or rendering implementation.
- **Valuable:** Gives the player spatial awareness.
- **Estimable:** Limited to one hour.
- **Small:** Covers one displayed game-state value.
- **Testable:** The marker can be compared with the player's actual position.

---

## UI-03 - Display NPCs and the Exit

**Priority:** High  
**Estimated time:** 1.5 hours

### User Story

> As a player, I want important locations and encounters to be visible on the map, so that I can understand where NPCs and the exit are in relation to me.

### Acceptance Criteria

- [ ] Unresolved NPC locations are distinguishable from ordinary walkable locations.
- [ ] The prison exit is distinguishable from ordinary walkable locations.
- [ ] NPC and exit markers correspond to their locations in the underlying map.
- [ ] Resolved NPCs are no longer presented as active encounters.
- [ ] Displaying these markers does not change NPC or player state.

### INVEST Check

- **Independent:** Focuses on displaying NPC and exit locations.
- **Negotiable:** Does not prescribe marker symbols or UI layout.
- **Valuable:** Helps the player understand important points of interest.
- **Estimable:** 1.5-hour focused task.
- **Small:** Covers two related map elements.
- **Testable:** Marker presence and locations can be verified against game state.

---

## UI-04 - Display Current Player Status

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want my important status information to remain visible, so that I can make informed decisions about movement, item use and encounters.

### Acceptance Criteria

- [ ] The UI displays the player's current health.
- [ ] The UI displays the player's current attack value.
- [ ] The UI indicates whether the player has the required key.
- [ ] Displayed values match the underlying player state.
- [ ] Changes to health, attack or key possession are reflected in the UI.

### INVEST Check

- **Independent:** Focuses on displaying player status.
- **Negotiable:** Does not prescribe the exact layout or wording.
- **Valuable:** Provides information needed for gameplay decisions.
- **Estimable:** Limited to one hour.
- **Small:** Focuses on a defined set of status information.
- **Testable:** Displayed values can be compared with the model state.

---

## UI-05 - Refresh the UI After Player Actions

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want the displayed game state to update after my actions, so that the information on screen reflects what has actually happened.

### Acceptance Criteria

- [ ] Successful movement is reflected in the displayed player position.
- [ ] Changes to health are reflected after damage or healing.
- [ ] Changes to attack are reflected after equipping a weapon.
- [ ] Changes to key possession are reflected after receiving the key.
- [ ] The UI does not continue displaying stale game-state information after an action.

### INVEST Check

- **Independent:** Focuses on keeping the displayed state synchronised with gameplay.
- **Negotiable:** Does not prescribe when or how the UI refresh occurs internally.
- **Valuable:** Prevents confusion caused by outdated information.
- **Estimable:** Limited to two hours.
- **Small:** Focuses on updating existing UI information.
- **Testable:** Each state change can be followed by a display check.

---

# 2. Item Using - Weapon and Herb

This feature extends the basic inventory system by allowing collected items to have active gameplay effects. Herbs can restore health and weapons can increase the player's attack.

## ITEM-USE-01 - Use an Item from the Inventory

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to use an item that I am carrying, so that collected items can provide useful effects during the game.

### Acceptance Criteria

- [ ] The player can select an item from their inventory to use.
- [ ] The game identifies whether the selected item can be used.
- [ ] A valid item use applies that item's intended effect.
- [ ] The inventory is updated appropriately after a successful item use.
- [ ] Using an item provides feedback to the player.

### INVEST Check

- **Independent:** Focuses on the general item-use interaction.
- **Negotiable:** Does not prescribe the command or implementation.
- **Valuable:** Makes collected items useful during gameplay.
- **Estimable:** Limited to one hour.
- **Small:** Establishes the common item-use behaviour.
- **Testable:** Item selection, effect and inventory changes can be verified.

---

## ITEM-USE-02 - Use a Herb to Restore Health

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to use a healing herb to restore my health, so that I can survive further encounters.

### Acceptance Criteria

- [ ] Using a herb increases the player's health by the configured healing amount.
- [ ] The player's health never exceeds the maximum health.
- [ ] One herb is removed from the inventory after successful use.
- [ ] The player receives feedback showing that the herb was used.
- [ ] The player's attack and other inventory items remain unchanged.

### INVEST Check

- **Independent:** Can be tested with a player carrying a herb and below maximum health.
- **Negotiable:** Describes the expected effect without prescribing implementation.
- **Valuable:** Allows the player to recover from combat damage.
- **Estimable:** Limited to one hour.
- **Small:** Focuses on one item and one effect.
- **Testable:** Health and inventory changes can be asserted.

---

## ITEM-USE-03 - Prevent Unnecessary Herb Use at Full Health

**Priority:** Medium  
**Estimated time:** 30 minutes

### User Story

> As a player, I want the game to prevent me from wasting a healing herb when I am already at full health, so that I can save it for a later encounter.

### Acceptance Criteria

- [ ] If the player's health is already at maximum, using a herb does not increase health.
- [ ] The herb remains in the player's inventory.
- [ ] The game explains that the player is already at full health.
- [ ] The player's attack and other inventory items remain unchanged.
- [ ] The action does not cause an error or crash.

### INVEST Check

- **Independent:** Focuses on the full-health case for one item.
- **Negotiable:** Does not prescribe how the condition is checked.
- **Valuable:** Prevents accidental loss of a useful resource.
- **Estimable:** Limited to 30 minutes.
- **Small:** Covers one edge case.
- **Testable:** Health and inventory can be checked before and after the action.

---

## ITEM-USE-04 - Equip a Weapon

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to equip a weapon I have collected, so that I can increase my attack strength for future encounters.

### Acceptance Criteria

- [ ] A weapon held by the player can be equipped.
- [ ] Equipping the weapon increases the player's attack by the configured amount.
- [ ] The updated attack value is reflected in the player's state.
- [ ] Equipping the same weapon again does not repeatedly stack the same bonus.
- [ ] The game provides feedback after the weapon is equipped.

### INVEST Check

- **Independent:** Can be tested with a player who already possesses a weapon.
- **Negotiable:** Does not prescribe how equipment state is stored.
- **Valuable:** Gives the player a meaningful combat benefit.
- **Estimable:** Limited to one hour.
- **Small:** Focuses on one weapon effect.
- **Testable:** Attack before and after equipping can be compared.

---

## ITEM-USE-05 - Handle Invalid Item Use

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want clear feedback when I try to use an item that I do not have or cannot use, so that I understand what went wrong.

### Acceptance Criteria

- [ ] Trying to use an item that is not in the inventory does not change the game state.
- [ ] Using an unknown item name produces clear feedback.
- [ ] Attempting to use an unavailable item does not reduce player health.
- [ ] Attempting to use an unavailable item does not remove another inventory item.
- [ ] Invalid item use does not cause an error or crash.

### INVEST Check

- **Independent:** Focuses on invalid item-use cases.
- **Negotiable:** Does not prescribe the exact error message.
- **Valuable:** Prevents confusing or unintended item changes.
- **Estimable:** Limited to one hour.
- **Small:** Covers invalid input rather than the valid item effects.
- **Testable:** Invalid inputs and unchanged state can be verified.

---

# 3. Difficulty Selection

Difficulty selection allows the player to choose the type of prison level they want to play. Rather than manually changing configuration values, the game provides a collection of maps associated with different difficulty choices.

## DIFF-01 - Choose a Difficulty

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to choose a difficulty before starting the game, so that I can select the type of challenge I want to play.

### Acceptance Criteria

- [ ] The player is presented with the available difficulty choices before the game begins.
- [ ] The player can select one of the available difficulties.
- [ ] The selected difficulty is accepted as the player's game choice.
- [ ] The game does not start with an unrecognised difficulty selection.
- [ ] The selected difficulty is used when creating the game level.

### INVEST Check

- **Independent:** Focuses on making and recording the initial difficulty choice.
- **Negotiable:** Does not prescribe how the choice is presented or stored.
- **Valuable:** Gives the player control over the type of game they play.
- **Estimable:** Limited to one hour.
- **Small:** Covers the initial difficulty-selection interaction.
- **Testable:** Available choices and the selected value can be verified.

---

## DIFF-02 - Display Available Difficulty Options

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to see the available difficulty options before choosing one, so that I can make an informed selection.

### Acceptance Criteria

- [ ] The available difficulty choices are displayed before a game is started.
- [ ] Each choice has a clear name.
- [ ] The player can distinguish one difficulty option from another.
- [ ] The displayed options correspond to difficulties supported by the game.
- [ ] Displaying the choices does not start a game or alter player state.

### INVEST Check

- **Independent:** Focuses on presenting the available choices.
- **Negotiable:** Does not prescribe the exact wording or UI.
- **Valuable:** Allows the player to understand the available options.
- **Estimable:** Limited to one hour.
- **Small:** Covers presentation rather than level loading.
- **Testable:** The displayed choices can be checked against supported difficulties.

---

## DIFF-03 - Load the Map for the Selected Difficulty

**Priority:** High  
**Estimated time:** 2 hours

### User Story

> As a player, I want my selected difficulty to load its corresponding prison map, so that my difficulty choice changes the level I play.

### Acceptance Criteria

- [ ] Each supported difficulty is associated with a configured prison map.
- [ ] Selecting a difficulty loads the corresponding map.
- [ ] The loaded map can be used by the normal movement system.
- [ ] The loaded map contains the required starting location.
- [ ] The loaded map contains a valid exit.

### INVEST Check

- **Independent:** Focuses on the relationship between a difficulty choice and its map.
- **Negotiable:** Does not prescribe how maps are stored or loaded.
- **Valuable:** Makes difficulty selection meaningful.
- **Estimable:** Two-hour focused task.
- **Small:** Covers map selection rather than the complete game.
- **Testable:** The selected difficulty and resulting map can be compared.

---

## DIFF-04 - Start a New Game Using the Selected Difficulty

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want the selected difficulty to remain associated with my game session, so that the level does not unexpectedly change while I am playing.

### Acceptance Criteria

- [ ] The game starts using the map selected by the player.
- [ ] The player starts at the selected map's starting location.
- [ ] Movement uses the selected map throughout the game session.
- [ ] Starting the game does not replace the selected map with another difficulty's map.
- [ ] The selected difficulty remains identifiable during the current game session.

### INVEST Check

- **Independent:** Focuses on maintaining the selected level after game creation.
- **Negotiable:** Does not prescribe how the difficulty is stored.
- **Valuable:** Ensures the player's initial choice has consistent effects.
- **Estimable:** Limited to one hour.
- **Small:** Focuses on one game-session behaviour.
- **Testable:** The selected map and starting state can be checked.

---

## DIFF-05 - Handle an Invalid Difficulty Selection

**Priority:** Medium  
**Estimated time:** 1 hour

### User Story

> As a player, I want invalid difficulty choices to be handled clearly, so that I can correct my selection without starting an unintended game.

### Acceptance Criteria

- [ ] An unrecognised difficulty choice is not treated as a valid difficulty.
- [ ] The game provides feedback explaining that the selection is invalid.
- [ ] An invalid selection does not load an unintended map.
- [ ] An invalid selection does not begin a normal game session.
- [ ] The player can make another valid difficulty selection.

### INVEST Check

- **Independent:** Focuses on invalid difficulty input.
- **Negotiable:** Does not prescribe the exact validation mechanism.
- **Valuable:** Prevents the player from accidentally starting the wrong level.
- **Estimable:** Limited to one hour.
- **Small:** Covers one input-validation case.
- **Testable:** Invalid and subsequent valid selections can be verified.

---

# 4. Answering Riddles

Answering riddles provides a non-combat route for resolving NPC encounters. The player can hear a configured riddle, submit an answer and retry after an incorrect response.

## RIDDLE-01 - Hear an NPC Riddle

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to ask an NPC for its riddle, so that I can understand the challenge before attempting to answer it.

### Acceptance Criteria

- [ ] The player can request a riddle from an unresolved NPC on their current location.
- [ ] The NPC's configured riddle is displayed to the player.
- [ ] Talking to the NPC does not reduce the player's health.
- [ ] Talking to the NPC does not remove inventory items.
- [ ] The NPC remains unresolved after the riddle is displayed.

### INVEST Check

- **Independent:** Focuses only on presenting the riddle.
- **Negotiable:** Does not prescribe how the riddle is stored or displayed.
- **Valuable:** Gives the player the information needed to solve the encounter.
- **Estimable:** Limited to one hour.
- **Small:** Covers one interaction.
- **Testable:** Riddle output and unchanged state can be verified.

---

## RIDDLE-02 - Submit an Answer

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to submit an answer to an NPC's riddle, so that I can attempt to resolve the encounter.

### Acceptance Criteria

- [ ] The player can submit an answer after a riddle has been offered.
- [ ] The submitted answer is compared with the NPC's configured answer.
- [ ] A correct answer resolves the riddle encounter.
- [ ] A correct answer does not cause combat damage.
- [ ] The player receives feedback about the result.

### INVEST Check

- **Independent:** Focuses on submitting and checking an answer.
- **Negotiable:** Does not prescribe the answer-matching implementation.
- **Valuable:** Provides a direct way to progress through an NPC encounter.
- **Estimable:** Limited to one hour.
- **Small:** Covers the answer-submission behaviour.
- **Testable:** Correct and incorrect answer outcomes can be verified.

---

## RIDDLE-03 - Accept Normal Variations in Answers

**Priority:** Medium  
**Estimated time:** 1 hour

### User Story

> As a player, I want the game to recognise a correct answer even if I use different capitalisation or surrounding spaces, so that formatting does not prevent a correct response.

### Acceptance Criteria

- [ ] A correct answer written in lowercase is accepted.
- [ ] A correct answer written in uppercase is accepted.
- [ ] A correct answer with surrounding whitespace is accepted.
- [ ] These formatting differences do not change the meaning of the answer.
- [ ] An answer that differs from the configured answer is not incorrectly accepted.

### INVEST Check

- **Independent:** Focuses on normalising answer input.
- **Negotiable:** Does not prescribe the exact string-processing implementation.
- **Valuable:** Prevents trivial formatting differences from causing incorrect results.
- **Estimable:** Limited to one hour.
- **Small:** Covers input normalisation only.
- **Testable:** Multiple answer formats can be tested against the same riddle.

---

## RIDDLE-04 - Retry an Incorrect Answer

**Priority:** High  
**Estimated time:** 1 hour

### User Story

> As a player, I want to retry a riddle after an incorrect answer, so that I have another opportunity to solve the encounter.

### Acceptance Criteria

- [ ] An incorrect answer does not resolve the NPC.
- [ ] An incorrect answer does not award the NPC's rewards.
- [ ] The player receives feedback indicating that the answer was incorrect.
- [ ] The player can submit another answer without needing to restart the game.
- [ ] An incorrect answer does not reduce the player's health.

### INVEST Check

- **Independent:** Focuses on the retry behaviour after one incorrect answer.
- **Negotiable:** Does not prescribe how retries are stored or presented.
- **Valuable:** Allows the player to continue an encounter without unnecessary combat or game restart.
- **Estimable:** Limited to one hour.
- **Small:** Covers one unsuccessful attempt and its continuation.
- **Testable:** NPC state, health, inventory and subsequent answer submission can be checked.

---

## RIDDLE-05 - Resolve an NPC by Answering Correctly

**Priority:** High  
**Estimated time:** 1.5 hours

### User Story

> As a player, I want a correct riddle answer to complete the NPC encounter, so that I can progress without defeating the NPC through combat.

### Acceptance Criteria

- [ ] A correct answer resolves the NPC encounter.
- [ ] The player does not take combat damage from successfully solving the riddle.
- [ ] The NPC is no longer treated as an unresolved encounter.
- [ ] The configured NPC reward is awarded after successful resolution.
- [ ] Returning to the resolved NPC does not restart the encounter or award the reward again.

### INVEST Check

- **Independent:** Focuses on the successful completion of the riddle route.
- **Negotiable:** Describes the outcome without prescribing implementation.
- **Valuable:** Provides a meaningful non-combat progression path.
- **Estimable:** 1.5-hour focused task.
- **Small:** Covers the successful end of one riddle encounter.
- **Testable:** Resolution, health, rewards and repeat-interaction behaviour can be verified.

---