# Empty Data Test Patterns

## Overview

This document provides standard patterns and best practices for testing empty data scenarios in the Noteverso application. Empty data tests ensure that the application handles cases where no data exists gracefully, displaying appropriate empty states and not throwing errors.

---

## Why Empty Data Tests Matter

1. **User Experience**: Users should see helpful empty states, not errors
2. **Edge Cases**: Empty data is a common edge case that must be handled
3. **API Contracts**: APIs should return consistent structures even with no data
4. **Frontend Robustness**: UI components should handle empty arrays/null values
5. **Regression Prevention**: Prevents bugs when data is deleted or filtered out

---

## Backend Empty Data Test Patterns

### Pattern 1: Empty List Response

**Use Case**: GET endpoint that returns a list of items

**Pattern**:
```java
@Test
void should_getResource_whenEmpty() throws Exception {
    // Arrange
    when(service.getItems(anyString())).thenReturn(new ArrayList<>());

    // Act & Assert
    mockMvc.perform(get("/api/v1/resource"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
}
```

**Example**: `should_getLabels_whenEmpty()` in LabelControllerTest

---

### Pattern 2: Empty Paginated Response

**Use Case**: GET endpoint that returns paginated results

**Pattern**:
```java
@Test
void should_getResource_whenEmpty() throws Exception {
    // Arrange
    PageResult<ItemDTO> emptyResult = new PageResult<>();
    emptyResult.setRecords(new ArrayList<>());
    emptyResult.setTotal(0L);
    emptyResult.setPageIndex(1);
    emptyResult.setPageSize(10);

    when(service.getItems(any(), any())).thenReturn(emptyResult);

    // Act & Assert
    mockMvc.perform(get("/api/v1/resource")
                    .param("pageIndex", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(0))
            .andExpect(jsonPath("$.records").isEmpty());
}
```

**Examples**:
- `should_getUserAttachments_whenEmpty()` in FileControllerTest
- `should_getNotesByLabel_whenEmpty()` in LabelControllerTest
- `should_getProjectNotes_whenEmpty()` in ProjectControllerTest

---

### Pattern 3: Empty Search Results

**Use Case**: Search endpoint that returns no matches

**Pattern**:
```java
@Test
void should_searchResource_whenNoResults() throws Exception {
    // Arrange
    PageResult<ItemDTO> emptyResult = new PageResult<>();
    emptyResult.setRecords(new ArrayList<>());
    emptyResult.setTotal(0L);
    emptyResult.setPageIndex(1);
    emptyResult.setPageSize(10);

    when(service.search(anyString(), anyString(), any())).thenReturn(emptyResult);

    // Act & Assert
    mockMvc.perform(get("/api/v1/resource/search")
                    .param("keyword", "nonexistent")
                    .param("pageIndex", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(0))
            .andExpect(jsonPath("$.records").isEmpty());
}
```

**Example**: `should_searchNotes_whenNoResults()` in NoteControllerTest

---

### Pattern 4: Empty Service Layer Response

**Use Case**: Service method that returns empty collection

**Pattern**:
```java
@Test
void should_returnEmptyList_whenNoData() {
    // Arrange
    when(mapper.selectList(any())).thenReturn(new ArrayList<>());

    // Act
    List<ItemDTO> result = service.getItems(userId);

    // Assert
    assertThat(result).isEmpty();
    assertThat(result).isNotNull();
}
```

**Example**: `should_returnEmptyResult_whenNoNotesWithLabels()` in NoteSearchServiceTest

---

## Frontend Empty Data Test Patterns

### Pattern 1: Component Empty State

**Use Case**: Component that displays a list of items

**Pattern**:
```typescript
it('should_displayEmptyState_whenNoData', async () => {
  // Arrange
  vi.mocked(api.getItems).mockResolvedValue({
    ok: true,
    data: [],
  } as any)

  // Act
  render(<Component />)

  // Assert
  await waitFor(() => {
    expect(screen.getByText(/no items/i)).toBeInTheDocument()
  })
})
```

