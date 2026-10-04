# Package-stage verification (#45)

The pipeline runs build, test, then package. The package job builds the repository
Dockerfile (including its Gradle tests) and runs the resulting immutable image ID.
It verifies the non-root user, a complete line-mode combat escape and full
Easy/Normal/Hard riddle escapes through real pseudo-terminal input. Startup,
difficulty labels, rewards, intermediate map transitions, final victory output
and zero exit status are checked. Each playthrough completes both campaign maps.
No image is published and no deployment occurs.

## Runner prerequisite

The GitHub Actions package job runs on `ubuntu-latest`, using the runner's Docker
engine. The workflow runs on pushes to main, pull requests targeting main, and
manual dispatch. Permissions are limited to reading repository contents.

## Local Linux verification

With Docker and Python 3 installed, run from the repository root:

```sh
python3 ci/package_smoke.py --image terminal-prison-game:local
```

To repeat only smoke tests against a built image, add `--skip-build`.
The harness requires POSIX pseudo-terminals, so run it on Linux or inside a Linux
container with Docker access when using Windows. No Python packages are required.

The image build has a 15-minute timeout; commands and terminal expectations have
shorter timeouts. The CI job has a 20-minute limit. Containers are uniquely named
and labelled per job using `SMOKE_JOB_ID` (derived from GitHub run ID and attempt)
and removed in finally blocks. An `always()` workflow cleanup step also removes
that job's remaining containers when the daemon is reachable. Local runs use a
random identifier. The hosted runner is discarded after the job.

Raw terminal output, build output and runtime logs are retained in package-logs/
and uploaded even when a check fails. result.txt is written only after success.
