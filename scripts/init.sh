#!/usr/bin/env bash
# Initialize the bootstrap project for your own use:
#   - rename the Gradle root project
#   - move the Kotlin sources into your own package
#   - update the Docker image name
#
# Usage:
#   ./scripts/init.sh                                       # interactive
#   ./scripts/init.sh --name acme-tests \                   # non-interactive
#                     --package com.acme.loadtest \
#                     --image acme/loadtest \
#                     --group com.acme \
#                     --version 0.1.0-SNAPSHOT
#
# Re-running this script is safe but unnecessary: at the end it offers to delete itself.

set -euo pipefail

ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

NAME=""
PACKAGE=""
IMAGE=""
GROUP=""
VERSION=""

usage() {
  sed -n '2,17p' "$0" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --name)    NAME="$2"; shift 2 ;;
    --package) PACKAGE="$2"; shift 2 ;;
    --image)   IMAGE="$2"; shift 2 ;;
    --group)   GROUP="$2"; shift 2 ;;
    --version) VERSION="$2"; shift 2 ;;
    -h|--help) usage ;;
    *) echo "Unknown flag: $1" >&2; usage ;;
  esac
done

prompt() {
  local var_name="$1" prompt="$2" default="$3" answer
  read -r -p "$prompt [$default]: " answer
  printf -v "$var_name" '%s' "${answer:-$default}"
}

[[ -z $NAME    ]] && prompt NAME    "Project name (Gradle rootProject.name)" "my-load-tests"
[[ -z $PACKAGE ]] && prompt PACKAGE "Base Kotlin package"                     "com.example.loadtest"
[[ -z $IMAGE   ]] && prompt IMAGE   "Docker image name"                       "${NAME%%-*}/${NAME}"
[[ -z $GROUP   ]] && prompt GROUP   "Maven/Gradle group"                      "$PACKAGE"
[[ -z $VERSION ]] && prompt VERSION "Initial version"                         "0.1.0-SNAPSHOT"

echo
echo "About to apply:"
echo "  rootProject.name = $NAME"
echo "  group            = $GROUP"
echo "  version          = $VERSION"
echo "  base package     = $PACKAGE"
echo "  Docker image     = $IMAGE"
echo
read -r -p "Continue? [y/N] " confirm
[[ "$confirm" =~ ^[Yy]$ ]] || { echo "Aborted."; exit 1; }

# 1. settings.gradle.kts — rootProject.name
sed -i.bak -E "s|^rootProject\.name = \".*\"|rootProject.name = \"$NAME\"|" settings.gradle.kts

# 2. build.gradle.kts — group, version, docker image name.
# Use [[:space:]]* instead of \s for portability (BSD sed, including macOS, has no \s).
sed -i.bak -E "s|^group = \".*\"|group = \"$GROUP\"|" build.gradle.kts
sed -i.bak -E "s|^version = \".*\"|version = \"$VERSION\"|" build.gradle.kts
sed -i.bak -E "s|^([[:space:]]+name = )\".*\"|\1\"$IMAGE\"|" build.gradle.kts

# 3. Move Kotlin sources to the new package.
OLD_PACKAGE="my.bootstrap"
OLD_PATH="src/main/kotlin/${OLD_PACKAGE//./\/}"
NEW_PATH="src/main/kotlin/${PACKAGE//./\/}"

if [[ -d "$OLD_PATH" && "$OLD_PATH" != "$NEW_PATH" ]]; then
  mkdir -p "$(dirname "$NEW_PATH")"
  # `git mv` if we're in a git repo, otherwise plain mv.
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    git mv "$OLD_PATH" "$NEW_PATH"
  else
    mv "$OLD_PATH" "$NEW_PATH"
  fi
fi

# Same treatment for src/test if it exists.
OLD_TEST="src/test/kotlin/${OLD_PACKAGE//./\/}"
NEW_TEST="src/test/kotlin/${PACKAGE//./\/}"
if [[ -d "$OLD_TEST" && "$OLD_TEST" != "$NEW_TEST" ]]; then
  mkdir -p "$(dirname "$NEW_TEST")"
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    git mv "$OLD_TEST" "$NEW_TEST"
  else
    mv "$OLD_TEST" "$NEW_TEST"
  fi
fi

# 4. Rewrite "package my.bootstrap" declarations and any remaining FQN references.
find src -type f -name "*.kt" -print0 | xargs -0 sed -i.bak -E \
  -e "s|^package $OLD_PACKAGE|package $PACKAGE|" \
  -e "s|\\b$OLD_PACKAGE\\b|$PACKAGE|g"

# 5. Drop sed backups.
find . -name "*.bak" -type f -delete

echo
echo "Done. Verify with:"
echo "    ./gradlew clean build qalipsisRunAllScenarios"
echo

read -r -p "Delete this init script (and scripts/init.ps1)? [Y/n] " del
if [[ ! "$del" =~ ^[Nn]$ ]]; then
  rm -f scripts/init.sh scripts/init.ps1
  rmdir scripts 2>/dev/null || true
  echo "Removed scripts/."
fi
