# Docker Manager Script Update - Test Environment

## Change Made

Updated `docker/docker-manager.sh` to skip copying `.env` file when starting the test environment.

## Reason

Test environment uses `backend/noteverso-core/src/test/resources/application.properties` for configuration, not the `.env` file. Copying `.env` for test environment is unnecessary and could cause confusion.

## Implementation

### Before
```bash
# Copy .env to backend directory
if [ -f ".env" ]; then
    echo "Copying .env to backend directory..."
    cp .env ../../backend/.env
    echo "✅ Environment file copied to backend"
fi
```

### After
```bash
# Copy .env to backend directory (only for dev and prod, not test)
if [ "$ENV" != "test" ] && [ -f ".env" ]; then
    echo "Copying .env to backend directory..."
    cp .env ../../backend/.env
    echo "✅ Environment file copied to backend"
fi
```

## Behavior

| Environment | .env Copied? | Configuration Used |
|-------------|--------------|-------------------|
| dev | ✅ Yes | `backend/.env` |
| test | ❌ No | `backend/noteverso-core/src/test/resources/application.properties` |
| prod | ✅ Yes | `backend/.env` |

## User Experience

When starting test environment:
```bash
./docker/docker-manager.sh test up
```

Output now includes:
```
Starting test environment...
✅ test environment is running

Access services at:
  PostgreSQL: localhost:5433
  Redis: localhost:6380
  MinIO Console: http://localhost:9003
  Mailpit UI: http://localhost:8026

ℹ️  Test environment uses application.properties (not .env)
```

## Documentation Updated

1. ✅ `docs/ENV_CONFIGURATION.md` - Added note about test environment
2. ✅ `docker/README.md` - Updated all references to .env copying (3 locations)

## Date: 2026-03-01
