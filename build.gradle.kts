plugins {
    id("java")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jline:jline-terminal:3.30.6")
    implementation("org.jline:jline-reader:3.30.6")
    runtimeOnly("org.jline:jline-terminal-jni:3.30.6")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Main")
    applicationName = "prison-game"
}

// Lets the game read the player's commands when started with `./gradlew run`.
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

// Runs the automatic gameplay tester without requiring terminal input.
tasks.register<JavaExec>("runTester") {
    group = "verification"
    description =
        "Runs the automatic game tester against the bundled game configuration."

    classpath =
        sourceSets["main"]
            .runtimeClasspath

    mainClass.set(
        "tester.GameTester"
    )
}
