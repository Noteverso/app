# RUNNING

Project startup commands for daily development.

## Prerequisites

- Node.js + pnpm
- JDK + Maven
- Docker (for local dependencies: PostgreSQL/Redis/MinIO/Mailpit)

## Start Infrastructure (Docker)

```bash
cd docker/dev
docker compose up -d
```

Initialize database schema (first setup or schema reset):

```bash
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

Sync env for backend:

```bash
cp docker/dev/.env backend/.env
```

## Start Backend

From `backend/`:

```bash
./mvnw spring-boot:run -pl noteverso-core
```

Optional full build:

```bash
./mvnw clean install -DskipTests
```

## Start Frontend

From project root:

```bash
pnpm web:install
pnpm web:dev
```

Or from `frontend/web/`:

```bash
pnpm dev
```

## Quick Health Checks

```bash
curl -I http://127.0.0.1:5173
curl -I http://127.0.0.1:8081
```

