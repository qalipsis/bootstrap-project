# Contributing to the QALIPSIS bootstrap project

Thanks for taking the time to improve the bootstrap. This repository is a **template** —
its job is to make it as fast as possible for new users to get from `git clone` to a
running QALIPSIS scenario. Keep that bar in mind when proposing changes.

## Ground rules

- A fresh clone must build and run the example with **zero manual edits**:
  ```bash
  git clone <fork>
  cd bootstrap-project
  ./gradlew build qalipsisRunAllScenarios
  ```
  Both must exit 0. CI enforces this on every push.
- Keep the example scenario small and self-explanatory. Richer examples belong in
  [`qalipsis-examples`](https://github.com/qalipsis/qalipsis-examples).
- Don't introduce optional features that need extra setup (tokens, accounts, secrets)
  without making them strictly opt-in. The cloud plugin is the model: commented out by
  default, enabled by uncommenting one line.
- Prefer editing existing files over adding new ones. Every new file is one more thing
  the user has to read or rename.

## Local workflow

```bash
./gradlew test                       # unit tests (kotest + JUnit 5)
./gradlew build                      # full build, includes shadowJar + distZip
./gradlew qalipsisRunAllScenarios    # end-to-end smoke
./scripts/init.sh --name foo \       # smoke-test the rename helper if you touched it
                  --package com.foo  #   (run inside a temp copy)
```

## Pull requests

- One change per PR. Bundle `README.md` and `CLAUDE.md` updates with the code change they
  describe.
- Update or add tests when you change runtime behavior.
- The CI matrix runs Ubuntu / macOS / Windows × JDK 11 / 17 / 21. If your change is OS- or
  JDK-sensitive, call that out in the description.

## Reporting issues

If the bootstrap fails on a fresh clone, that's a P0. Please include:

- OS, JDK version, Gradle output (`--stacktrace --info` is helpful).
- The exact command you ran.

Use the templates in `.github/ISSUE_TEMPLATE/` so we have the basics up front.
