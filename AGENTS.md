# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Esentially
1. Before writing any code, describe your approach and wait for approval.
2. Before kill any process or port, check if it's running and wait for approval.
3. If the requirements I give you are ambiguous, ask clarifying questions before writing any code.
4. After you finish writing any code, list the edge cases and suggest test cases to cover them.
5. If a task requires changes to more than 3 files, stop and break it into smaller tasks first.
6. Every time I correct you, reflect on what you did wrong and come up with a plan to never make the same mistake again.
7. Unit tests should find errors, not hide them. When tests fail, we should:
  1. Investigate the root cause
  2. Fix the bug
  3. Keep the test to prevent regression

## Empty Parameter Handling Pattern

**Standardized approach for handling empty/null collections:**

### Service Layer (Primary Validation)
- **MUST validate** before calling mapper for INSERT operations (prevents SQL errors)
- **SHOULD validate** for SELECT operations (performance optimization - avoids unnecessary DB calls)
- Pattern: `if (collection == null || collection.isEmpty()) return emptyResult;`
- Return empty collections/maps, never null or exceptions

### Mapper Layer (Defensive SQL)
- All SELECT methods with collection parameters use conditional checks
- Pattern for empty collection handling:
  ```xml
  <if test="collection == null or collection.size() == 0">
      AND 1 = 0
  </if>
  ```
- INSERT methods assume non-empty (service validates first)

### Testing Requirements
- All methods accepting collections MUST have tests for:
  - Empty list parameter
  - Null parameter
- INSERT methods: Verify mapper is not called when empty
- SELECT methods: Verify empty result is returned

### Examples
```java
// Service layer - SELECT method
public HashMap<String, Long> getCounts(List<String> ids, String userId) {
    if (ids == null || ids.isEmpty()) {
        return new HashMap<>();
    }
    return mapper.getCounts(ids, userId);
}

// Service layer - INSERT method
public void batchInsert(List<Item> items, String userId) {
    if (items == null || items.isEmpty()) {
        return;
    }
    mapper.batchInsert(items);
}
```

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

### Frontend Tests
#### Browser Automation

Use `agent-browser` for web automation. Run `agent-browser --help` for all commands.

Core workflow:
1. `agent-browser open <url>` - Navigate to page
2. `agent-browser snapshot -i` - Get interactive elements with refs (@e1, @e2)
3. `agent-browser click @e1` / `fill @e2 "text"` - Interact using refs
4. Re-snapshot after page changes