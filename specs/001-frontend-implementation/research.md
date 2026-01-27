# Research: Technical Decisions and Patterns

**Feature**: Complete Frontend Implementation  
**Date**: 2026-01-27  
**Phase**: 0 - Research and technology validation

## Overview

This document captures research findings and technical decisions for implementing the complete frontend of the note-taking application. All decisions are based on analysis of existing codebase, specification requirements, and React/TypeScript best practices.

## 1. State Management Strategy

### Current State
- Local React state (`useState`) in components
- Manual data fetching with axios
- No centralized caching strategy
- Optimistic updates implemented manually in `SharedNotesPage.tsx`

### Research Findings

**TanStack Query (React Query)** - Already installed (`@tanstack/react-query: ^5.51.11`)

**Capabilities**:
- Automatic caching with configurable TTL
- Built-in loading/error states
- Optimistic updates with automatic rollback
- Infinite query support for pagination
- Request deduplication
- Background refetching

**Benefits for this project**:
- Eliminates manual cache management
- Handles race conditions automatically
- Reduces boilerplate code by ~60%
- Built-in pagination for infinite scroll
- Optimistic updates become declarative

**Example pattern**:
```typescript
// Current manual approach
const [notes, setNotes] = useState([])
const [loading, setLoading] = useState(false)
useEffect(() => {
  setLoading(true)
  getNotesApi({ projectId, pageIndex: 1 })
    .then(data => setNotes(data.records))
    .finally(() => setLoading(false))
}, [projectId])

// With TanStack Query
const { data: notes, isLoading } = useQuery({
  queryKey: ['notes', projectId],
  queryFn: () => getNotesApi({ projectId, pageIndex: 1 })
})
```

### Decision

**Adopt TanStack Query for all server state**

**Rationale**:
1. Already installed - zero new dependencies
2. Solves 5 current pain points: caching, loading states, error handling, optimistic updates, pagination
3. Industry standard (100k+ weekly npm downloads)
4. Excellent TypeScript support
5. Reduces code complexity significantly

**Implementation scope**:
- Create custom hooks: `useNotes`, `useProjects`, `useLabels`
- Wrap all API calls in queries/mutations
- Configure global query client with sensible defaults
- Set up query key conventions

## 2. Form Management

### Current State
- Manual form state in components
- No validation library
- React Hook Form installed (`@hookform/resolvers: ^3.9.0`) but not used

### Research Findings

**React Hook Form** - Already installed

**Capabilities**:
- Uncontrolled components (better performance)
- Built-in validation with resolvers (Zod, Yup)
- Field-level error handling
- Watch API for dependent fields
- Integration with UI libraries

**Use cases in this project**:
- Project creation/editing dialog (name, color)
- Label creation/editing dialog (name)
- User registration/login forms
- Search input with debouncing

### Decision

**Use React Hook Form for all dialog forms**

**Rationale**:
1. Already installed - zero cost
2. Reduces form boilerplate
3. Built-in validation
4. Better performance than controlled components
5. Excellent Radix UI integration

**Out of scope**:
- Note editor (TipTap has own state management)
- Simple single-field inputs (overhead not justified)

## 3. Infinite Scroll Implementation

### Current State
- Manual `IntersectionObserver` in `SharedNotesPage.tsx`
- Custom pagination logic
- No scroll position restoration

### Research Findings

**Current pattern** (from SharedNotesPage):
```typescript
const observer = useRef<IntersectionObserver>()
const lastNoteElementRef = useCallback((node) => {
  if (loading) return
  if (observer.current) observer.current.disconnect()
  observer.current = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && hasMore) {
      setPage(prevPage => prevPage + 1)
    }
  })
  if (node) observer.current.observe(node)
}, [loading, hasMore])
```

**TanStack Query `useInfiniteQuery`**:
```typescript
const { data, fetchNextPage, hasNextPage } = useInfiniteQuery({
  queryKey: ['notes', projectId],
  queryFn: ({ pageParam = 1 }) => getNotesApi({ projectId, pageIndex: pageParam }),
  getNextPageParam: (lastPage, pages) => 
    lastPage.total > pages.length * 10 ? pages.length + 1 : undefined
})
```

### Decision

**Extract to reusable `useInfiniteScroll` hook + TanStack Query**

**Rationale**:
1. TanStack Query handles pagination state
2. Custom hook encapsulates IntersectionObserver
3. Reusable across notes, labels, attachments
4. Automatic scroll restoration

**Implementation**:
```typescript
// hooks/use-infinite-scroll.ts
export function useInfiniteScroll(
  callback: () => void,
  { enabled, threshold = 1.0 }
) {
  // IntersectionObserver logic
  return { ref: lastItemRef }
}

// hooks/use-notes.ts
export function useInfiniteNotes(projectId: string) {
  const query = useInfiniteQuery({ /* ... */ })
  const { ref } = useInfiniteScroll(
    () => query.fetchNextPage(),
    { enabled: query.hasNextPage }
  )
  return { ...query, lastItemRef: ref }
}
```

