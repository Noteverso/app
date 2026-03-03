# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Esentially
1. Before writing any code, describe your approach and wait for approval.
2. Before kill any process or port, check if it's running and wait for approval.
3. If the requirements I give you are ambiguous, ask clarifying questions before writing any code.
4. After you finish writing any code, list the edge cases and suggest test cases to cover them.
5. If a task requires changes to more than 3 files, stop and break it into smaller tasks first.
6. Every time I correct you, reflect on what you did wrong and come up with a plan to never make the same mistake again.


## Development Commands

### Backend

```bash
# From backend/ directory
./mvnw clean install -DskipTests          # Build all modules
# Set version for all modules (if needed)
mvn clean install -DskipTests -Drevision=0.0.1
./mvnw spring-boot:run -pl noteverso-core       # Run application (from noteverso-core/)
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
See `docker/README.md` for detailed Docker documentation.

### Quick Start Development

```bash
# Start Docker services (PostgreSQL, Redis, MinIO, Mailpit)
cd docker/dev
docker compose up -d

# Initialize database schema
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backend/noteverso-core/src/main/resources/noteverso-pg.sql

# The .env file should be copied to backend/ for Spring Boot
cp docker/dev/.env backend/.env
```

## Configuration

- Spring Boot uses environment variables loaded from `.env` file
- Vite API base URL should be configured for different environments.

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

# Run specific test:
cd backend && ./mvnw test -Dtest=NoteControllerTest
```

**Test Architecture:**
- Unit tests: `@ExtendWith(MockitoExtension.class)` - Fast, no Spring context
- Integration tests: `@SpringBootTest` or `@MybatisPlusTest` - Real database
- Controller tests: Standalone MockMvc setup - Faster than `@WebMvcTest`

## Browser Automation

Use `agent-browser` for web automation. Run `agent-browser --help` for all commands.

Core workflow:
1. `agent-browser open <url>` - Navigate to page
2. `agent-browser snapshot -i` - Get interactive elements with refs (@e1, @e2)
3. `agent-browser click @e1` / `fill @e2 "text"` - Interact using refs
4. Re-snapshot after page changes