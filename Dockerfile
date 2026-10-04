# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
# Git on Windows may check the wrapper out with CRLF line endings.
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
COPY src/ src/
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon test installDist

FROM eclipse-temurin:17-jre-jammy AS runtime
RUN apt-get update \
    && apt-get install -y --no-install-recommends ncurses-bin \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --uid 10001 game
WORKDIR /app
COPY --from=build --chown=game:game /workspace/build/install/prison-game/ ./
ENV TERM=xterm-256color
USER game
ENTRYPOINT ["/app/bin/prison-game"]
