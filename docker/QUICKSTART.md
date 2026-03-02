# Docker Quick Start Guide

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+

## Development Setup (5 minutes)

### 1. Start Infrastructure Services

```bash
cd docker/dev
docker compose up -d
```

This starts: PostgreSQL, Redis, MinIO, Mailpit

### 2. Initialize Database

```bash
# Using the manager script
cd docker
./docker-manager.sh dev init

# Or manually
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

### 3. Create MinIO Bucket

Visit http://localhost:9001
- Login: `noteverso_minio` / `noteverso_minio_dev_pass`
- Create bucket: `noteverso-dev`

### 4. Start Backend (Local)

The backend automatically reads configuration from `.env` file in the backend directory.

```bash
# The docker-manager script automatically copies .env to backend/
# Or copy manually:
cp docker/dev/.env backend/.env

cd backend
./mvnw spring-boot:run
```

Backend runs on http://localhost:8080

### 5. Start Frontend (Local)

```bash
cd frontend/web
pnpm install
pnpm dev
```

Frontend runs on http://localhost:5173

## Full Stack with Docker

```bash
cd docker/dev
docker compose -f docker compose.full.yml up -d
```

Access:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Mailpit: http://localhost:8025
- MinIO: http://localhost:9001

## Useful Commands

```bash
# View logs
docker compose logs -f

# Stop services
docker compose down

# Clean everything (removes data!)
docker compose down -v

# Check service status
docker compose ps

# Restart a service
docker compose restart postgres
```

## Using the Manager Script

```bash
# Start dev environment
./docker-manager.sh dev up

# View logs
./docker-manager.sh dev logs

# Stop environment
./docker-manager.sh dev down

# Initialize database
./docker-manager.sh dev init

# Clean all data (with confirmation)
./docker-manager.sh dev clean
```

## Test Environment

```bash
# Start test environment (different ports)
cd docker/test
docker compose up -d

# Or with script
./docker-manager.sh test up
```

## Troubleshooting

### Port already in use
```bash
# Check what's using the port
lsof -i :5432

# Change port in docker compose.yml
ports:
  - "5433:5432"  # Use 5433 instead
```

### Services not healthy
```bash
# Check logs
docker compose logs postgres

# Restart service
docker compose restart postgres
```

### Database connection refused
```bash
# Wait for PostgreSQL to be ready
docker compose ps

# Check health status
docker inspect noteverso-postgres-dev | grep Health
```

## Next Steps

1. ✅ Services running
2. ✅ Database initialized
3. ✅ MinIO bucket created
4. 🚀 Start developing!

See `docker/README.md` for detailed documentation.
