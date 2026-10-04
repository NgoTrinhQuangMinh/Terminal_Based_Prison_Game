# Package-stage verification (#45)

The pipeline runs build, test, then package. The package job builds the repository
Dockerfile (including its Gradle tests) and runs the resulting immutable image ID.
It verifies the non-root user, a complete line-mode combat escape and full
Easy/Normal/Hard riddle escapes through real pseudo-terminal input. Startup,
difficulty labels, rewards, victory output and zero exit status are checked.
No image is published and no deployment occurs.

## Runner prerequisite

The `comp2120` runner assigned to `docker-package` must use the Docker executor,
permit privileged Docker-in-Docker, and share `/certs/client` between the job and
service containers. This repository cannot configure the institution's runner.
If that runner does not provide these capabilities, an administrator must supply
a suitable runner/tag before the package job can succeed. Other jobs are unchanged.
The job enables `FF_NETWORK_PER_BUILD` to use a separate bridge network and
service DNS instead of legacy container links. This addresses the runner error
`bad parameter: link is not supported` during environment preparation. The
runner must not force a conflicting `network_mode`; its administrator must
resolve that setting if this preparation error persists.

The job uses TLS on port 2376, following the official GitLab setup:
https://docs.gitlab.com/ci/docker/docker_in_docker/

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
