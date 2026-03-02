#!/bin/bash

# Noteverso Backend Startup Script

cd "$(dirname "$0")"

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

cd noteverso-core

echo "Starting Noteverso backend..."
echo "Working directory: $(pwd)"

# Run Spring Boot application using JAR
java -jar target/noteverso-core-0.0.1-SNAPSHOT.jar