**Examples**:
- `should_displayEmptyState_whenNoLabels()` in label-selector.test.tsx
- `should_displayEmptyState_whenNoLabels()` in label.test.tsx
- `should_displayEmptyState_whenNoAttachments()` in attachment.test.tsx

---

### Pattern 2: Page Empty State

**Use Case**: Page that displays paginated data

**Pattern**:
```typescript
it('should_displayEmptyState_whenNoResults', async () => {
  // Arrange
  vi.mocked(api.getItems).mockResolvedValue({
    ok: true,
    data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
  } as any)

  // Act
  renderWithRouter(<Page />)

  // Assert
  await waitFor(() => {
    expect(screen.getByText(/no results found/i)).toBeInTheDocument()
  })
})
```

**Example**: `should_displayEmptyState_whenNoResults()` in search.test.tsx

---

### Pattern 3: Search Empty Results

**Use Case**: Search functionality with no matches

**Pattern**:
```typescript
it('should_displayEmptyState_whenNoSearchResults', async () => {
  // Arrange
  vi.mocked(api.search).mockResolvedValue({
    ok: true,
    data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
  } as any)

  // Act
  renderWithRouter(<SearchPage />)

  // Assert
  await waitFor(() => {
    expect(screen.getByText(/no results found/i)).toBeInTheDocument()
    expect(screen.getByText(/try different keywords/i)).toBeInTheDocument()
  })
})
```

**Example**: `should_displayEmptyState_whenNoResults()` in search.test.tsx

---

## E2E Empty Data Test Patterns

### Pattern 1: Empty State Display

**Use Case**: Verify empty state is shown when no data exists

**Pattern**:
```typescript
test('should display empty state when no items', async ({ page }) => {
  // Arrange - Login and navigate
  await page.goto('/login')
  await page.fill('input[type="email"]', 'test@example.com')
  await page.fill('input[type="password"]', 'password123')
  await page.click('button[type="submit"]')
  
  // Act - Navigate to page
  await page.goto('/items')
  
  // Assert - Verify empty state
  await expect(page.locator('text=No items yet')).toBeVisible()
  await expect(page.locator('button:has-text("Create")')).toBeVisible()
})
```

**Examples**:
- Label workflow empty state test
- Attachment workflow empty state test
- Search workflow no results test

---

## Best Practices

### 1. Always Test Empty States

Every GET endpoint that returns a collection should have an empty data test:

```java
// ✅ Good - Tests both success and empty cases
@Test
void should_getItems_successfully() { /* ... */ }

@Test
void should_getItems_whenEmpty() { /* ... */ }
```

```java
// ❌ Bad - Only tests success case
@Test
void should_getItems_successfully() { /* ... */ }
```

---

### 2. Return Consistent Structures

APIs should return the same structure whether data exists or not:

```java
// ✅ Good - Consistent structure
{
  "records": [],
  "total": 0,
  "pageIndex": 1,
  "pageSize": 10
}
```

```java
// ❌ Bad - Null or missing fields
{
  "records": null
}
```

---

### 3. Verify Empty Collections, Not Null

```java
// ✅ Good - Returns empty collection
public List<Item> getItems() {
    return new ArrayList<>();
}

// ❌ Bad - Returns null
public List<Item> getItems() {
    return null;
}
```

---

### 4. Test UI Empty States

Frontend tests should verify that empty states are user-friendly:

```typescript
// ✅ Good - Verifies helpful empty state
await expect(screen.getByText(/no items yet/i)).toBeInTheDocument()
await expect(screen.getByText(/create your first item/i)).toBeInTheDocument()
await expect(screen.getByRole('button', { name: /create/i })).toBeInTheDocument()

// ❌ Bad - Only checks for empty list
await expect(screen.queryByRole('listitem')).not.toBeInTheDocument()
```

