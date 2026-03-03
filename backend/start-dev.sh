#!/bin/bash

# Noteverso Backend Startup Script

cd "$(dirname "$0")"

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "Starting Noteverso backend..."
echo "Working directory: $(pwd)"

# Run Spring Boot application using Maven
./mvnw spring-boot:run -pl noteverso-core
