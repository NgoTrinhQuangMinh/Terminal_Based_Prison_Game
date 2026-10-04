## ENM-01
As a player, I want to locate NPCs and see their combat strength when I encounter them, so that I can decide how to approach each encounter.

**Acceptance Criteria:**

- Given an unresolved NPC on another tile, when the map is displayed, then its location is distinguishable from empty floor.
- Given an unresolved NPC, when the player enters its tile, then feedback displays that NPC's current health and attack and identifies the encounter choices.
- Entering the tile alone does not damage either participant or award items.

**Priority:** High | **Estimated effort:** 1.5 hours

## ENM-02

As a player, I want to attack an NPC and receive clear combat feedback, so that I can understand the risk and progress of a fight.

**Acceptance Criteria:**

- Given a player with attack 3 and health 10 and an NPC with health 6 and attack 2, when the player fights once, then the NPC has health 3 and the player has health 8.
- A non-final exchange reports damage dealt, remaining NPC health and counterattack damage.
- When an attack reduces NPC health to zero, then defeat is reported and the NPC does not counterattack.
- When a counterattack reduces player health to zero, then game over is reported and further game commands do not continue play.

**Priority:** High | **Estimated effort:** 2 hours

## ENM-03

As a player, I want to hear and solve an NPC's riddle, so that I can complete the encounter without taking combat damage.

**Acceptance Criteria:**

- Given an unresolved NPC on the player's tile, when the player talks, then that NPC's configured riddle is displayed.
- Talking leaves both participants' health and the player's inventory unchanged.
- Given a riddle that has not yet been offered, when an answer command is submitted, then the player is told to talk first and receives no reward.
- Given an offered riddle with answer `clock`, when the player submits `CLOCK` with surrounding spaces, then the encounter is resolved without player damage.
- Given an offered riddle, when the player submits an incorrect or empty answer, then no reward is granted and the encounter remains unresolved; an incorrect answer produces retry feedback.
- After an incorrect answer, the player can submit the correct answer without hearing the riddle again.

**Priority:** High | **Estimated effort:** 3 hours

## ENM-04 

As a game designer, I want to adjust each NPC's health and attack without editing game code, so that I can balance encounter difficulty.

**Acceptance Criteria:**

- Given one NPC's configured health is changed from 6 to 8, when a new game starts, then its encounter displays health 8 while other NPCs retain their own values.
- Given its attack is 3 and it survives the player's attack, when it counterattacks, then a player with health 10 is reduced to 7.
- Given missing, non-integer or non-positive combat stats, when the configuration loads, then it is rejected rather than used for play.

**Priority:** Medium | **Estimated effort:** 1.5 hours

## ENM-05 

As a game designer, I want to change each NPC's riddle and accepted answer without editing game code, so that encounters offer different puzzles.

**Acceptance Criteria:**

- Given an NPC's riddle and answer are changed, when a new game starts and the player talks to it, then the revised question is displayed and the revised answer resolves its encounter.
- Given two NPCs with different answers, when the player answers one NPC, then only that NPC's answer is used and the other encounter remains unchanged.
- Given a missing or blank riddle or answer, when configuration loads, then it is rejected.

**Priority:** Medium | **Estimated effort:** 1 hour

## ENM-06

As a player, I want to leave an unresolved NPC and return later, so that I can postpone an encounter without losing its progress.

**Acceptance Criteria:**

- Given an unresolved encounter and an adjacent walkable tile, when the player moves away, then movement succeeds without an automatic attack.
- Given an NPC damaged by a previous attack, when the player leaves and returns, then the NPC retains its remaining health.
- Given an offered riddle, when the player leaves and returns during the same game, then the player can still answer that NPC's riddle.

**Priority:** Medium | **Estimated effort:** 1.5 hours

## ENM-07 

As a player, I want to receive an NPC's rewards automatically when I complete its encounter, so that my success gives me useful items without an extra collection step.

**Acceptance Criteria:**

- Given an NPC configured to drop a herb and a key, when it is defeated, then both items enter the player's inventory and feedback lists them.
- In a fresh equivalent encounter, solving its riddle grants the same items.
- After resolution, further fight, talk or answer commands on that tile do not grant more rewards.
- After the player leaves a resolved NPC's tile, that NPC is no longer shown as an active encounter.

**Priority:** High | **Estimated effort:** 1.5 hours

## ENM-08

As a player, I want NPC interactions to apply only to the NPC on my current tile, so that commands have predictable targets.

**Acceptance Criteria:**

- Given no unresolved NPC on the player's tile, when the player fights, talks or submits an answer, then feedback reports that no NPC is available for that action.
- These commands leave the player's health and inventory unchanged and do not alter distant NPCs.
- Given the player heard a riddle and then moved away, when its correct answer is submitted on an empty tile, then no reward is granted.

**Priority:** High | **Estimated effort:** 1 hour

## ENM-09

As a player, I want to enter, correct or cancel a riddle answer without triggering other actions, so that I can respond without accidental movement or combat.

**Acceptance Criteria:**

- Given answer entry is active, when the player types letters such as `w`, `a`, `s`, `d` or `f`, then they are added to the answer rather than triggering game actions.
- The player can delete a mistyped character and submit the corrected non-blank answer.
- Cancelling clears the input and returns to normal controls without resolving the NPC or changing player health or inventory.
- An incorrect answer keeps answer entry active; a correct answer exits it.

**Priority:** Medium | **Estimated effort:** 2 hours

## ENM-10 

As a game designer, I want to choose each NPC's reward items without editing game code, so that I can control the resources available through encounters.

**Acceptance Criteria:**

- Given an NPC's rewards are changed to two herbs and a weapon, when a new game starts and that encounter is resolved, then the player receives exactly those items.
- Changing one NPC's rewards does not change another NPC's configured rewards.
- Given a missing or empty reward list or an unsupported item name, when configuration loads, then it is rejected rather than silently awarding incorrect items.

**Priority:** Medium | **Estimated effort:** 1.5 hours
