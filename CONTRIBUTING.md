# Project Development Guide

This document explains how team members should work on the project.

The goal is to keep development consistent, traceable, and easy for every team member to understand.

---

# 2. Responsibility of Each Package

## `engine`

Contains the central game logic.

### `GameEngine`

Controls what the game does.

Examples:

- moving the player,
- attacking an enemy,
- picking up an item,
- processing commands,
- checking win/loss conditions.

`GameEngine` should coordinate other classes rather than contain all game data itself.

### `GameState`

Stores the current state of the game.

Examples:

- current player,
- current map,
- player location,
- enemy states,
- item states,
- game-over state.

Conceptually:

```text
Command
   ↓
GameEngine
   ↓
changes
   ↓
GameState
```

---

## `model`

Contains classes representing things that exist in the game.

Examples:

```text
Player
Room
GameMap
Enemy
NPC
Item
Inventory
```

These classes should mainly represent data and behaviour related to the object they model.

---

## `command`

Contains user actions.

Examples:

```text
move
attack
pickup
use
talk
inventory
quit
```

Keeping commands separate prevents `Main.java` or `GameEngine.java` from becoming one very large class full of `if` statements.

---

## `config`

Contains configuration-loading logic.

For example:

```text
game.json
      ↓
GameConfigLoader
      ↓
GameMap / Player / Items / Enemies
```

Game content should be configurable where practical rather than hard-coded directly into Java.

---

## `ui`

Handles interaction with the human player.

For example:

```text
InputHandler
```

reads terminal input.

```text
TerminalRenderer
```

prints rooms, maps, messages, combat results, and inventory information.

The UI should not contain important game logic.

---

## `tester`

Contains the automatic game tester required by the project.

The tester should interact with the game through the same game logic used by a human player.

For example:

```text
Human Input
     ↓
 GameEngine
     ↑
Automatic Tester
```

The tester should not duplicate the game logic.

---

## `resources`

Contains configuration and data files.

Example:

```text
src/main/resources/game.json
```

Possible configurable content includes:

- rooms,
- room connections,
- enemies,
- NPCs,
- items,
- dialogue,
- starting position,
- win conditions.

---

## `test`

Contains JUnit tests.

Test packages should roughly match the production-code packages.

Example:

```text
src/main/java/engine/GameEngine.java
src/test/java/engine/GameEngineTest.java
```

---

# 3. General Development Rule

Do not make normal feature changes directly on `main`.

The standard workflow is:

```text
Idea / Requirement
       ↓
Issue
       ↓
Branch
       ↓
Commits
       ↓
Merge Request
       ↓
Review
       ↓
Verification
       ↓
CI/CD passes
       ↓
Merge into main
       ↓
Close Issue
```

`main` should always remain in a compilable and runnable state.

---

# 4. Work Items and Issues

GitLab Work Items are used to track project work.

For this project, most development work should be represented by an **Issue**.

Create an issue for work such as:

- a feature,
- a bug,
- a test task,
- refactoring,
- CI/CD work,
- Docker work,
- configuration work,
- project infrastructure.

Do not create separate issues for extremely small changes that are naturally part of an existing issue.

---

# 5. Raising a New Idea

If you have an idea that has not yet been agreed on by the team, create an issue first.

Use a title such as:

```text
[Idea] Add locked doors and keys
```

Recommended format:

```markdown
## Idea

Describe the proposed feature.

## Why?

Explain why it may improve the game or project.

## Possible Approach

Briefly describe a possible implementation if known.

## Impact

Which existing parts of the project may be affected?

## Questions

List anything that should be discussed by the team.
```

Apply:

```text
type::idea
status::backlog
```

The team should discuss the idea in the issue comments.

If accepted, update the issue into a normal feature issue and add acceptance criteria.

If rejected or postponed, explain why in a comment.

Important decisions should not exist only in private messages.

---

# 6. Raising a Development Issue

Every development issue should explain exactly what needs to be completed.

Recommended template:

```markdown
## Summary

What needs to be implemented or fixed?

## Motivation

Why is this work required?

## Acceptance Criteria

- [ ] Requirement 1
- [ ] Requirement 2
- [ ] Requirement 3
- [ ] Relevant tests are added or updated
- [ ] Existing tests still pass

## Dependencies

Depends on #...

Blocks #...

## Technical Notes

Optional implementation notes.

## Verifier

@username
```

Acceptance criteria should be objectively testable.

Bad:

```text
Inventory works correctly.
```

Better:

