# Backend Unit Test Coverage Plan - COMPLETE ANALYSIS

## Executive Summary

**Critical Findings**:
1. **5 empty test methods** in RelationServiceTest
2. **2 completely empty test files** (UserServiceTest, ViewOptionServiceTest)
3. **Significant gaps** in existing service tests (only 24-60% coverage)
4. **17 missing test files** for managers, security, utilities, infrastructure, mappers

**Total Work Required**: ~100 new test cases across 17 new files + 50 missing tests in existing files

---

## Current Test Coverage (20 test files)

### ✅ Controllers (5/6 tested)
- ✅ AuthControllerTest
- ✅ FileControllerTest
- ✅ LabelControllerTest
- ✅ NoteControllerTest
- ✅ ProjectControllerTest
- ❌ **MailController** - MISSING TEST

### ⚠️ Services (9 files, but INCOMPLETE)

#### 🔴 CRITICAL: Empty or Broken Tests
- ❌ **UserServiceTest** - EMPTY FILE (0 tests, needs 3)
- ❌ **ViewOptionServiceTest** - EMPTY FILE (0 tests, needs 7)
- ⚠️ **RelationServiceTest** - 5 EMPTY TEST METHODS + 15 missing tests

#### 🟡 Partial Coverage (needs expansion)
- ⚠️ **AttachmentServiceTest** - 5 tests (needs 3 more)
- ⚠️ **LabelServiceTest** - 9 tests (needs 3 more)
- ⚠️ **NoteServiceTest** - 6 tests (needs 10 more)
- ⚠️ **ProjectServiceTest** - 12 tests (needs 8 more)

#### ✅ Good Coverage
- ✅ **NoteSearchServiceTest** - 5 tests (adequate)

#### ⚠️ Skip
- ⚠️ **EmailServiceImpl** - Skip (external mail dependency)

### ✅ DAOs/Mappers (6/11 tested)
- ✅ AttachmentRelationMapperTest
- ✅ LabelMapperTest
- ✅ NoteMapperTest
- ✅ NoteLabelRelationMapperTest
- ✅ NoteRelationMapperTest
- ✅ ProjectMapperTest
- ❌ **AttachmentMapper** - MISSING TEST
- ❌ **NoteProjectRelationMapper** - MISSING TEST
- ❌ **UserConfigMapper** - MISSING TEST
- ❌ **UserMapper** - MISSING TEST
- ❌ **ViewOptionMapper** - MISSING TEST

---

## Missing Test Coverage

### 🔴 PHASE 1: Fix Broken/Empty Tests (CRITICAL - 1 hour)

#### Complete Empty Service Tests (3 files)
1. **UserServiceTest** - Add 3 tests (createUser, existsByEmail, edge cases)
2. **ViewOptionServiceTest** - Add 7 tests (CRUD + getViewOptionsMap)
3. **RelationServiceTest** - Complete 5 empty test methods:
   - getAttachmentsByNoteId
   - getLabelsByNoteId
   - getAttachmentCountByObjectIds
   - getReferencedCountByReferencedNoteIds
   - getReferencingCountByReferencingNoteIds

**Estimated**: 15 new test cases

---

### 🟡 PHASE 2: Expand Service Test Coverage (3 hours)

#### Add Missing Tests to Existing Service Test Files
4. **RelationServiceTest** - Add 15 more tests for:
   - Insert operations (insertNoteLabelRelation, insertNoteAttachmentRelation, insertNoteRelation)
   - Update operations (updateNoteRelation, updateNoteLabelRelation, updateNoteAttachment)
   - Delete operations (deleteNoteRelation, deleteNoteLabelRelation, deleteNoteAttachmentRelation)
   - Get operations (getReferencingNotes, getReferencedNotes, getLabelsByNoteIds, etc.)

5. **NoteServiceTest** - Add 10 tests for:
   - createNote, updateNote, moveNoteToTrash, restoreNote
   - toggleArchive, toggleFavorite, togglePin, moveNote, deleteNote

6. **ProjectServiceTest** - Add 8 tests for:
   - archiveProject, unarchiveProject, favoriteProject, unFavoriteProject
   - constructProject, constructInboxProject, edge cases

7. **AttachmentServiceTest** - Add 3 tests for:
   - userAttachmentTotalSize, edge cases

8. **LabelServiceTest** - Add 3 tests for:
   - Edge cases and error scenarios

**Estimated**: 39 new test cases

---

### 🟢 PHASE 3: New Test Files - Controllers (15 minutes)
9. **MailControllerTest** - Mail endpoints (4-5 tests)

**Estimated**: 5 new test cases

---

### 🟢 PHASE 4: New Test Files - Managers (45 minutes)
10. **NoteManagerImplTest** - Note item construction, pagination (5 tests)
11. **UserConfigManagerImplTest** - User config, DTO construction (3 tests)
12. **AuthManagerImplTest** - Principal extraction (2 tests)

**Estimated**: 10 new test cases

---

### 🟢 PHASE 5: New Test Files - Security (45 minutes)
13. **JwtUtilsTest** - Token generation/validation (5 tests)
14. **UserDetailsServiceImplTest** - Load user by username (3 tests)
15. **UserDetailsImplTest** - User details methods (4 tests)

**Estimated**: 12 new test cases

---

