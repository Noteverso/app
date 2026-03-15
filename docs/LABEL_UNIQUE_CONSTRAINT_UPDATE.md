# Label Unique Constraint Update

**Date:** 2026-03-08  
**Task:** Add unique constraint on label name scoped per creator

## Overview

Modified the `noteverso_label` table to allow different users to create labels with the same name while preventing duplicate names within a single user's labels.

## Changes Made

### Database Schema

**Before:**
```sql
name varchar(60) NOT NULL CONSTRAINT noteverso_label_name_pk UNIQUE
```

**After:**
```sql
name varchar(60) NOT NULL,
CONSTRAINT noteverso_label_name_creator_uk UNIQUE (name, creator)
```

### Files Modified

1. `/root/personal/app/backend/noteverso-core/src/main/resources/noteverso-pg.sql`
2. `/root/personal/app/backend/noteverso-core/src/test/resources/db/schema.sql`

### Migration Steps

1. **Backup existing data** - 1 label preserved
2. **Drop old constraint** - `noteverso_label_name_pk`
3. **Add new constraint** - `noteverso_label_name_creator_uk UNIQUE (name, creator)`
4. **Apply to both databases** - dev and test environments

### Verification Results

- ✅ All unit tests pass (`LabelServiceTest`)
- ✅ Same user duplicate name → Correctly rejected
- ✅ Different users same name → Correctly allowed
- ✅ Existing data preserved
- ✅ No backend code changes required

## Backend Impact

**No code changes needed.** The existing exception handling in `LabelServiceImpl` automatically works with the new per-creator constraint scope:

```java
// createLabel() and updateLabel() methods
catch (Exception e) {
    if (e instanceof DuplicateKeyException) {
        throw new DuplicateRecordException(LABEL_NAME_DUPLICATE);
    }
}
```

---

# Docker Commands Reference

## Container Status Check

### Check if container is running
```bash
docker ps --filter "name=<container-name>" --format "{{.Names}}\t{{.Status}}"
```

**Examples:**
```bash
docker ps --filter "name=noteverso-postgres-dev" --format "{{.Names}}\t{{.Status}}"
docker ps --filter "name=noteverso-postgres-test" --format "{{.Names}}\t{{.Status}}"
```

**Output:**
```
noteverso-postgres-dev    Up 5 days (healthy)
```

## Database Operations

### Execute SQL Query

**Basic syntax:**
```bash
docker exec <container-name> psql -U <username> -d <database> -c "<SQL>"
```

### Common Operations

#### 1. Check for duplicate data
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "SELECT name, creator, COUNT(*) FROM noteverso_label GROUP BY name, creator HAVING COUNT(*) > 1;"
```

#### 2. Count records
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "SELECT COUNT(*) as label_count FROM noteverso_label;"
```

#### 3. View table data
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "SELECT * FROM noteverso_label;"
```

#### 4. Backup data to file
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "SELECT * FROM noteverso_label;" > /tmp/label_backup.txt
```

### Schema Modifications

#### Drop constraint
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "ALTER TABLE noteverso_label DROP CONSTRAINT IF EXISTS noteverso_label_name_pk;"
```

#### Add constraint
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "ALTER TABLE noteverso_label ADD CONSTRAINT noteverso_label_name_creator_uk UNIQUE (name, creator);"
```

#### Verify constraints
```bash
docker exec noteverso-postgres-dev psql -U noteverso -d noteverso_dev -c \
  "SELECT conname, contype, pg_get_constraintdef(oid) as definition 
   FROM pg_constraint 
   WHERE conrelid = 'noteverso_label'::regclass AND contype = 'u';"
```

**Output:**
```
             conname             | contype |       definition       
---------------------------------+---------+------------------------
 noteverso_label_pk              | u       | UNIQUE (label_id)
 noteverso_label_name_creator_uk | u       | UNIQUE (name, creator)
```

### Execute SQL from File

**Using stdin redirection:**
```bash
docker exec -i <container-name> psql -U <username> -d <database> < script.sql
```

**Capture errors:**
```bash
docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < test.sql 2>&1
```

### Multi-statement SQL Script Example

```bash
cat > /tmp/test_constraint.sql << 'EOF'
-- Insert first label
INSERT INTO noteverso_label (label_id, name, color, creator, updater, added_at, updated_at, is_favorite)
VALUES ('test_label_1', 'TestLabel', '#ff0000', 'user1', 'user1', NOW(), NOW(), 0);

-- Try duplicate (should fail)
INSERT INTO noteverso_label (label_id, name, color, creator, updater, added_at, updated_at, is_favorite)
VALUES ('test_label_2', 'TestLabel', '#00ff00', 'user1', 'user1', NOW(), NOW(), 0);
EOF

docker exec -i noteverso-postgres-dev psql -U noteverso -d noteverso_dev < /tmp/test_constraint.sql 2>&1
```

**Expected output:**
```
INSERT 0 1
ERROR:  duplicate key value violates unique constraint "noteverso_label_name_creator_uk"
DETAIL:  Key (name, creator)=(TestLabel, user1) already exists.
```

## Environment Details

### Dev Database
- **Container:** `noteverso-postgres-dev`
- **User:** `noteverso`
- **Database:** `noteverso_dev`

### Test Database
- **Container:** `noteverso-postgres-test`
- **User:** `noteverso_test`
- **Database:** `noteverso_test`

## Docker Command Patterns

### 1. Health Check Pattern
```bash
docker ps --filter "name=<pattern>" --format "{{.Names}}\t{{.Status}}"
```
- Clean, parseable output
- Filter by container name pattern
- Show only relevant columns

### 2. Interactive SQL Pattern
```bash
docker exec -i <container> psql -U <user> -d <db> < script.sql
```
- `-i` flag enables stdin
- Pipe SQL scripts directly
- Capture output with redirection

### 3. Error Capture Pattern
```bash
docker exec <container> psql ... 2>&1
```
- Redirect stderr to stdout
- See both results and errors
- Useful for testing constraint violations

### 4. Inline SQL Pattern
```bash
docker exec <container> psql -U <user> -d <db> -c "<SQL>"
```
- Quick one-off queries
- No file creation needed
- Immediate results

## Best Practices

1. **Always backup data** before schema changes
2. **Test constraints** with sample data before production
3. **Verify changes** by querying system tables
4. **Use transactions** for complex migrations (BEGIN/COMMIT/ROLLBACK)
5. **Apply to all environments** (dev, test, prod) consistently
6. **Document changes** with before/after examples

## Related Files

- Schema: `backend/noteverso-core/src/main/resources/noteverso-pg.sql`
- Test Schema: `backend/noteverso-core/src/test/resources/db/schema.sql`
- Service: `backend/noteverso-core/src/main/java/com/noteverso/core/service/impl/LabelServiceImpl.java`
- Tests: `backend/noteverso-core/src/test/java/com/noteverso/core/service/LabelServiceTest.java`
