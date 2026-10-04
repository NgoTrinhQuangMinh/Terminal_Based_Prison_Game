"""Verify the built game image through real Docker processes and a POSIX PTY.

Author: Minh. Uses only Python's standard library; run on Linux with Docker CLI.
"""
import argparse
import errno
import fcntl
import os
from pathlib import Path
import pty
import select
import signal
import struct
import subprocess
import termios
import time
import uuid
from collections import deque
from contextlib import contextmanager

LOGS = Path("package-logs")
JOB = os.environ.get("CI_JOB_ID", "local-" + uuid.uuid4().hex[:12])


def command(args, name, input_text=None, timeout=60):
    """Run a bounded command, persist output even on failure, and require success."""
    with (LOGS / (name + ".log")).open("w") as log:
        result = subprocess.run(args, input=input_text, text=True,
                                stdout=log, stderr=subprocess.STDOUT, timeout=timeout)
    output = (LOGS / (name + ".log")).read_text()
    if result.returncode:
        raise RuntimeError(f"{name} exited {result.returncode}; see {name}.log")
    return output


@contextmanager
def container_name(scenario):
    """Name and label each smoke container and remove it on every exit path."""
    name = f"maze-smoke-{JOB}-{scenario}"
    try:
        yield name
    finally:
        result = subprocess.run(["docker", "rm", "-f", name], capture_output=True,
                                text=True, timeout=20)
        with (LOGS / "cleanup.log").open("a") as log:
            log.write(result.stdout + result.stderr)


def docker_run(name):
    """Return common run options identifying containers owned by this job."""
    return ["docker", "run", "--rm", "--name", name,
            "--label", f"maze-ci-job={JOB}"]


def route(resource, start_marker, end_marker):
    """Find movement keys in the checked-in map without crossing a locked exit."""
    rows = Path(resource).read_text().splitlines()
    locations = {tile: (x, y) for y, row in enumerate(rows)
                 for x, tile in enumerate(row) if tile in "P1X"}
    start, end = locations[start_marker], locations[end_marker]
    queue = deque([(start, "")])
    seen = {start}
    while queue:
        (x, y), keys = queue.popleft()
        if (x, y) == end:
            return keys
        for key, dx, dy in [("d", 1, 0), ("a", -1, 0), ("s", 0, 1), ("w", 0, -1)]:
            nx, ny = x + dx, y + dy
            if not (0 <= ny < len(rows) and 0 <= nx < len(rows[ny])):
                continue
            tile = rows[ny][nx]
            if tile in "-|" or (nx, ny) in seen or (tile == "X" and end_marker != "X"):
                continue
            seen.add((nx, ny))
            queue.append(((nx, ny), keys + key))
    raise AssertionError(f"No route from {start_marker} to {end_marker}: {resource}")


def continuous(image, choice, label, resource, answer):
    """Drive menu, movement, riddle, victory and clean exit in a real terminal."""
    with container_name(label.lower()) as name:
        master, slave = pty.openpty()
        fcntl.ioctl(slave, termios.TIOCSWINSZ, struct.pack("HHHH", 35, 100, 0, 0))
        process = subprocess.Popen(docker_run(name) + ["-it", "-e", "TERM=xterm-256color", image],
                                   stdin=slave, stdout=slave, stderr=slave)
        os.close(slave)
        buffer = bytearray()
        with (LOGS / (label.lower() + "-terminal.log")).open("wb") as log:
            def expect(text):
                """Consume terminal output until the expected text appears or time expires."""
                target = text.encode()
                deadline = time.monotonic() + 30
                while target not in buffer:
                    if time.monotonic() >= deadline:
                        raise TimeoutError(f"{label}: did not display {text!r}")
                    if not select.select([master], [], [], 0.25)[0]:
                        continue
                    try:
                        data = os.read(master, 65536)
                    except OSError as error:
                        if error.errno == errno.EIO:
                            data = b""
                        else:
                            raise
                    if not data:
                        raise AssertionError(f"{label}: exited before displaying {text!r}")
                    log.write(data)
                    log.flush()
                    buffer.extend(data)
                del buffer[:buffer.index(target) + len(target)]

            try:
                expect("CHOOSE DIFFICULTY")
                os.write(master, choice.encode())
                expect("MAZE ESCAPE - " + label)
                expect("WASD / Arrows")
                os.write(master, (route(resource, "P", "1") + "t").encode())
                expect("Answer:")
                os.write(master, (answer + "\r").encode())
                expect("Correct!")
                os.write(master, route(resource, "1", "X").encode())
                expect("Game ended.")
                os.write(master, b" ")
                expect("You escaped!")
                if process.wait(timeout=15) != 0:
                    raise AssertionError(f"{label}: container exit failed")
            finally:
                if process.poll() is None:
                    process.terminate()
                    try:
                        process.wait(timeout=5)
                    except subprocess.TimeoutExpired:
                        process.kill()
                        process.wait(timeout=5)
                os.close(master)


def main():
    """Build one commit-tagged image and fail if any packaged behaviour is broken."""
    parser = argparse.ArgumentParser()
    parser.add_argument("--image", required=True)
    parser.add_argument("--skip-build", action="store_true", help="Test an already built local image")
    args = parser.parse_args()
    LOGS.mkdir(exist_ok=True)
    command(["docker", "info"], "docker-info", timeout=30)
    if not args.skip_build:
        command(["docker", "build", "--progress=plain", "-t", args.image, "."], "build", timeout=900)
    image = command(["docker", "image", "inspect", "--format", "{{.Id}}", args.image], "image").strip()
    with container_name("user") as name:
        uid = command(docker_run(name) + ["--entrypoint", "id", image, "-u"], "runtime-user").strip()
        if not uid.isdigit() or int(uid) == 0:
            raise AssertionError(f"Expected non-root runtime user, got {uid!r}")
    with container_name("line") as name:
        commands = "\n".join(list("ddssddd") + ["fight", "fight"] + list("dddww")) + "\n"
        output = command(docker_run(name) + ["-i", image, "--line"], "line-combat", commands)
        for expected in ["You defeat the NPC", "Drops collected:", "You escaped!", "Congratulations"]:
            if expected not in output:
                raise AssertionError(f"Line playthrough did not report {expected!r}")
    resources = "src/main/resources/"
    for choice, label, resource, answer in [
            ("1", "Easy", "difficulty/easy/maze.txt", "sun"),
            ("2", "Normal", "maze.txt", "clock"),
            ("3", "Hard", "difficulty/hard/maze.txt", "light")]:
        continuous(image, choice, label, resources + resource, answer)
        print(f"PASS: {label} terminal playthrough", flush=True)
    (LOGS / "result.txt").write_text("PASS: non-root runtime, combat escape, all difficulty UI/riddle escapes.\n")
    print("All packaged gameplay checks passed.", flush=True)


def interrupted(signum, frame):
    """Turn CI cancellation into an exception so container cleanup still runs."""
    raise KeyboardInterrupt(f"Received signal {signum}")


if __name__ == "__main__":
    signal.signal(signal.SIGTERM, interrupted)
    main()
