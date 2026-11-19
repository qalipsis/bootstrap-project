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

## Licensing

This repository is distributed under the [Apache License 2.0](./LICENSE). As stated in its section 5, every
contribution you submit is licensed under the same terms. The project does not accept contributions under any
other terms.

The Apache License covers the content of this repository only. Running a scenario relies on the QALIPSIS
runtime — `qalipsis-head`, `qalipsis-factory`, `qalipsis-runtime`, `qalipsis-shared`, `qalipsis-cluster` and
`qalipsis-webapp` — which is dual-licensed under the GNU AGPL 3.0 **or** a
commercial license, at the licensee's choice: see
[LICENSE-COMMERCIAL.md](https://github.com/qalipsis/qalipsis-oss/blob/main/LICENSE-COMMERCIAL.md) in
`qalipsis-oss`. The QALIPSIS API modules this repository compiles against are under the Apache License 2.0.

## Contributor License Agreement

Before your first pull request can be merged, you must sign
the [QALIPSIS Individual Contributor License Agreement](https://github.com/qalipsis/qalipsis-oss/blob/main/cla/individual-cla.md).
The CLA
bot posts the signing link as a comment on the pull request; signing takes a minute and is remembered for all your
later contributions to the QALIPSIS repositories. Contributing on behalf of an employer requires
the [Corporate Contributor License Agreement](https://github.com/qalipsis/qalipsis-oss/blob/main/cla/corporate-cla.md),
signed by someone entitled to bind the company.

The CLA does **not** transfer your copyright. You keep it, and grant AERIS IT Solutions GmbH a non-exclusive right to
distribute your contribution — under the Apache License 2.0, and in the commercial and SaaS editions of QALIPSIS.

## What you may contribute

- **Your own work only.** Submit only content you wrote, or have the right to submit. If your employer holds rights
  on your work, the corporate CLA must be signed first. You remain responsible for everything you submit, including
  content produced with code generators or AI assistants: review it as if you had written it yourself, and make sure
  it does not reproduce third-party code.
- **No code from the QALIPSIS runtime modules.** Do not copy or adapt code from `qalipsis-head`, `qalipsis-factory`,
  `qalipsis-runtime`, `qalipsis-shared`, `qalipsis-cluster` or `qalipsis-webapp`: these modules
  are dual-licensed (AGPL 3.0 or commercial), and their code cannot enter an Apache-licensed repository. Depend on
  the API modules instead; if something you need is missing there, open an issue on
  [`qalipsis-oss`](https://github.com/qalipsis/qalipsis-oss/issues).
- **Third-party code** only under a license compatible with the Apache License 2.0 (Apache, MIT, BSD, …), with its
  original copyright notice kept, and mentioned explicitly in the pull request description. Never copy code
  licensed under the GPL, LGPL or AGPL, nor code published without a license.
- **Dependencies** must not bring a strong copyleft license — GPL without Classpath Exception, AGPL — into the
  dependency graph.
- **No secrets nor personal data**: no credentials, tokens, private hostnames or real user data, even in tests or
  configuration samples. Use placeholders.
- **Trademarks**: as stated in section 6 of the Apache License, the QALIPSIS name and logo are not licensed. Use them
  only to describe the origin of the code, not to name or promote a derived product.

## License headers

Files in this repository carry no license header, on purpose: the project is a template, copied by users into their
own repositories. Do not add one.

Projects created from this template belong to their authors, who choose the license of the code they write. The
files kept unchanged from the template remain under the Apache License 2.0, which only requires keeping its notice
when they are redistributed.

## Reporting issues

If the bootstrap fails on a fresh clone, that's a P0. Please include:

- OS, JDK version, Gradle output (`--stacktrace --info` is helpful).
- The exact command you ran.

Use the templates in `.github/ISSUE_TEMPLATE/` so we have the basics up front.