```text
- Player can pick up an item in the current room.
- Picked-up item is removed from the room.
- Picked-up item appears in the inventory.
- Trying to pick up a missing item does not crash the game.
```

---

# 7. Issue Roles

Each issue should have an **implementer** and a **verifier**.

For larger changes, there should also be a separate **reviewer**.

## Implementer

Responsible for:

- implementing the issue,
- creating the branch,
- writing commits,
- adding/updating tests,
- opening the Merge Request,
- responding to review comments.

## Reviewer

Responsible for checking:

- correctness,
- code quality,
- design,
- readability,
- unnecessary duplication,
- test quality,
- scope of the change.

## Verifier

Responsible for checking whether the completed feature actually satisfies the issue acceptance criteria.

The verifier should normally be someone other than the implementer.

For small tasks, the reviewer and verifier may be the same person.

---

# 8. Issue Board

Use the Issue Board to show the current state of work.

Recommended flow:

```text
Backlog
   ↓
Ready
   ↓
In Progress
   ↓
In Review
   ↓
Verification
   ↓
Done
```

Use the following status labels if needed:

```text
status::backlog
status::ready
status::in-progress
status::review
status::verification
status::blocked
```

A closed issue represents **Done**.

---

# 9. Meaning of Board States

## Backlog

The work exists but is not ready to start.

Examples:

- idea still being discussed,
- low priority,
- requirements unclear,
- waiting for another feature.

---

## Ready

The issue can be started.

Before entering Ready, the issue should have:

- clear description,
- acceptance criteria,
- priority,
- milestone if applicable,
- dependencies identified.

---

## In Progress

A team member is actively implementing the issue.

The issue should have:

- an assignee,
- a verifier,
- a development branch.

---

## In Review

Implementation is ready for review.

Requirements:

- Merge Request exists,
- issue is linked,
- relevant tests exist,
- implementation is ready to inspect.

---

## Verification

Code review is complete and another member is checking the feature against the acceptance criteria.

---

## Blocked

If work cannot continue, add:

```text
status::blocked
```

and leave a comment such as:

```markdown
## Blocked

**Reason:**  
Waiting for the map loader from #18.

**Needed before continuing:**  
Issue #18 must be merged.

**Next action:**  
Resume implementation once #18 is complete.
```

Do not leave blocked work silently in `In Progress`.

---

# 10. Labels

Use labels to make issues easy to understand and filter.

## Type

```text
type::feature
type::bug
type::test
type::refactor
type::infrastructure
type::idea
```

## Component

```text
component::engine
component::state
component::map
component::player
component::inventory
component::enemy
component::npc
component::config
component::command
component::ui
component::tester
component::ci
component::docker
```

## Priority

```text
priority::critical
priority::high
priority::medium
priority::low
```

Priority meaning:

```text
critical  → main is broken or submission requirement is failing
high      → required feature or blocks other work
medium    → normal planned work
low       → optional improvement / polish
```

---

# 11. Milestones

Milestones group related issues into a larger stage of the project.

Recommended milestones:

```text
M1 — Planning & Architecture

M2 — Minimum Playable Game

M3 — Additional Features & Testing

M4 — Docker & Finalisation
```

Issues should be assigned to the milestone in which the team expects them to be completed.

If an issue must move to another milestone, leave a short explanation in the issue.


---

# 12. Branches

Create branches from the latest `main`.

Recommended naming:

```text
feature/<issue-number>-<description>
bugfix/<issue-number>-<description>
test/<issue-number>-<description>
refactor/<issue-number>-<description>
infra/<issue-number>-<description>
```

Examples:

```text
feature/21-player-movement

feature/35-inventory

bugfix/41-invalid-room-movement

test/52-game-engine-tests

infra/60-gitlab-ci
```

Before starting:

```bash
git checkout main
git pull
git checkout -b feature/21-player-movement
```

A branch should normally correspond to one issue.

---

# 13. Commits

Commits should be small enough that another developer can understand what changed.

Recommended format:

```text
<type>(<component>): <description> (#issue)
```

Examples:

```text
feat(engine): add player movement handling (#21)

feat(inventory): add item pickup (#35)

fix(map): prevent movement through missing exit (#41)

test(engine): add invalid movement tests (#52)

ci: run Maven tests in pipeline (#60)
```

Suggested commit types:

```text
feat
fix
test
refactor
docs
ci
build
chore
```

Avoid commit messages such as:

```text
update
changes
work
final
stuff
fixed
```

Do not wait until an entire feature is complete before creating one very large commit.

---

# 14. Merge Requests

