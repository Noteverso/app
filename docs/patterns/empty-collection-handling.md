# Empty Collection Handling Pattern

Standard pattern for handling `null` / empty collections in layered backend code (Service + Mapper).

## Goal

- Prevent invalid SQL for collection-driven queries.
- Avoid unnecessary database calls for empty input.
- Keep return values predictable (empty collections/maps, not `null`).

## Layer Responsibilities

### Service Layer (Primary Validation)

- Must validate collection inputs before mapper calls for `INSERT`/batch-write operations.
- Should validate collection inputs for `SELECT` operations (performance and clarity).
- Return empty result objects directly when input is empty.

Pattern:

```java
if (ids == null || ids.isEmpty()) {
    return Collections.emptyList(); // or emptyMap/new HashMap<>()
}
```

### Mapper Layer (Defensive SQL)

- `SELECT` mapper methods that accept collections should include empty-collection guards in XML.
- For MyBatis `<foreach>`-based conditions, use an always-false clause when collection is empty.

Pattern:

```xml
<if test="collection == null or collection.size() == 0">
    AND 1 = 0
</if>
```

Notes:

- This prevents malformed `IN ()` SQL in edge paths.
- `INSERT` mappers assume non-empty input (service layer must guard first).

## Recommended Service Examples

### SELECT with Collection Input

```java
public HashMap<String, Long> getCounts(List<String> ids, String userId) {
    if (ids == null || ids.isEmpty()) {
        return new HashMap<>();
    }
    return mapper.getCounts(ids, userId);
}
```

### INSERT with Collection Input

```java
public void batchInsert(List<Item> items, String userId) {
    if (items == null || items.isEmpty()) {
        return;
    }
    mapper.batchInsert(items);
}
```

## Testing Requirements

For every public method that accepts a collection:

1. Empty list input test (`List.of()` / empty set).
2. `null` input test.

Expected assertions:

- `SELECT`: returns empty result object.
- `INSERT`: returns without invoking mapper.

Example assertion for write operations:

```java
verify(mapper, never()).batchInsert(any());
```

## Anti-Patterns

- Passing empty collections to mapper `<foreach>` queries without service guard.
- Returning `null` for empty-result cases.
- Swallowing SQL syntax errors caused by empty `IN` conditions instead of fixing validation.

## Quick Checklist

- Service method has `null/empty` guard.
- Mapper XML has defensive branch for collection-based `SELECT`.
- Tests cover both `null` and empty collection inputs.
- Write methods verify mapper not called on empty input.