---

### 5. Test Edge Cases

Consider these empty data scenarios:

- **No data exists**: User has never created any items
- **All data deleted**: User deleted all items
- **Filtered to empty**: Filters result in no matches
- **Search no results**: Search query matches nothing
- **Paginated empty**: Last page has no items

---

## Common Pitfalls

### 1. Null Pointer Exceptions

```java
// ❌ Bad - Can throw NPE
List<Item> items = service.getItems();
int count = items.size(); // NPE if items is null

// ✅ Good - Safe handling
List<Item> items = service.getItems();
int count = items != null ? items.size() : 0;

// ✅ Better - Never return null
public List<Item> getItems() {
    List<Item> items = mapper.selectList(query);
    return items != null ? items : new ArrayList<>();
}
```

---

### 2. Missing Empty State UI

```typescript
// ❌ Bad - Shows nothing when empty
{items.map(item => <ItemCard key={item.id} item={item} />)}

// ✅ Good - Shows empty state
{items.length > 0 ? (
  items.map(item => <ItemCard key={item.id} item={item} />)
) : (
  <EmptyState message="No items yet" />
)}
```

---

### 3. Inconsistent Pagination

```java
// ❌ Bad - Inconsistent when empty
if (items.isEmpty()) {
    return null;
}
return new PageResult<>(items, total, pageIndex, pageSize);

// ✅ Good - Consistent structure
PageResult<Item> result = new PageResult<>();
result.setRecords(items != null ? items : new ArrayList<>());
result.setTotal(total);
result.setPageIndex(pageIndex);
result.setPageSize(pageSize);
return result;
```

---

## Testing Checklist

Use this checklist when adding empty data tests:

### Backend
- [ ] Empty list endpoint returns `[]` not `null`
- [ ] Empty paginated endpoint returns `{records: [], total: 0}`
- [ ] Search with no results returns empty structure
- [ ] Service methods return empty collections, not null
- [ ] Controller test verifies `status().isOk()`
- [ ] Controller test verifies `jsonPath("$.records").isEmpty()`

### Frontend
- [ ] Component renders without errors when data is empty
- [ ] Empty state message is displayed
- [ ] Call-to-action button is shown (e.g., "Create")
- [ ] No console errors or warnings
- [ ] Loading state is handled correctly
- [ ] API mock returns empty data structure

### E2E
- [ ] Empty state is visible in browser
- [ ] User can navigate to create new item
- [ ] No JavaScript errors in console
- [ ] Empty state has helpful message

---

## Examples by Feature

### Labels

**Backend**:
- `should_getLabels_whenEmpty()` - Empty labels list
- `should_getNotesByLabel_whenEmpty()` - No notes for label

**Frontend**:
- `should_displayEmptyState_whenNoLabels()` - Component empty state
- `should_displayEmptyState_whenNoLabels()` - Page empty state

**E2E**:
- Empty state display test in label workflow

---

### Attachments

**Backend**:
- `should_getUserAttachments_whenEmpty()` - No attachments

**Frontend**:
- `should_displayEmptyState_whenNoAttachments()` - Page empty state

**E2E**:
- Empty state display test in attachment workflow

---

### Search

**Backend**:
- `should_searchNotes_whenNoResults()` - Search returns empty

**Frontend**:
- `should_displayEmptyState_whenNoResults()` - Search page empty state

**E2E**:
- Search with no results test

---

### Projects

**Backend**:
- `should_getProjects_whenEmpty()` - No projects
- `should_getProjectNotes_whenEmpty()` - Project has no notes
- `should_getInboxNotes_whenEmpty()` - Inbox is empty

---

## Conclusion

Empty data tests are essential for:
- Ensuring robust error handling
- Providing good user experience
- Preventing null pointer exceptions
- Maintaining API consistency
- Catching edge case bugs

Follow these patterns to ensure comprehensive empty data test coverage across the application.
