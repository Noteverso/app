#!/bin/bash

# Noteverso Docker Environment Manager
# Usage: ./docker-manager.sh [dev|test|prod] [up|down|restart|logs|ps|clean]

set -e

ENV=${1:-dev}
ACTION=${2:-up}

if [[ ! "$ENV" =~ ^(dev|test|prod)$ ]]; then
    echo "Error: Invalid environment. Use: dev, test, or prod"
    exit 1
fi

if [[ ! "$ACTION" =~ ^(up|down|restart|logs|ps|clean|init)$ ]]; then
    echo "Error: Invalid action. Use: up, down, restart, logs, ps, clean, init"
    exit 1
fi

DOCKER_DIR="$ENV"

if [ ! -d "$DOCKER_DIR" ]; then
    echo "Error: Directory $DOCKER_DIR not found"
    exit 1
fi

cd "$DOCKER_DIR"

case $ACTION in
    up)
        echo "Starting $ENV environment..."
        
        # Copy .env to backend directory (only for dev and prod, not test)
        if [ "$ENV" != "test" ] && [ -f ".env" ]; then
            echo "Copying .env to backend directory..."
            cp .env ../../backend/.env
            echo "✅ Environment file copied to backend"
        fi
        
        docker compose up -d
        echo "Waiting for services to be healthy..."
        sleep 5
        docker compose ps
        echo ""
        echo "✅ $ENV environment is running"
        echo ""
        if [ "$ENV" = "dev" ]; then
            echo "Access services at:"
            echo "  PostgreSQL: localhost:5432"
            echo "  Redis: localhost:6379"
            echo "  MinIO Console: http://localhost:9001"
            echo "  Mailpit UI: http://localhost:8025"
        elif [ "$ENV" = "test" ]; then
            echo "Access services at:"
            echo "  PostgreSQL: localhost:5433"
            echo "  Redis: localhost:6380"
            echo "  MinIO Console: http://localhost:9003"
            echo "  Mailpit UI: http://localhost:8026"
            echo ""
            echo "ℹ️  Test environment uses application.properties (not .env)"
        fi
        ;;
    down)
        echo "Stopping $ENV environment..."
        docker compose down
        echo "✅ $ENV environment stopped"
        ;;
    restart)
        echo "Restarting $ENV environment..."
        docker compose restart
        echo "✅ $ENV environment restarted"
        ;;
    logs)
        docker compose logs -f
        ;;
    ps)
        docker compose ps
        ;;
    clean)
        echo "⚠️  This will remove all data volumes for $ENV environment!"
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker compose down -v
            echo "✅ $ENV environment cleaned"
        else
            echo "Cancelled"
        fi
        ;;
    init)
        echo "Initializing database for $ENV environment..."
        if [ "$ENV" = "dev" ]; then
            CONTAINER="noteverso-postgres-dev"
            DB_USER="noteverso"
            DB_NAME="noteverso_dev"
        elif [ "$ENV" = "test" ]; then
            CONTAINER="noteverso-postgres-test"
            DB_USER="noteverso_test"
            DB_NAME="noteverso_test"
        else
            CONTAINER="noteverso-postgres-prod"
            DB_USER="noteverso_prod"
            DB_NAME="noteverso_prod"
        fi
        
        SQL_FILE="../../backend/noteverso-core/src/main/resources/noteverso-pg.sql"
        if [ -f "$SQL_FILE" ]; then
            docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < "$SQL_FILE"
            echo "✅ Database initialized"
        else
            echo "Error: SQL file not found at $SQL_FILE"
            exit 1
        fi
        ;;
esac
