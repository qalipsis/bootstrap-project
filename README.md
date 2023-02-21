# Bootstrap Project

This repository contains a preconfigured project with a skeleton to develop a scenario.

## How to use the bootstrap

1. Clone the bootstrap project to your local machine (`git clone https://github.com/qalipsis/bootstrap-project.git`) and open it in your IDE.
2. Open `build.gradle.kts` and uncomment the required plugins, then reload Gradle project.
3. Rename the file `src/main/kotlin/my/bootstrap/MyBootstrapScenario.kt` and docker image names.
4. Develop your own scenario.
5. Create an archive of your scenario. Execute the statement `./gradlew assemble`.
6. Upload the docker image: `./gradlew dockerPush`.

## Links
1. [Gradle Shadow](https://github.com/johnrengelman/shadow) - gradle plugin for creating fat/uber JARs with support for package relocation.
2. [Docker Gradle Plugin](https://github.com/palantir/gradle-docker) - the repository provides Gradle plugins for working with Docker containers.
