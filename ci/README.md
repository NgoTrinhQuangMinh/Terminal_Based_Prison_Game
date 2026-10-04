# Package-stage verification (#45)

The pipeline runs build, test, then package. The package job builds the repository
Dockerfile (including its Gradle tests) and runs the resulting immutable image ID.
It verifies the non-root user, a complete line-mode combat escape and full
Easy/Normal/Hard riddle escapes through real pseudo-terminal input. Startup,
difficulty labels, rewards, victory output and zero exit status are checked.
No image is published and no deployment occurs.

## Runner prerequisite

The GitHub Actions package job runs on `ubuntu-latest`, where Docker is available
to the job. It does not require a GitLab runner, Docker-in-Docker service or
runner-specific network configuration. Repository Actions permissions only need
to allow the job to build and run local containers.

## Local Linux verification

With Docker and Python 3 installed, run from the repository root:

```sh
python3 ci/package_smoke.py --image maze-escape-ci:local
```

To repeat only smoke tests against a built image, add `--skip-build`.
The harness requires POSIX pseudo-terminals, so run it on Linux or inside a Linux
container with Docker access when using Windows. No Python packages are required.

The image build has a 15-minute timeout; commands and terminal expectations have
shorter timeouts. The CI job has a 20-minute limit. Containers are uniquely named
and labelled per job and removed in finally blocks. CI after_script also removes
that job's remaining containers on failure or cancellation when the daemon is
reachable. Docker-in-Docker state disappears when the job service is removed.

Raw terminal output, build output and runtime logs are retained in package-logs/
and uploaded even when a check fails. result.txt is written only after success.
