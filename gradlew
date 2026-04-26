#!/usr/bin/env bash
# Lightweight gradlew bootstrapper for ModForge AI generated projects.
set -eu
DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading gradle-wrapper.jar..."
  curl -fsSL -o "$WRAPPER_JAR" \
    https://raw.githubusercontent.com/gradle/gradle/v8.7.0/gradle/wrapper/gradle-wrapper.jar
fi
exec java -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
