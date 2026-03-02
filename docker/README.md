# Docker Environment Setup

This directory contains Docker Compose configurations for three environments: development, test, and production.

## Services

Each environment includes:
- **PostgreSQL 15** - Database
- **Redis 7** - Cache and session storage
- **MinIO** - S3-compatible object storage
- **Mailpit** - Email testing tool (SMTP server + web UI)

## Environments

### Development (`docker/dev`)
- Ports: PostgreSQL (5432), Redis (6379), MinIO (9000, 9001), Mailpit (1025, 8025)
- Credentials: Simple passwords for local development
- Data: Persisted in Docker volumes

### Test (`docker/test`)
- Ports: PostgreSQL (5433), Redis (6380), MinIO (9002, 9003), Mailpit (1026, 8026)
- Credentials: Test-specific passwords
- Data: Persisted in separate Docker volumes
- Isolated from dev environment
- **Note**: Unit tests use `backend/noteverso-core/src/test/resources/application.properties` and require Docker test environment running

### Production (`docker/prod`)
- Ports: PostgreSQL (5432), Redis (6379), MinIO (9000, 9001), Mailpit (1025, 8025)
- Credentials: **Must be changed** - uses environment variables
- Data: Persisted with restart policies
- Health checks enabled

## Quick Start

### Development Environment

```bash
# Start all services
cd docker/dev
docker compose up -d

# The .env file is automatically copied to backend/ directory (for dev/prod only)
# Test environment does not copy .env (uses application.properties)
# This allows Spring Boot to read the configuration

# View logs
docker compose logs -f

# Stop services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

**Note**: When using the `docker-manager.sh` script, the `.env` file is automatically copied to the `backend/` directory for Spring Boot configuration (dev and prod environments only). Test environment does not copy `.env` since tests use `application.properties`.

### Test Environment

```bash
cd docker/test
docker compose up -d
```

### Production Environment

```bash
cd docker/prod

# Copy and configure environment file
cp .env.example .env
# Edit .env and change all passwords and secrets!

# Start services
docker compose up -d
```

## Service Access

### Development

| Service | URL | Credentials |
|---------|-----|-------------|
| PostgreSQL | localhost:5432 | noteverso / noteverso_dev_pass |
| Redis | localhost:6379 | Password: noteverso_redis_dev |
| MinIO Console | http://localhost:9001 | noteverso_minio / noteverso_minio_dev_pass |
| MinIO API | http://localhost:9000 | Same as console |
| Mailpit UI | http://localhost:8025 | No auth |
| Mailpit SMTP | localhost:1025 | No auth |

### Test

| Service | URL | Credentials |
|---------|-----|-------------|
| PostgreSQL | localhost:5433 | noteverso_test / noteverso_test_pass |
| Redis | localhost:6380 | Password: noteverso_redis_test |
| MinIO Console | http://localhost:9003 | noteverso_test / noteverso_minio_test_pass |
| MinIO API | http://localhost:9002 | Same as console |
| Mailpit UI | http://localhost:8026 | No auth |
| Mailpit SMTP | localhost:1026 | No auth |

### Production

Use credentials from your `.env` file.

## Environment Files

Each environment has environment configuration files:

- `docker/dev/.env` - Development configuration (for local backend + Docker services)
- `docker/dev/.env.docker` - Development configuration (for full Docker stack)
- `docker/test/.env` - Test configuration
- `docker/prod/.env.example` - Production template (copy to `.env` and customize)

### Two Configuration Modes

#### Mode 1: Local Backend + Docker Services (Recommended for Development)

Use `.env` file which has `localhost` for service connections:

```bash
# Start Docker services only
cd docker/dev
docker compose up -d

# Copy .env to backend (done automatically by docker-manager.sh)
cp docker/dev/.env backend/.env

# Run backend locally
cd backend
./mvnw spring-boot:run
```

#### Mode 2: Full Docker Stack

Use `.env.docker` file which has Docker service names for connections:

```bash
# Start everything in Docker
cd docker/dev
docker compose -f docker compose.full.yml up -d
```

### Spring Boot Integration

The backend Spring Boot application uses different configurations for different contexts:

#### Main Application
The main application reads from the `.env` file:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

When using `docker-manager.sh`, the `.env` file is automatically copied to `backend/.env` (for dev and prod environments only).

#### Unit Tests
**Important**: Unit tests do **not** use the `.env` file. They use a separate configuration:

- **Location**: `backend/noteverso-core/src/test/resources/application.properties`
- **Database**: Docker test database (localhost:5433)
- **Services**: Connects to Docker test environment (Redis, MinIO)
- **Requires**: Docker test environment running

To run tests:
```bash
# Start Docker test environment
cd docker/test
docker compose up -d

# Run tests
cd ../../backend
./mvnw test
```
```

**Key Differences**:
- `.env` uses `localhost:5432` for PostgreSQL (local backend connects to Docker services)
- `.env.docker` uses `postgres:5432` (Docker backend connects to Docker services via service names)

When you start the Docker environment using `docker-manager.sh`, the `.env` file is automatically copied from `docker/{env}/.env` to `backend/.env`. This allows the Spring Boot application running locally to connect to Docker services.

**Manual copy** (if not using docker-manager.sh):
```bash
# For local backend development
./docker/copy-env.sh dev

# Or manually
cp docker/dev/.env backend/.env
```

The `.env` file in the backend directory is ignored by git (listed in `.gitignore`).

## MinIO Setup

After starting MinIO, create the bucket:

```bash
# Using MinIO Client (mc)
mc alias set local http://localhost:9000 noteverso_minio noteverso_minio_dev_pass
mc mb local/noteverso-dev

# Or use the web console at http://localhost:9001
```

## Database Initialization

The PostgreSQL container will automatically create the database specified in the environment variables. You'll need to run the schema initialization SQL separately:

```bash
# Development
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backend/noteverso-core/src/main/resources/noteverso-pg.sql

# Test
docker exec -i noteverso-postgres-test psql -U noteverso_test -d noteverso_test < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

## Health Checks

All services include health checks. Check status:

```bash
docker compose ps
```

## Troubleshooting

### Port conflicts
If ports are already in use, modify the port mappings in `docker compose.yml`:
```yaml
ports:
  - "5433:5432"  # Change 5433 to another port
```

### Reset everything
```bash
docker compose down -v
docker compose up -d
```

### View logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f postgres
```

### Connect to PostgreSQL
```bash
docker exec -it noteverso-postgres-dev psql -U noteverso -d noteverso_dev
```

### Connect to Redis
```bash
docker exec -it noteverso-redis-dev redis-cli -a noteverso_redis_dev
```

## Production Notes

⚠️ **Before deploying to production:**

1. **Change all passwords** in `.env`
2. **Generate strong JWT secret** (min 256 bits)
3. **Use real SMTP server** instead of Mailpit
4. **Enable HTTPS** for MinIO (set `OSS_IS_HTTPS=Y`)
5. **Configure backups** for PostgreSQL and MinIO volumes
6. **Set up monitoring** and alerting
7. **Review security settings** for all services
8. **Use secrets management** (e.g., Docker secrets, Vault)

## Backup and Restore

### PostgreSQL Backup
```bash
docker exec noteverso-postgres-dev pg_dump -U noteverso noteverso_dev > backup.sql
```

### PostgreSQL Restore
```bash
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backup.sql
```

### MinIO Backup
Use MinIO Client (mc) or backup the Docker volume directly.

## CI/CD Integration

These Docker Compose files can be used in CI/CD pipelines:

```yaml
# GitHub Actions example
- name: Start test environment
  run: |
    cd docker/test
    docker compose up -d
    docker compose ps
```
