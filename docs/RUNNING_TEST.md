# RUNNING_TEST

Testing commands and environments.

## Backend Tests

Backend tests rely on Docker test services.

Start test infra:

```bash
cd docker/test
docker compose up -d
```

Initialize test schema (first time):

```bash
docker exec -i noteverso-postgres-test psql -U noteverso_test -d noteverso_test < backend/noteverso-core/src/main/resources/noteverso-pg.sql
```

Run tests from `backend/`:

```bash
./mvnw test
```

Run a specific class:

```bash
./mvnw test -Dtest=NoteControllerTest
```

## Frontend Unit/Component Tests

From project root:

```bash
pnpm web:test
```

From `frontend/web/`:

```bash
pnpm test
```

Run a specific test file:

```bash
pnpm test -- src/pages/shared-notes-page/shared-notes-page.test.ts
```

## Browser Automation (agent-browser)

Core flow:

1. Open target page.
2. Login if required.
3. Execute user actions.
4. Validate UI/API state.
5. Close session.

Use `test@gmail.com/Admin123456` for testing auth flows.

Example:

```bash
agent-browser --session qa open http://127.0.0.1:5173
agent-browser --session qa snapshot -i
agent-browser --session qa close
```

Always teardown sessions after tests:

```bash
agent-browser session list
agent-browser --session <name> close
```

