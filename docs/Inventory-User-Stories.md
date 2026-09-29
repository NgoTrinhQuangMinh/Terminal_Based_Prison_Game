# Activity 1 - User Stories: Inventory

**Feature:** Inventory
**Owner:** Xinran Tian

These user stories describe how the player collects, views and uses items in the prison escape game. They focus on player-facing behaviour, so the classes and tasks needed can be worked out when turning them into development issues.

---

## US-INV-01 - Receive Items from Encounters

**Priority:** High
**Estimated time:** 1 hour

### User Story

> As a player, I want to receive items after resolving an encounter with an NPC so that I can collect the resources I need to escape.

### Acceptance Criteria

- [ ] When an NPC encounter is resolved (by combat or by answering its riddle), the NPC's items are added to the player's inventory.
- [ ] The game tells the player which items were received.
- [ ] If an NPC gives the same item more than once (e.g. two herbs), the inventory contains the matching number of that item.
- [ ] Items from the same NPC can only be received once.
- [ ] Tests verify that items are added correctly and are not awarded twice.

---

## US-INV-02 - View Inventory

**Priority:** High
**Estimated time:** 1 hour

### User Story

> As a player, I want to view the items I am carrying so that I can decide what to use next.

### Acceptance Criteria

- [ ] The player can view their inventory with an inventory command (and a short alias such as `i`).
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
- [ ] Using the use command without an item name shows how to use it.
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

## INVEST Check

- **Independent:** Each story covers one inventory behaviour and can be developed and tested separately, apart from dependencies identified during planning.
- **Negotiable:** The stories describe what the player needs, not which classes or methods should be used.
- **Valuable:** Each story helps the player manage resources and progress towards escaping.
- **Estimable:** Each story is small enough to estimate, at 30 minutes to 1 hour.
- **Small:** Each story focuses on a single action, such as viewing, using or equipping an item.
- **Testable:** Every story has acceptance criteria that can be checked with tests.

## Development Notes

Checking the key when entering the exit is covered by Movement (US-MOV-04), so these stories only cover holding and displaying the key. Weapon effects in combat relate to the Enemies stories and should be coordinated with that owner when creating development issues.
