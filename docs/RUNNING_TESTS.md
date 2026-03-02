# Quick Reference: Running Tests

## Prerequisites

Tests require Docker test environment to be running.

## Start Test Environment

```bash
cd docker/test
docker compose up -d
```

This starts:
- PostgreSQL on port 5433 (database: noteverso_test)
- Redis on port 6380
- MinIO on port 9002
- Mailpit on port 1026

## Initialize Database Schema

**Required on first run or after cleaning volumes:**

```bash
docker exec -i noteverso-postgres-test psql -U noteverso_test -d noteverso_test < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

Verify tables were created:
```bash
docker exec noteverso-postgres-test psql -U noteverso_test -d noteverso_test -c "\dt"
```

## Run Tests

```bash
cd backend
./mvnw test
```

## Run Specific Test

```bash
cd backend
./mvnw test -Dtest=AttachmentServiceTest
./mvnw test -Dtest=NoteControllerTest
```

## Run with Coverage

```bash
cd backend
./mvnw test jacoco:report
```

## Stop Test Environment

```bash
cd docker/test
docker compose down
```

## Clean Test Data

```bash
cd docker/test
docker compose down -v  # Removes volumes (requires schema re-initialization)
```

## Test Configuration

Tests use: `backend/noteverso-core/src/test/resources/application.properties`

Key settings:
- Database: `jdbc:postgresql://localhost:5433/noteverso_test`
- Redis: `localhost:6380`
- MinIO: `http://localhost:9002`
- Mail: `localhost:1026`
- JWT: `noteverso.jwt-secret` and `noteverso.jwt-expiration-days`

## Test Architecture

### Unit Tests (Service Layer)
- Use `@ExtendWith(MockitoExtension.class)`
- Mock all dependencies with `@Mock`
- Fast execution, no Spring context

### Integration Tests (DAO Layer)
- Use `@MybatisPlusTest` or `@SpringBootTest`
- Connect to real test database
- Test actual SQL queries

### Controller Tests
- Use `@ExtendWith(MockitoExtension.class)` with `MockMvcBuilders.standaloneSetup()`
- Mock service dependencies
- Avoids loading full Spring context (faster than `@WebMvcTest`)
- Set up authentication in `@BeforeEach`

## Troubleshooting

### Tests fail with connection errors

**Problem**: Cannot connect to database/Redis/MinIO

**Solution**: Ensure Docker test environment is running
```bash
cd docker/test
docker compose ps  # Check if services are running
docker compose up -d  # Start if not running
```

### Port conflicts

**Problem**: Port 5433/6380/9002 already in use

**Solution**: Stop conflicting services or change ports in `docker/test/.env`

### Database schema issues

**Problem**: Tests fail with "relation does not exist"

**Solution**: Initialize database schema (see above)
```bash
docker exec -i noteverso-postgres-test psql -U noteverso_test -d noteverso_test < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

### Missing environment variables

**Problem**: Tests fail with "Could not resolve placeholder"

**Solution**: Check `application.properties` has all required properties:
- `noteverso.jwt-secret`
- `noteverso.jwt-expiration-days`
- `MAIL_HOST`, `MAIL_PORT`, etc.

## Date: 2026-03-02
