#!/bin/zsh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p out

echo "Compiling project..."
javac -d out $(find src -name '*.java')

echo "Launching game..."
java -cp out ui.MainMenu
