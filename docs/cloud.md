# Publish to QALIPSIS Cloud

Once you have a working scenario locally, you can push it to
[QALIPSIS Cloud](https://app.qalipsis.io/) so it can be executed from the web UI.

## 1. Create an API token

1. Open <https://app.qalipsis.io/>, then **Settings → API Tokens** in the left sidebar.
2. Click **Generate new token** to open the drawer.
3. Grant at least these permissions:
   - `write:scenario`
   - `read:scenario`
4. Click **Generate** and **copy the token immediately** — it won't be shown again.

## 2. Configure the project

Add the token to a file Gradle reads but **never commit it**. The simplest option is your
user-level `~/.gradle/gradle.properties` (already outside the repo):

```properties
qalipsis.cloud.registry.token=<paste your token here>
```

If you need a project-local override (CI, multi-tenant setups), create
`gradle-local.properties` next to `build.gradle.kts` — it is already in `.gitignore` —
and put the property there.

## 3. Enable the Gradle plugin

In `build.gradle.kts`, uncomment the cloud plugin:

```kotlin
plugins {
    id("io.qalipsis.bootstrap") version "0.1.5"
    id("io.qalipsis.cloud") version "0.1.5"   // <-- uncomment me
    // ...
}
```

## 4. Publish

```bash
./gradlew qalipsisCloudPublish
```

The command takes seconds to a couple of minutes. On success, your scenarios appear in the
QALIPSIS Cloud UI when you create a new campaign.

## CI publication

For CI, expose the token as a secret named `QALIPSIS_CLOUD_TOKEN` and pass it to Gradle:

```bash
./gradlew qalipsisCloudPublish -Pqalipsis.cloud.registry.token=$QALIPSIS_CLOUD_TOKEN
```

The provided `.github/workflows/release.yml` does exactly this on tag push when the secret is set.
 is executed