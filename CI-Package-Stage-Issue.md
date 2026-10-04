# Add a package stage to verify the Docker application

**Priority:** P2

These are two alternative descriptions of the same issue. Use the appropriate template when posting it.

## Template 1 — Feature proposal

### Idea

Add a `package` stage after `build` and `test` in the CI pipeline. Build the Docker image and verify that the packaged game starts and works as a whole.

### Why?

Passing Java tests does not guarantee that the Docker image contains the correct launcher, dependencies and resources. Testing the container helps catch packaging and runtime problems before merging.

### Possible Approach

Build the image from the repository's Dockerfile, then run automated smoke tests against that exact image. Check application startup, difficulty selection, representative gameplay and clean exit.

Use scripted line mode for a complete gameplay route and a pseudo-terminal for the continuous UI. Apply timeouts and clean up containers after each job.

### Impact

- GitHub Actions workflow configuration.
- Docker build and runtime verification.
- Container smoke-test scripts.
- CI execution time and runner requirements.

### Questions

- Does the GitHub Actions runner support Docker builds and container execution?
- Should the image eventually be published to a registry? Publishing can remain a separate issue.

---

## Template 2 — Implementation task

### Summary

Add a `package` job to the GitHub Actions workflow that builds the Docker image and verifies the packaged application through automated container smoke tests.

### Motivation

Verify that the application works inside its delivered container, including configuration resources, Java dependencies, terminal interaction and gameplay.

### Acceptance Criteria

- [ ] The pipeline runs stages in this order: `build`, `test`, `package`.
- [ ] The package job builds the repository's Dockerfile without disabling its tests.
- [ ] Smoke tests run against the exact image built by that job.
- [ ] The container starts successfully as the configured non-root user.
- [ ] An automated line-mode playthrough collects the key, reaches the exit and verifies victory.
- [ ] A pseudo-terminal smoke test selects each difficulty and verifies that its game screen appears.
- [ ] Continuous UI input, an NPC interaction and quitting work inside the container.
- [ ] Tests assert expected output or game outcomes rather than relying only on the process exit code.
- [ ] Build failures, unexpected outcomes and timeouts fail the package job.
- [ ] Logs are retained as CI artifacts, including when verification fails.
- [ ] Temporary containers are cleaned up on success and failure.
- [ ] Existing build and test jobs continue to pass.

### Dependencies

**Depends on (complete and integrate first):**

- Docker packaging, including the Dockerfile and application launcher.
- Continuous terminal UI.
- Difficulty selection and bundled maps.
- Working gameplay, including NPC interactions, rewards and victory conditions.
- A CI runner with a supported Docker build and execution setup.

**Blocks:**

- Treating the container image as verified for release.
- Future automated image publishing that requires packaging verification.

**Parallel work:**

Smoke-test scripts can be developed while runner support is configured. Pipeline integration requires both.

### Technical Notes

Use the existing Dockerfile as the packaging source. Tag the image with the commit identifier to avoid testing an unrelated image.

CI has no interactive terminal by default. Use a pseudo-terminal harness for continuous UI checks and piped input with `--line` for deterministic gameplay checks. Give every container run a timeout.

This issue verifies packaging and runtime behaviour. Registry publishing and deployment are outside its scope.
