# Activity 1 - User Stories: Movement

**Feature:** Movement  
**Owner:** Pat Kupkee

These user stories define the player-facing movement requirements for the text-based prison escape RPG. They focus on required behaviour rather than implementation details, so the necessary classes and development tasks can be identified from the stories.

---

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

## INVEST Check

- **Independent:** Each story focuses on a distinct movement behaviour and can be developed and tested as a separate piece of work, subject to dependencies identified during development planning.
- **Negotiable:** The stories specify player-facing behaviour without prescribing particular classes, methods, or implementation techniques.
- **Valuable:** Each story provides a clear benefit to the player and contributes to navigating or progressing through the prison.
- **Estimable:** Each story has a small scope and a one-hour initial estimate.
- **Small:** Each story focuses on one specific movement behaviour rather than the entire movement system.
- **Testable:** Each story has explicit acceptance criteria that can be verified through tests.

## Development Notes

These stories should be used as the basis for later development issues. Technical dependencies such as `Position`, `Maze`, `Player`, game-session state, and NPC selection should be identified when converting the stories into implementation tasks rather than being assumed as completed beforehand.
