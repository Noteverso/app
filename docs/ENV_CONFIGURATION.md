# Environment Configuration Integration

## Overview

The Docker environment configuration is now automatically integrated with the Spring Boot backend application through `.env` files.

## Configuration Files

### Development Environment

1. **`.env`** - For local backend + Docker services
   - Uses `localhost` for service connections
   - Copied to `backend/.env` automatically
   - Used when running backend with `./mvnw spring-boot:run`

2. **`.env.docker`** - For full Docker stack
   - Uses Docker service names (e.g., `postgres`, `redis`)
   - Used by `docker compose.full.yml`
   - Used when running everything in Docker

### Key Differences

| Configuration | PostgreSQL | Redis | MinIO | Use Case |
|--------------|------------|-------|-------|----------|
| `.env` | localhost:5432 | localhost:6379 | http://localhost:9000 | Local backend + Docker services |
| `.env.docker` | postgres:5432 | redis:6379 | http://minio:9000 | Full Docker stack |

## Automatic Integration

### Using docker-manager.sh

```bash
./docker/docker-manager.sh dev up
```

This automatically:
1. Starts Docker services
2. Copies `docker/dev/.env` to `backend/.env` (for dev and prod only)
3. Backend can now connect to Docker services

**Note**: Test environment does not copy `.env` file since tests use `application.properties`.

### Using copy-env.sh

```bash
./docker/copy-env.sh dev
```

Standalone script to copy environment configuration to backend.

## Spring Boot Configuration

The backend uses different configuration approaches for different contexts:

### Main Application (application.yml)

The main application loads configuration from `.env` file:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

This tells Spring Boot to load environment variables from the `.env` file in the backend directory.

### Test Configuration (application.properties)

**Important**: Unit tests use a separate configuration file that connects to the Docker test environment.

- **Location**: `backend/noteverso-core/src/test/resources/application.properties`
- **Database**: Docker test database (localhost:5433/noteverso_test)
- **Redis**: Docker test Redis (localhost:6380)
- **MinIO**: Docker test MinIO (localhost:9002)
- **Independent**: Does not read from `.env` file
- **Requires**: Docker test environment running

Example test configuration:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/noteverso_test
spring.datasource.username=noteverso_test
spring.datasource.password=noteverso_test_pass
spring.data.redis.host=localhost
spring.data.redis.port=6380
```

### Configuration Priority

1. **Main Application**: Uses `.env` file (copied from docker environment)
2. **Unit Tests**: Uses `src/test/resources/application.properties` (connects to Docker test environment)
3. **Integration Tests**: Would use test-specific configuration or test containers

## Workflow Examples

### Development Workflow (Recommended)

```bash
# 1. Start Docker services
cd docker/dev
docker compose up -d

# 2. Environment is automatically copied (if using docker-manager.sh)
# Or manually:
cd ../..
./docker/copy-env.sh dev

# 3. Run backend locally
cd backend
./mvnw spring-boot:run

# 4. Run frontend locally
cd ../frontend/web
pnpm dev
```

### Full Docker Workflow

```bash
# Start everything in Docker
cd docker/dev
docker compose -f docker compose.full.yml up -d

# Access:
# - Frontend: http://localhost:3000
# - Backend: http://localhost:8081
```

## Environment Variables Used

The following variables are read from `.env`:

**Database**:
- `DATABASE_HOST_PORT`
- `DATABASE_DB`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

**Redis**:
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`

**Mail**:
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

**MinIO (S3)**:
- `OSS_ACCESS_KEY`
- `OSS_ACCESS_KEY_SECRET`
- `OSS_END_POINT`
- `OSS_BUCKET_NAME`
- `OSS_IS_HTTPS`

**JWT**:
- `JWT_SECRET`
- `JWT_EXPIRATION_DAYS`

## Security

- `backend/.env` is in `.gitignore` (not committed to git)
- Production secrets should be in `docker/prod/.env` (also in `.gitignore`)
- Use `docker/prod/.env.example` as a template

## Troubleshooting

### Backend can't connect to services

**Problem**: Connection refused errors

**Solution**: Ensure `.env` is copied to backend directory
```bash
./docker/copy-env.sh dev
```

### Wrong configuration loaded

**Problem**: Backend connects to wrong host

**Solution**: Check which `.env` file is in `backend/` directory
```bash
cat backend/.env | grep DATABASE_HOST_PORT
# Should show: localhost:5432 (for local backend)
# Or: postgres:5432 (for Docker backend)
```

### Environment variables not loaded

**Problem**: Spring Boot uses default values

**Solution**: Verify `.env` file exists and Spring Boot config is correct
```bash
ls -la backend/.env
cat backend/noteverso-core/src/main/resources/application.yml | grep "import:"
```

## Files Created/Modified

- `docker/dev/.env.docker` - Docker-specific configuration
- `docker/copy-env.sh` - Standalone copy script
- `docker/docker-manager.sh` - Enhanced with auto-copy
- `docker/README.md` - Updated documentation
- `docker/QUICKSTART.md` - Updated with .env info