All normal development branches should be merged into `main` using a Merge Request.

Recommended title:

```text
[Issue #21] Implement player movement
```

Recommended description:

```markdown
## Related Issue

Closes #21

## Summary

Briefly describe the change.

## Implementation

Explain the main implementation approach.

## Testing

- [ ] New tests added where needed
- [ ] Existing tests pass
- [ ] Manual testing completed if needed
- [ ] CI/CD pipeline passes

## Acceptance Criteria

- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

Anything important for the reviewer.
```

Using:

```text
Closes #21
```

links the MR to the issue and allows GitLab to close the issue after the MR is merged.

---

# 15. Code Review

The implementer should not merge their own work without another member checking it.

The reviewer should check:

```text
Correctness
Design
Readability
Naming
Duplication
Tests
Edge cases
Scope
```

Review comments should explain the concern.

Example:

```text
The method currently allows movement when `nextRoom` is null.

Could we return false here and add a JUnit test for an invalid exit?
```

If changes are requested:

```text
Reviewer comment
      ↓
Implementer updates branch
      ↓
New commit is pushed
      ↓
Merge Request updates automatically
      ↓
Reviewer checks again
```

Do not open a new MR simply because review changes were requested.

---

# 16. Verification

Review and verification are related but different.

```text
Review
=
Is the implementation well written and technically correct?

Verification
=
Does the feature actually satisfy the issue requirements?
```

The verifier should read the original acceptance criteria and check each one.

Suggested verification comment:

```markdown
## Verification

Verified by @username

- [x] Player can move to a connected room
- [x] Invalid movement is rejected
- [x] Player position updates correctly
- [x] JUnit tests pass
- [x] CI/CD passes

Result: Approved for merge.
```

If verification fails:

```markdown
## Verification Failed

Expected:
...

Observed:
...

Steps to reproduce:

1. ...
2. ...
3. ...

Returning issue to In Progress.
```

Then move the issue back to:

```text
status::in-progress
```

---

# 17. CI/CD

The GitLab pipeline should automatically check the project.

At minimum:

```text
Commit / Merge
      ↓
Compile
      ↓
Run JUnit Tests
      ↓
Pass / Fail
```

Recommended stages:

```text
build
test
package
```

Docker validation may be added later if useful.

A failed pipeline must be investigated.

Do not merge an MR while required CI checks are failing.

Do not modify a correct test simply to make the pipeline pass.

---


# 19. Discussions and Decisions

Discussion about an issue should happen in the issue when possible.

Use **Issue comments** for:

```text
requirements
feature ideas
scope
dependencies
architecture decisions
blockers
```

Use **Merge Request discussions** for:

```text
implementation
code review
test coverage
requested code changes
```

Team chat can still be used for quick communication.

However, if a decision affects the project, summarise it in GitLab.

Example:

```text
Decision:

Room connections will be stored using room IDs in game.json.

Reason:

This keeps the JSON configuration simple and prevents circular objects.
```

This allows someone who was not part of the original conversation to understand why the decision was made.

---

# 24. Example Workflow

Suppose the team wants to implement inventory.

Create:

```text
Issue #35
[Feature] Implement basic inventory
```

Labels:

```text
type::feature
component::inventory
priority::high
status::ready
```

Acceptance criteria:

```text
[ ] Player has an inventory
[ ] Player can pick up an item
[ ] Picked-up item disappears from room
[ ] Inventory command lists held items
[ ] Invalid pickup does not crash the game
[ ] JUnit tests cover pickup behaviour
```

Assign:

```text
Implementer: Alice
Verifier: Bob
```

Alice starts:

```bash
git checkout main
git pull
git checkout -b feature/35-inventory
```

Example commits:

```text
feat(inventory): add inventory model (#35)

feat(inventory): support item pickup (#35)

test(inventory): test valid and invalid pickup (#35)
```

Alice opens:

```text
[Issue #35] Implement basic inventory
```

with:

```text
Closes #35
```

The issue moves:

```text
Ready
  ↓
In Progress
  ↓
In Review
```

A reviewer checks the code.

Bob then verifies the acceptance criteria.

When CI/CD passes and verification is complete:

```text
Merge Request → main
```

Then:

```text
Issue #35 → Closed / Done
```

---

# 25. Main Principle

The repository should make it possible to understand:

```text
WHAT was changed?
WHY was it changed?
WHO implemented it?
WHO checked it?
HOW was it tested?
WHICH issue required it?
WHEN was it integrated?
```

If the GitLab history answers those questions clearly, the project workflow is working correctly.
