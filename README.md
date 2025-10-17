# Bootstrap Project

This repository contains a preconfigured project with a skeleton to develop scenarios for QALIPSIS.

## Prerequisites
1. Java 11 or later (JDK) is installed on your machine.
2. You have:
   1. either a text editor with a plugin for Kotlin ([Atom](https://atom-editor.cc/), [Visual Studio Code](https://code.visualstudio.com/)...)
   2. or even better an [IDE that supports Kotlin](https://kotlinlang.org/docs/kotlin-ide.html).

## How to use the bootstrap

1. Clone the bootstrap project to your local machine (`git clone https://github.com/qalipsis/bootstrap-project.git`) and open it in your IDE or text editor.
2. In the file `settings.gradle.kts`, rename your project.
3. Open `build.gradle.kts`, add the required QALIPSIS plugins in the section `qalipsis { plugins {} }` and the dependencies you need.
4. Reload the Gradle project if your IDE or editor provides this functionality.
5. Rename the file `src/main/kotlin/my/bootstrap/MyBootstrapScenario.kt` and docker image name in the `docker {}` area.
6. Develop your own scenario.
7. Optional: create a Java archive of your scenario. Execute the statement `./gradlew build`.
8. Optional: create a ZIP archive to distribute your scenarios: `./gradlew distZip`.
9. Optional: upload the docker image to execute your scenarios in Docker or Kubernetes: `./gradlew dockerPush`.

## Links
1. [QALIPSIS Gradle Plugin](https://github.com/qalipsis/qalipsis-gradle-plugin) - Gradle plugin to speed up the development of QALIPSIS scenarios.
2. [Gradle Shadow](https://github.com/johnrengelman/shadow) - Gradle plugin for creating fat/uber JARs with support for package relocation.
3. [Docker Gradle Plugin](https://github.com/palantir/gradle-docker) - Gradle plugin for working with Docker containers.
