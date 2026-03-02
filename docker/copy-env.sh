#!/bin/bash

# Copy environment configuration to backend
# Usage: ./copy-env.sh [dev|test|prod]

set -e

ENV=${1:-dev}

if [[ ! "$ENV" =~ ^(dev|test|prod)$ ]]; then
    echo "Error: Invalid environment. Use: dev, test, or prod"
    exit 1
fi

SOURCE="docker/$ENV/.env"
DEST="backend/.env"

# For production, use .env if it exists, otherwise .env.example
if [ "$ENV" = "prod" ]; then
    if [ -f "docker/prod/.env" ]; then
        SOURCE="docker/prod/.env"
    else
        SOURCE="docker/prod/.env.example"
        echo "⚠️  Warning: Using .env.example for production. Create docker/prod/.env with real secrets!"
    fi
fi

if [ ! -f "$SOURCE" ]; then
    echo "Error: Source file $SOURCE not found"
    exit 1
fi

cp "$SOURCE" "$DEST"
echo "✅ Copied $SOURCE to $DEST"
echo ""
echo "Spring Boot will now use these environment variables:"
echo "  - DATABASE_HOST_PORT"
echo "  - REDIS_HOST, REDIS_PORT, REDIS_PASSWORD"
echo "  - MAIL_HOST, MAIL_PORT"
echo "  - OSS_ACCESS_KEY, OSS_END_POINT, OSS_BUCKET_NAME"
echo "  - JWT_SECRET, JWT_EXPIRATION_DAYS"
