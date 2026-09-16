#!/bin/sh

set -e

GRADLE_VERSION=8.2.2
GRADLE_DIR="$HOME/.gradle/wrapper/dists/gradle-$GRADLE_VERSION-bin"
GRADLE_ZIP="$HOME/.gradle/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_HOME="$GRADLE_DIR/gradle-$GRADLE_VERSION"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
    mkdir -p "$GRADLE_DIR"

    if [ ! -f "$GRADLE_ZIP" ]; then
        curl -L \
          -o "$GRADLE_ZIP" \
          "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    fi

    unzip -q "$GRADLE_ZIP" -d "$GRADLE_DIR"
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
