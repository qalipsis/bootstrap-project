# QALIPSIS bootstrap project

This repository is a **template**. Its job is to give you a working Kotlin project with all
the [QALIPSIS](https://qalipsis.io) plumbing already in place — Gradle setup, runtime config,
Docker packaging, CI, tests — so you can delete the bundled example and start writing your
own load-testing scenarios immediately.

Everything under `src/` is illustrative: the `MyBootstrapScenario.kt` you see, its test, the
`docker-compose.yml`, and the references to `httpbin` are placeholders that demonstrate how
the pieces fit together. You are expected to replace them.

## 60-second quickstart

```bash
# 1. Get the project (or click "Use this template" on GitHub).
git clone https://github.com/qalipsis/bootstrap-project.git my-load-tests
cd my-load-tests

# 2. Run the bundled example end-to-end so you know the toolchain is healthy.
./gradlew qalipsisRunAllScenarios

# 3. Personalize the project (renames root, package and Docker image in one shot).
./scripts/init.sh
```

You should see a successful campaign report on stdout in under 30 seconds. From there, edit
`src/main/kotlin/.../MyBootstrapScenario.kt` (or replace it entirely) and re-run. That's the
loop.

## Prerequisites

- **JDK 11 or later** (Temurin recommended).
- A Kotlin-aware editor — [IntelliJ IDEA](https://www.jetbrains.com/idea/),
  [VS Code + Kotlin](https://marketplace.visualstudio.com/items?itemName=fwcd.kotlin), or any
  [IDE that supports Kotlin](https://kotlinlang.org/docs/kotlin-ide.html).
- Optional: Docker (needed for `./gradlew dockerBuild`, the Testcontainers-based test, and any
  containerised target you may add).

The Gradle wrapper is shipped with the project — no Gradle install required.

## Common Gradle tasks

| Command                             | What it does                                                   |
|-------------------------------------|----------------------------------------------------------------|
| `./gradlew qalipsisRunAllScenarios` | Run every `@Scenario` declared in `src/main/kotlin`.           |
| `./gradlew test`                    | Run the JUnit/kotest tests under `src/test`.                   |
| `./gradlew build`                   | Compile, test, and produce the fat JAR (`build/libs/*-qalipsis.jar`). |
| `./gradlew distZip`                 | Bundle scenarios into a distributable ZIP.                     |
| `./gradlew dockerBuild`             | Build a Docker image containing your scenarios.                |
| `./gradlew dockerPush`              | Publish that image to the registry configured in `build.gradle.kts`. |
| `./scripts/init.sh`                 | Rename project, base package and Docker image in one shot.     |

## Project layout

```
.
├── build.gradle.kts                          # plugins, dependencies, Docker config
├── gradle.properties                         # Gradle perf flags + optional QALIPSIS version override
├── gradle/libs.versions.toml                 # version catalog (plugins + libraries)
├── settings.gradle.kts                       # project name (renamed by ./scripts/init.sh)
├── qalipsis.yml                              # logging / metrics / events config for the runtime
├── logback.xml                               # log appenders
├── scripts/
│   ├── init.sh                               # one-shot rename helper (POSIX)
│   └── init.ps1                              # one-shot rename helper (Windows)
├── src/main/kotlin/my/bootstrap/             # ← write your scenarios here
│   └── MyBootstrapScenario.kt                # example scenario — replace with your own
├── src/test/kotlin/my/bootstrap/             # ← write your tests here
│   └── MyBootstrapScenarioTest.kt            # example test using QalipsisTestRunner + Testcontainers
├── src/main/docker/Dockerfile                # base image for `./gradlew docker`
├── docker-compose.yml                        # example mock backend (httpbin) for the bundled scenario
└── .github/workflows/                        # CI: build, run scenarios, release
```

## Add a step type or plugin

Most QALIPSIS plugins are added in two places:

1. Declare the plugin in `build.gradle.kts`:
   ```kotlin
   qalipsis {
       plugins {
           http()              // enabled out of the box (used by the example)
           // apacheCassandra()
           // jakartaEeMessaging()
           // jdbc()
           // kafka()
           // mongodb()
           // r2dbcJasync()
           // redisLettuce()
           // ...
       }
   }
   ```
2. Use the corresponding DSL in your scenario. See the
   [examples repo](https://github.com/qalipsis/qalipsis-examples) for one scenario per plugin.

## About the bundled example

> The sections below describe the example scenario that ships with the template. Once you
> have written your own scenario(s), feel free to delete `MyBootstrapScenario.kt`,
> `MyBootstrapScenarioTest.kt`, and `docker-compose.yml`, and remove this section from the
> README.

`MyBootstrapScenario.kt` POSTs a small payload to an HTTP endpoint and asserts the response
status. By default the URL is read from the `http.server.url` configuration property, which
defaults to `https://httpbin.org` when running via `./gradlew qalipsisRunAllScenarios`.

`MyBootstrapScenarioTest.kt` runs the same scenario against an isolated `httpbin` container
started by Testcontainers, so the test does not depend on the public service.

For ad-hoc local runs without going to the public httpbin, the bundled `docker-compose.yml`
brings the same image up on `localhost:8080`:

```bash
docker compose up -d
./gradlew qalipsisRunAllScenarios -Phttp.server.url=http://localhost:8080
docker compose down
```

(Replace this section with one that documents *your* scenarios as they take shape.)

## Run your scenarios in QALIPSIS Cloud

When you are ready to go beyond running campaigns from your laptop, you can publish your
scenarios to [**QALIPSIS Cloud**](https://app.qalipsis.io/) and trigger them from the web UI
— no runtime to host, no agents to scale, distributed minions out of the box, and rich
post-campaign reports.

The full setup (token creation, Gradle plugin, CI publication) is documented in
[`docs/cloud.md`](docs/cloud.md). The short version:

```bash
# 1. Generate a token in QALIPSIS Cloud (Settings → API Tokens, write:scenario + read:scenario).
# 2. Drop it into ~/.gradle/gradle.properties:
#       qalipsis.cloud.registry.token=<your token>
# 3. Uncomment the cloud plugin in build.gradle.kts.
# 4. Publish.
./gradlew qalipsisCloudPublish
```

The provided `.github/workflows/release.yml` will publish automatically on tag push when the
`QALIPSIS_CLOUD_TOKEN` repository secret is configured.

## Going further

- **Full DSL & runtime reference**: <https://qalipsis.io/docs/user-documentation/latest/>
- **Working examples (Kafka, JDBC, Cassandra, Mongo, gRPC, …)**: <https://github.com/qalipsis/qalipsis-examples>
- **Gradle plugin source**: <https://github.com/qalipsis/qalipsis-gradle-plugin>
- **Docker plugin (Palantir)**: <https://github.com/palantir/gradle-docker>
- **Shadow plugin (uber-JAR)**: <https://github.com/johnrengelman/shadow>

---

## Like QALIPSIS? Help us keep building it

QALIPSIS is open-source and will stay that way. The team behind it also offers paid
products that fund the project's development:

- ☁️ [**QALIPSIS Cloud**](https://qalipsis.io/) — managed runtime, distributed campaigns,
  hosted dashboards and historical reports. The fastest way from "my scenario compiles" to
  "my team can run it on demand."
- 🛡️ **Enterprise support, training and consulting** — get help architecting load tests,
  rolling QALIPSIS out across teams, or running it on your own infrastructure with an SLA.
  Drop us a line at <https://qalipsis.io> to start a conversation.

Even just starring the [GitHub repo](https://github.com/qalipsis), filing issues, or sharing
a scenario in the [examples repo](https://github.com/qalipsis/qalipsis-examples) is a real
help — thank you!
