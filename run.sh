#!/bin/bash

# Prompt for API key and region if not set in environment
if [ -z "$AZURE_SPEECH_KEY" ]; then
  read -p "Azure Speech Key: " AZURE_SPEECH_KEY
fi
if [ -z "$AZURE_SPEECH_REGION" ]; then
  read -p "Azure Speech Region: " AZURE_SPEECH_REGION
fi

export AZURE_SPEECH_KEY
export AZURE_SPEECH_REGION

# Build the project if the JAR doesn't exist
if [ ! -f target/TraductorVoz-0.0.1-SNAPSHOT.jar ]; then
  mvn package || { echo "Build failed"; exit 1; }
fi

# Run the application, forwarding any extra args (e.g., target language)
java -jar target/TraductorVoz-0.0.1-SNAPSHOT.jar "$@"