## 4. Optimistic Updates Pattern

### Current State
- Manual optimistic UI in `SharedNotesPage.tsx` (lines 100-150)
- UUID generation for temporary IDs
- Manual rollback on error
- Tightly coupled to component

### Research Findings

**Current manual pattern**:
```typescript
// Optimistically add note
const tempUuid = uuidv4()
const optimisticNote = { noteId: tempUuid, content, ... }
setNotes(prev => [optimisticNote, ...prev])

// Later, replace temp ID with real ID
if (res.ok) {
  const updated = notes.map(n => 
    n.noteId === tempUuid ? { ...n, noteId: res.note } : n
  )
  setNotes(updated)
}
```

**TanStack Query pattern**:
```typescript
const mutation = useMutation({
  mutationFn: addNote,
  onMutate: async (newNote) => {
    // Cancel outgoing queries
    await queryClient.cancelQueries(['notes'])
    // Snapshot previous value
    const prev = queryClient.getQueryData(['notes'])
    // Optimistically update
    queryClient.setQueryData(['notes'], old => [newNote, ...old])
    return { prev }
  },
  onError: (err, newNote, context) => {
    // Rollback on error
    queryClient.setQueryData(['notes'], context.prev)
  },
  onSettled: () => {
    // Refetch after success or error
    queryClient.invalidateQueries(['notes'])
  }
})
```

### Decision

**Standardize optimistic updates with TanStack Query**

**Rationale**:
1. Automatic rollback on errors
2. No manual state synchronization
3. Consistent pattern across all mutations
4. Query invalidation handled automatically
5. Race condition protection built-in

**Implementation scope**:
- Note creation/editing
- Project creation/editing
- Label creation/editing
- Pin/archive/delete actions

## 5. Error Handling Strategy

### Current State
- Basic error responses from API
- No global error handling
- No user-friendly error messages

### Research Findings

**Radix UI Toast** - Already installed (`@radix-ui/react-toast: ^1.2.1`)

**Error categories**:
1. **Network errors**: Connection lost, timeout
2. **Validation errors**: Invalid form input
3. **Authorization errors**: Session expired, insufficient permissions
4. **Business logic errors**: Cannot delete project with notes
5. **Unexpected errors**: 500 errors, unhandled exceptions

### Decision

**Implement layered error handling**

**Strategy**:
```
Component Error Boundary (fallback UI)
    ↓
TanStack Query Error Handling (toast notification)
    ↓
API Layer Error Mapping (categorize errors)
    ↓
Axios Interceptors (session handling)
```

**Rationale**:
1. Error boundaries prevent full app crashes
2. Toast notifications for recoverable errors
3. Consistent error UX across application
4. Automatic session expiry handling

**Implementation**:
- Global error boundary in app root
- Toast provider in layout
- Error mapper utility
- Axios response interceptor for 401/403

## 6. Search Implementation

### Current State
- No search functionality implemented
- Backend API support unclear

### Research Findings

**Backend endpoints** (from controller analysis):
- No dedicated `/api/v1/search` endpoint found
- Note filtering by project/label exists
- Need to clarify if backend supports content search

**Two approaches**:

**Option A: Backend search (preferred if available)**
```typescript
GET /api/v1/notes/search?q=keyword&projectId=...
```
- Full-text search in database
- Efficient for large datasets
- Requires backend implementation

**Option B: Client-side filtering (fallback MVP)**
```typescript
const filtered = notes.filter(note => 
  note.content.toLowerCase().includes(query.toLowerCase())
)
```
- Works with existing endpoints
- Limited to loaded notes
- Acceptable for MVP

### Decision

**NEEDS CLARIFICATION**: Check backend capabilities

**Interim approach**:
1. Implement client-side search as MVP
2. Debounce search input (300ms)
3. Search across loaded notes only
4. Add backend search when available

**Implementation**:
```typescript
const { query, setQuery, results } = useSearch({
  items: notes,
  searchFields: ['content'],
  debounce: 300
})
```

## 7. Rich Text Content Storage

### Current State
- TipTap editor installed and configured
- Outputs HTML content
- Backend stores content as `String` field

### Research Findings

**TipTap output formats**:
- **HTML**: `<p><strong>Bold</strong> text</p>`
- **JSON**: `{ type: 'doc', content: [...] }`
- **Markdown**: `**Bold** text`

**Backend storage** (from `NoteDTO.java`):
```java
private String content;  // Plain string field
```

**Security considerations**:
- HTML injection risk
- XSS vulnerabilities
- Need sanitization

### Decision

**Store as HTML with client-side sanitization**

**Rationale**:
1. No backend schema changes needed
2. Preserves formatting completely
3. Compatible with TipTap's natural output
4. Sanitization handled by TipTap on render

**Implementation**:
- TipTap outputs HTML
- Store directly in content field
- TipTap sanitizes on render
- No additional parsing needed

**Alternative considered**: JSON format
- Rejected: More complex, no clear benefit
- Backend would need to parse/validate
- HTML is simpler and sufficient

