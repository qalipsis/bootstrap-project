# CLAUDE.md

Notes for AI coding assistants working in this repository.

## What this project is

A starter template for [QALIPSIS](https://qalipsis.io) load-testing scenarios. Users
clone it, run `./scripts/init.sh` to personalize the package and Docker image name, then
write Kotlin DSL scenarios under `src/main/kotlin`.

## Tech stack

- Kotlin (JVM) + Gradle 8.5 with the Kotlin DSL
- QALIPSIS Gradle bootstrap plugin (`io.qalipsis.bootstrap`) — version override in `gradle.properties`
- Apache HTTP client via the `io.qalipsis.plugins.http` plugin
- Kotest 5.x + JUnit 5 for tests
- bmuschko `docker-remote-api` plugin + Shadow plugin for the fat JAR / image (see [`docs/migrate-to-bmuschko-docker.md`](migrate-to-bmuschko-docker.md) for the migration history)

## Repository layout

| Path                                          | Purpose                                                        |
|-----------------------------------------------|----------------------------------------------------------------|
| `build.gradle.kts`                            | Plugins, deps, `qalipsis { plugins { ... } }`, Docker config.  |
| `gradle.properties`                           | `qalipsis.version` override + Gradle perf flags.               |
| `settings.gradle.kts`                         | `rootProject.name` (rewritten by init script).                 |
| `qalipsis.yml`, `logback.xml`                 | Runtime config (logging, events, metrics).                     |
| `src/main/kotlin/my/bootstrap/`               | Example scenario — moved by init script.                       |
| `src/test/kotlin/my/bootstrap/`               | Sample test (kotest + JUnit 5).                                |
| `src/main/docker/Dockerfile`                  | Base image for `./gradlew docker`.                             |
| `scripts/init.{sh,ps1}`                       | One-shot rename of project, package, image.                    |
| `docker-compose.yml`                          | Local mock backend for offline scenario development.           |
| `.github/workflows/`                          | CI (build + scenario run) and release (Docker push).           |
| `docs/cloud.md`                               | Optional QALIPSIS Cloud publication flow.                      |

## Common commands

| Command                                  | What it does                                              |
|------------------------------------------|-----------------------------------------------------------|
| `./gradlew build`                        | Compile, test, fat JAR.                                   |
| `./gradlew qalipsisRunAllScenarios`      | Run every `@Scenario` in `src/main/kotlin`.               |
| `./gradlew test`                         | Run JUnit/kotest unit tests only.                         |
| `./gradlew distZip`                      | Bundle scenarios into a distributable ZIP.                |
| `./gradlew docker`                       | Build the Docker image.                                   |
| `./scripts/init.sh`                      | Rename project + package + image in one shot.             |

## Conventions and constraints

- **Kotlin code style**: Kotlin official, 4-space indent (see `.editorconfig`).
- **Configuration cache**: explicitly OFF — kapt (used by QALIPSIS annotation processing) is not yet config-cache-compatible. Don't re-enable `org.gradle.configuration-cache` without verifying this.
- **`isZip64 = true` on `shadowJar`**: required because QALIPSIS deps push the fat JAR past 65 535 entries. Don't remove.
- **Cloud plugin commented by default**: enabling it requires a token (see `docs/cloud.md`). Keep it opt-in so a fresh clone builds without secrets.
- **Example scenario hits `https://httpbin.org`**: chosen so the first run works with no setup. If you change it, also update `docker-compose.yml` and `README.md`.
- **`my.bootstrap` is a placeholder package**: anywhere you'd hardcode `my.bootstrap`, the init script will rewrite it. If you add new files, follow the same convention.
- **Don't commit secrets**: `.env`, `.env.*`, `local.properties`, and `gradle-local.properties` are gitignored. Tokens belong in `~/.gradle/gradle.properties` or CI secrets.

## When making changes

- Verify `./gradlew build qalipsisRunAllScenarios` passes — both compile and runtime smoke must stay green.
- If you change `MyBootstrapScenario.kt`, also update `MyBootstrapScenarioTest.kt` and the README quickstart description if behavior changed.
- The init script is exercised by hand; if you change it, smoke-test it on a temp copy before committing.

## Authoritative external references

Use these when the local code doesn't tell you the answer — the QALIPSIS DSL is generic-heavy
and easy to guess wrong from training data alone.

- **QALIPSIS user documentation** (DSL, profiles, runtime, configuration): <https://qalipsis.io/docs/user-documentation/latest/>
- **`qalipsis-examples` repo** — canonical DSL patterns, one scenario per plugin (HTTP, Kafka, JDBC, Cassandra, Mongo, gRPC, …): <https://github.com/qalipsis/qalipsis-examples>
- **QALIPSIS Gradle plugin** (`io.qalipsis.bootstrap`, `io.qalipsis.cloud`): <https://github.com/qalipsis/qalipsis-gradle-plugin>
- **HTTP plugin source** (DSL surface, `HttpResult`, `HttpResponse`, request builders): <https://github.com/qalipsis/qalipsis-plugin-http>
- **Cloud publication flow** (token, plugin, CI): [`docs/cloud.md`](docs/cloud.md)
- **QALIPSIS Cloud product**: <https://qalipsis.io>

### Verifying API signatures locally

When in doubt about a method/type signature, **inspect the resolved jars in the Gradle cache**
rather than guessing — they reflect exactly what the build is using:

```bash
# Find the jar that owns a class:
for jar in $(find ~/.gradle/caches/modules-2 -name "*.jar" | grep -v sources); do
  unzip -l "$jar" 2>/dev/null | grep -q "MyClassName.class" && echo "$jar"
done

# Inspect signatures:
jar xf "$JAR" 'io/qalipsis/.../MyClass.class'
javap -p io/qalipsis/.../MyClass.class
```

This is how the `verify` / `HttpResult<INPUT, OUTPUT>` (vs. `HttpResponse<BODY>`) confusion was
resolved when wiring up the example scenario — the API surface had changed and only the
on-disk jar was authoritative.