### 🟢 PHASE 6: New Test Files - Utilities (30 minutes)
16. **RedisUtilsTest** - Redis operations (8 tests)
17. **DateFormatUtilsTest** - Date formatting (2 tests)

**Estimated**: 10 new test cases

---

### 🟢 PHASE 7: New Test Files - Infrastructure (30 minutes)
18. **OssClientTest** - S3/MinIO operations (5 tests)
19. **GlobalExceptionHandlerTest** - Exception handling (5 tests)

**Estimated**: 10 new test cases

---

### 🟢 PHASE 8: New Test Files - Missing Mappers (1 hour)
20. **AttachmentMapperTest** - Batch insert (2 tests)
21. **NoteProjectRelationMapperTest** - Relations (2 tests)
22. **UserConfigMapperTest** - Config queries (2 tests)
23. **UserMapperTest** - User queries (3 tests)
24. **ViewOptionMapperTest** - Batch queries (2 tests)

**Estimated**: 11 new test cases

---

## Total Work Required

### Existing Files (Expand/Fix)
- Fix empty tests: 15 test cases
- Expand coverage: 39 test cases
- **Subtotal**: 54 test cases in 8 existing files

### New Files
- Controllers: 5 test cases (1 file)
- Managers: 10 test cases (3 files)
- Security: 12 test cases (3 files)
- Utilities: 10 test cases (2 files)
- Infrastructure: 10 test cases (2 files)
- Mappers: 11 test cases (5 files)
- **Subtotal**: 58 test cases in 16 new files

### Grand Total
- **24 test files** (8 existing + 16 new)
- **~112 new test cases**
- **Estimated time**: ~7-8 hours

---

## Execution Priority

**PHASE 1** (1 hour): Fix broken tests - 15 cases  
**PHASE 2** (3 hours): Expand services - 39 cases  
**PHASE 3-8** (3 hours): New files - 58 cases  

**Total**: ~7-8 hours for 112 test cases

---

## Implementation Plan

### Phase 1: Complete Empty Service Tests (2 files)
**Priority**: CRITICAL - These files exist but are empty
- `UserServiceTest` - Add tests for createUser, existsByEmail
- `ViewOptionServiceTest` - Add tests for CRUD operations

### Phase 2: Missing Controller Test (1 file)
- `MailControllerTest` - Test mail sending endpoints

### Phase 3: Managers (3 files)
- `NoteManagerImplTest`
- `UserConfigManagerImplTest`
- `AuthManagerImplTest`

### Phase 4: Security (3 files)
- `JwtUtilsTest`
- `UserDetailsServiceImplTest`
- `UserDetailsImplTest`

### Phase 5: Utilities (2 files)
- `RedisUtilsTest`
- `DateFormatUtilsTest`

### Phase 6: Infrastructure (2 files)
- `OssClientTest`
- `GlobalExceptionHandlerTest`

### Phase 7: Missing Mapper Tests (5 files)
- `AttachmentMapperTest`
- `NoteProjectRelationMapperTest`
- `UserConfigMapperTest`
- `UserMapperTest`
- `ViewOptionMapperTest`

---

## Test Patterns

### Service Tests (Mockito)
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserMapper userMapper;
    @Mock private ProjectService projectService;
    @InjectMocks private UserServiceImpl userService;
    
    @Test
    void should_createUser_success() {
        // Arrange
        when(userMapper.insert(any())).thenReturn(1);
        
        // Act
        userService.createUser("test@test.com", "user", "pass");
        
        // Assert
        verify(userMapper).insert(any(User.class));
    }
}
```

### Mapper Tests (MyBatis)
```java
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {
    @Autowired private UserMapper userMapper;
    
    @Test
    void should_findUserByEmail_success() {
        // Arrange
        User user = new User();
        user.setEmail("test@test.com");
        userMapper.insert(user);
        
        // Act
        User found = userMapper.findUserByEmail("test@test.com");
        
        // Assert
        assertNotNull(found);
        assertEquals("test@test.com", found.getEmail());
    }
}
```

---

## Success Criteria

- ✅ All 17 new test files created
- ✅ All empty test files completed
- ✅ All tests follow AAA pattern
- ✅ All tests use proper mocking
- ✅ All tests are minimal and focused
- ✅ Backend test coverage reaches 90%+
- ✅ All business logic has unit tests
- ✅ All DAOs have integration tests

---

## Exclusions (No Tests Needed)

1. **Entity Classes** - POJOs with no logic
2. **DTO Classes** - Data transfer objects
3. **Request/Response Classes** - POJOs
4. **Enum Classes** - Simple enums
5. **Configuration Classes** - Spring config (integration test coverage)
6. **Constants Classes** - Static constants
7. **Main Application Class** - Spring Boot entry point
8. **Filters** - AuthenticationTokenFilter, UnAuthEntryPointJwt (integration tests)
9. **WebSecurityConfig** - Security config (integration tests)
10. **EmailServiceImpl** - External mail dependency (skip or mock heavily)

---

## Estimated Effort

- Phase 1 (Empty Services): 30 minutes
- Phase 2 (Controller): 15 minutes
- Phase 3 (Managers): 45 minutes
- Phase 4 (Security): 45 minutes
- Phase 5 (Utilities): 30 minutes
- Phase 6 (Infrastructure): 30 minutes
- Phase 7 (Mappers): 60 minutes

**Total**: ~4 hours for 17 test files with ~93 test cases