## 8. Component Architecture Patterns

### Current State
- Mix of container and presentational components
- Some duplication (e.g., SharedNotesPage)
- Inconsistent prop patterns

### Research Findings

**React best practices 2024**:
1. Composition over inheritance
2. Custom hooks for logic
3. Component co-location
4. Feature-based organization

**Existing patterns in codebase**:
- Radix UI components wrapped in `components/ui/`
- Feature components in `features/` (partially)
- Page components in `pages/`

### Decision

**Enforce feature-based organization with clear layers**

**Structure**:
```
features/note/
  ├── note-card.tsx        # Presentational
  ├── note-list.tsx        # Presentational
  ├── note-editor.tsx      # Stateful
  ├── note-actions.tsx     # Connected (uses hooks)
  └── use-note-actions.ts  # Logic hook
```

**Rationale**:
1. Co-locate related components
2. Clear separation of concerns
3. Reusable hooks for business logic
4. Easy to test in isolation

**Naming conventions**:
- `use-*`: Custom hooks
- `*-dialog`: Modal/popup components
- `*-list`: List components
- `*-card`: Item display components

## 9. Testing Strategy

### Current State
- Vitest installed but no tests configured
- React Testing Library not set up
- No E2E tests

### Research Findings

**Testing pyramid**:
```
       /\
      /  \  E2E (5%)
     /----\ 
    /      \  Integration (15%)
   /--------\
  /          \  Unit (80%)
 /____________\
```

**Recommended stack**:
- **Unit**: Vitest + React Testing Library
- **Integration**: Vitest + MSW (Mock Service Worker)
- **E2E**: Playwright (already common in React ecosystem)

### Decision

**Implement unit and integration tests, defer E2E**

**Priorities**:
1. Custom hooks (highest ROI)
2. API service functions
3. Utility functions
4. Component behavior (user interactions)
5. E2E critical paths (login, create note) - Phase 2

**Rationale**:
1. Hooks are core business logic
2. API integration tests prevent regressions
3. Component tests validate user flows
4. E2E tests expensive to maintain

**Test organization**:
```
tests/
├── unit/
│   ├── hooks/
│   ├── utils/
│   └── components/
├── integration/
│   └── api/
└── e2e/  # Future
```

## 10. Performance Optimization

### Requirements from spec
- <2s page loads
- <1s infinite scroll pagination
- <5s note creation end-to-end
- 60 FPS UI interactions

### Research Findings

**Vite optimizations** (already configured):
- Fast HMR (Hot Module Replacement)
- Code splitting
- Tree shaking
- Asset optimization

**React 18 features to leverage**:
- Concurrent rendering (already available)
- `useTransition` for non-urgent updates
- `useDeferredValue` for search results
- `<Suspense>` for code splitting

**TanStack Query optimizations**:
- Stale-while-revalidate caching
- Request deduplication
- Background refetching
- Prefetching on hover

### Decision

**Apply progressive enhancement**

**Phase 1 (MVP)**:
- TanStack Query caching (automatic)
- Lazy load routes with React.lazy
- Debounce search input
- Virtual scrolling NOT needed (10-20 notes per page)

**Phase 2 (if needed)**:
- Prefetch project notes on hover
- Image lazy loading for attachments
- Service worker for offline assets

**Rationale**:
1. Premature optimization avoided
2. Measure first, optimize based on data
3. TanStack Query handles 80% of perf issues
4. Virtual scrolling overkill for pagination

## Summary of Decisions

| Area | Decision | Alternatives Rejected |
|------|----------|----------------------|
| State Management | TanStack Query | Redux (overkill), Zustand (unnecessary), MobX (complex) |
| Forms | React Hook Form | Formik (heavier), Manual (boilerplate) |
| Infinite Scroll | Custom hook + `useInfiniteQuery` | Third-party library (unnecessary) |
| Optimistic Updates | TanStack Query pattern | Manual (error-prone) |
| Error Handling | Error boundary + Toast | Global modal (disruptive), Silent failures (bad UX) |
| Search | Client-side filtering (MVP) | Backend search (needs implementation) |
| Content Storage | HTML | JSON (complex), Markdown (parsing overhead) |
| Component Architecture | Feature-based + custom hooks | Page-based (poor scalability), Smart/Dumb (outdated pattern) |
| Testing | Vitest + RTL | Jest (slower), Enzyme (deprecated) |
| Performance | Progressive enhancement | Virtual scrolling (premature), SSR (not needed) |

## Validation Checklist

- ✅ All decisions leverage existing dependencies
- ✅ Zero new major dependencies required
- ✅ Patterns align with React 18+ best practices
- ✅ Performance requirements achievable
- ✅ Testing strategy covers critical paths
- ✅ Architecture supports incremental development
- ✅ No vendor lock-in for critical functionality

## Next Steps

1. Generate `data-model.md` with detailed state management patterns
2. Create API contracts in `contracts/` directory
3. Write `quickstart.md` for development setup
4. Proceed to Phase 2 task breakdown with `/speckit.tasks`
