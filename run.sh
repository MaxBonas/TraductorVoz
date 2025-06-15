#!/bin/bash

# Configuration file used to persist credentials
CONFIG_FILE=".traductor.env"

# Load stored variables if present
if [ -f "$CONFIG_FILE" ]; then
  # shellcheck source=/dev/null
  . "$CONFIG_FILE"
fi

# Prompt for API key and region if not set in environment
if [ -z "$AZURE_SPEECH_KEY" ]; then
  read -p "Azure Speech Key: " AZURE_SPEECH_KEY
  echo "AZURE_SPEECH_KEY=\"$AZURE_SPEECH_KEY\"" >> "$CONFIG_FILE"
fi
if [ -z "$AZURE_SPEECH_REGION" ]; then
  read -p "Azure Speech Region: " AZURE_SPEECH_REGION
  echo "AZURE_SPEECH_REGION=\"$AZURE_SPEECH_REGION\"" >> "$CONFIG_FILE"
fi

export AZURE_SPEECH_KEY
export AZURE_SPEECH_REGION

# Build the project if the JAR doesn't exist
# Compile the project so all classes are up to date
mvn -q package || { echo "Build failed"; exit 1; }

# Run the application using Maven so dependencies are on the classpath
mvn -q exec:java -Dexec.mainClass=traductor.Main -Dexec.args="$*" || {
  echo "Application failed";
  exit 1;
}

# Keep the window open so any error messages remain visible when
# the script is launched from a graphical shell.
read -p "Presiona Enter para salir" _
