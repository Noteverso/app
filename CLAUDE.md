# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Backend

```bash
# From backend/ directory
./mvnw clean install          # Build all modules
./mvnw spring-boot:run        # Run application (from noteverso-core/)
./mvnw test                   # Run all tests
./mvnw test -Dtest=ClassName  # Run specific test class
./mvnw jacoco:report          # Generate test coverage report

# Restart backend (rebuild + restart)
cd /root/personal/app/backend && mvn clean package -DskipTests -Dmaven.test.skip=true && pkill -f "noteverso-core" && sleep 2 && nohup ./start.sh > backend.log 2>&1 & sleep 10 && tail -n 20 backend.log
```

### Frontend

```bash
# From project root (uses pnpm workspace)
pnpm web:install              # Install dependencies
pnpm web:dev                  # Start dev server (with --host)
pnpm web:build                # Build for production
pnpm web:test                 # Run tests

# From frontend/web/ directory
pnpm dev                      # Start dev server
pnpm build                    # TypeScript compile + Vite build
pnpm lint                     # Run ESLint
pnpm lint:fix                 # Fix ESLint issues
```

## Docker Environment

Prefer `docker compose` command (not `docker-compose`).

### Quick Start Development

```bash
# Start Docker services (PostgreSQL, Redis, MinIO, Mailpit)
cd docker/dev
docker compose up -d

# Initialize database schema
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backend/noteverso-core/src/main/resources/noteverso-pg.sql

# The .env file should be copied to backend/ for Spring Boot
cp docker/dev/.env backend/.env

# Run backend locally
cd backend
./mvnw spring-boot:run

# Run frontend (in another terminal)
pnpm web:dev
```

### Service Access

See `docker/README.md` for detailed Docker documentation.

## Configuration

### Backend Configuration

Spring Boot uses environment variables loaded from `.env` file
Configuration file: `backend/noteverso-core/src/main/resources/application.yml`

### Frontend Configuration

Vite configuration in `frontend/web/vite.config.ts`. API base URL should be configured for different environments.

## Testing

### Backend Tests

Tests require Docker test environment. See `docs/RUNNING_TESTS.md` for complete guide.

**Quick Start:**
```bash
# Start test environment
cd docker/test && docker compose up -d

# Initialize schema (first time only)
docker exec -i noteverso-postgres-test psql -U noteverso_test -d noteverso_test < backend/noteverso-core/src/main/resources/noteverso-pg.sql

# Run tests
cd backend && ./mvnw test
```

**Test Architecture:**
- Unit tests: `@ExtendWith(MockitoExtension.class)` - Fast, no Spring context
- Integration tests: `@SpringBootTest` or `@MybatisPlusTest` - Real database
- Controller tests: Standalone MockMvc setup - Faster than `@WebMvcTest`

Run specific test:
```bash
cd backend
./mvnw test -Dtest=NoteControllerTest
```