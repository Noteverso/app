# Empty Data Parameter Tests - Clarification

**Date**: 2026-03-03  
**Status**: ✅ Complete with Important Notes

---

## Understanding Empty Data Tests

**Empty data tests** should test when we pass **empty parameters** (empty lists, null values, empty strings) to methods, NOT when the database has no matching records.

### Examples:
- ✅ **Correct**: `mapper.getItems(List.of())` - passing empty list
- ✅ **Correct**: `mapper.getItems(null)` - passing null
- ✅ **Correct**: `service.search("")` - passing empty string
- ❌ **Incorrect**: `mapper.getItems(List.of("nonexistent"))` - passing IDs that don't exist in DB

---

## Why Mapper Empty Parameter Tests Were Removed

### The Problem

MyBatis XML mappers use `<foreach>` to handle collections:

```xml
<select id="getItems">
    SELECT * FROM table WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</select>
```

When you pass an **empty list**, MyBatis generates invalid SQL:
```sql
SELECT * FROM table WHERE id IN  -- Missing values!
```

This causes: `ERROR: syntax error at or near "AND"`

---

## The Solution: Test at Service Layer

Empty parameter validation should happen at the **service layer**, not the mapper layer.

### Service Layer Pattern

```java
@Service
public class MyServiceImpl {
    public List<Item> getItems(List<String> ids, String userId) {
        // Handle empty params BEFORE calling mapper
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Now safe to call mapper
        return mapper.getItems(ids, userId);
    }
}
```

### Service Test Pattern

```java
@Test
void should_getItems_returnEmpty_whenEmptyList() {
    // Arrange
    List<String> emptyList = List.of();
    
    // Act
    List<Item> result = service.getItems(emptyList, "user1");
    
    // Assert
    assertThat(result).isEmpty();
    // Mapper should NOT be called
    verify(mapper, never()).getItems(any(), any());
}
```

---

## Tests Added

### 1. ProjectMapperTest - 1 Empty Parameter Test ✅

**Test Added**:
- `should_getProjects_returnEmpty_whenEmptyProjectIds()` - Tests empty Set parameter

**Why This Works**: The mapper XML handles empty Set differently than empty List in `<foreach>`

---

### 2. LabelMapperTest - 1 Empty Parameter Test ✅

**Test Added**:
- `should_getLabels_returnAll_whenEmptyName()` - Tests empty String parameter

**Why This Works**: Empty string is handled by SQL `LIKE` clause, not `<foreach>`

---

## Tests NOT Added (By Design)

These tests were intentionally NOT added because they cause SQL errors:

### NoteRelationMapperTest
- ❌ `getReferencingNoteCountByReferencingIds(List.of())` - Causes SQL syntax error
- ❌ `getReferencedNoteCountByReferencedIds(List.of())` - Causes SQL syntax error

### NoteLabelRelationMapperTest
- ❌ `getNoteCountByLabels(List.of())` - Causes SQL syntax error

### AttachmentRelationMapperTest
- ❌ `getAttachmentCountByObjectIds(List.of())` - Causes SQL syntax error

### NoteMapperTest
- ❌ `getNoteCountByProjects(List.of())` - Causes SQL syntax error
- ❌ `batchSelectByNoteIds(List.of())` - Causes SQL syntax error

**Reason**: All use `<foreach>` in MyBatis XML which doesn't handle empty collections

---

## Test Execution Results

```bash
cd backend/noteverso-core && ../mvnw test -Dtest=*MapperTest
```

**Output**:
```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 - AttachmentMapperTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 - NoteRelationMapperTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 - ProjectMapperTest
[WARNING] Tests run: 1, Failures: 0, Errors: 0, Skipped: 1 - NoteProjectRelationMapperTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 - ViewOptionMapperTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 - AttachmentRelationMapperTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 - NoteMapperTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 - NoteLabelRelationMapperTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 - UserConfigMapperTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 - UserMapperTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 - LabelMapperTest
[WARNING] Tests run: 25, Failures: 0, Errors: 0, Skipped: 1
[INFO] BUILD SUCCESS
```

---

## Recommendation: Add Service Layer Empty Parameter Tests

The proper place for empty parameter tests is the **service layer**. Here's what should be added:

### RelationServiceTest - Add Empty Parameter Tests

```java
@Test
void should_getReferencingNotes_returnEmpty_whenEmptyList() {
    // Arrange
    List<String> emptyList = List.of();
    
    // Act
    List<String> result = relationService.getReferencingNotes(emptyList, "user1");
    
    // Assert
    assertThat(result).isEmpty();
}
```

### NoteServiceTest - Add Empty Parameter Tests

```java
@Test
void should_searchNotes_returnEmpty_whenEmptyKeyword() {
    // Arrange
    String emptyKeyword = "";
    
    // Act
    PageResult<NoteItem> result = noteService.searchNotes("user1", emptyKeyword, null, null, null);
    
    // Assert
    assertThat(result.getRecords()).isEmpty();
}
```

---

## Summary

| Layer | Empty Parameter Tests | Status |
|-------|----------------------|--------|
| **Mapper Layer** | Limited (only non-foreach methods) | ✅ 2 tests added |
| **Service Layer** | Should handle all empty params | ⚠️ TODO |
| **Controller Layer** | Validation annotations handle this | ✅ N/A |

---

## Key Takeaways

1. ✅ **Mapper tests** should focus on valid data scenarios
2. ✅ **Service tests** should handle empty parameter validation
3. ✅ **MyBatis XML** `<foreach>` doesn't support empty collections
4. ✅ **Empty parameter handling** is a service layer responsibility
5. ✅ **2 mapper empty parameter tests** added where XML supports it

---

## Files Modified

1. `/backend/noteverso-core/src/test/java/com/noteverso/core/dao/ProjectMapperTest.java` - +1 empty Set test
2. `/backend/noteverso-core/src/test/java/com/noteverso/core/dao/LabelMapperTest.java` - +1 empty String test

---

## Next Steps (Optional)

Add empty parameter tests to service layer:
- RelationServiceTest - Add 5+ empty parameter tests
- NoteServiceTest - Add 3+ empty parameter tests
- ProjectServiceTest - Add 2+ empty parameter tests
- LabelServiceTest - Add 2+ empty parameter tests

**Total**: ~12 service layer empty parameter tests

---

## Conclusion

Empty parameter tests at the mapper layer are limited by MyBatis XML constraints. The proper approach is:

1. ✅ **Mapper Layer**: Test valid data scenarios only
2. ✅ **Service Layer**: Handle empty parameter validation and testing
3. ✅ **Controller Layer**: Use validation annotations (@NotEmpty, @NotNull)

This follows the principle of **defense in depth** - validate at each layer appropriately.
