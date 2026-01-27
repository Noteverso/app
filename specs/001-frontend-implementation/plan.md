# Implementation Plan: Complete Frontend Implementation

**Branch**: `001-frontend-implementation` | **Date**: 2026-01-27 | **Spec**: [spec.md](./spec.md)

## Summary

Complete the frontend implementation of a note-taking application with comprehensive CRUD operations for notes, projects, and labels. The application uses React 18+ with TypeScript, React Router for navigation, TipTap for rich text editing, and integrates with existing Spring Boot backend APIs. Implementation focuses on user experience with infinite scroll, optimistic updates, and comprehensive state management.

## Technical Context

**Language/Version**: TypeScript 5.x with React 18.2+  
**Primary Dependencies**: React, React Router 6.x, TipTap 2.5+ (rich text), Radix UI (component primitives), TanStack Query (data fetching), Vite (build tool)  
**Storage**: Backend REST API (Spring Boot 3.x, Java 17), supports MySQL/PostgreSQL  
**Testing**: Vitest (unit tests), React Testing Library (component tests), Playwright/Cypress (E2E - to be added)  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge - latest 2 versions), Desktop-first responsive design  
**Project Type**: Web application (monorepo: frontend + backend separate)  
**Performance Goals**: <2s page loads, <1s infinite scroll pagination, <5s note creation end-to-end, 60 FPS UI interactions  
**Constraints**: Desktop-only UI (no mobile optimization), Online-only (no offline support), No real-time collaboration  
**Scale/Scope**: Support 100+ concurrent users, 1000+ notes per user, 50+ projects per user, 100+ labels per user

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Constitution Status**: No constitution file defined beyond template. Applying React/TypeScript community best practices.

**Default Gates**:
- ✅ Leverage existing patterns (React components, hooks, API service layer already established)
- ✅ Component composition over inheritance
- ✅ Custom hooks for reusable logic
- ✅ Type safety throughout (no `any` types without justification)
- ✅ Testing strategy: Unit tests for hooks/utilities, Integration tests for API calls, Component tests for UI interactions
- ✅ No unnecessary state management libraries (React Context + TanStack Query sufficient for current scale)

## Project Structure

### Documentation (this feature)

```text
specs/001-frontend-implementation/
├── plan.md              # This file
├── research.md          # Phase 0: Technology decisions and patterns
├── data-model.md        # Phase 1: Frontend data models and state
├── quickstart.md        # Phase 1: Development setup and workflows
├── contracts/           # Phase 1: API contracts and TypeScript interfaces
│   ├── note-api.yaml
│   ├── project-api.yaml
│   └── label-api.yaml
└── tasks.md             # Phase 2: Implementation tasks (created by /speckit.tasks)
```

### Source Code (repository root)

```text
frontend/web/
├── src/
│   ├── api/                    # API service layer
│   │   ├── note/
│   │   │   ├── note.ts        # Note CRUD operations (partially implemented)
│   │   │   └── types.ts       # Note API types
│   │   ├── project/
│   │   │   ├── project.ts     # Project CRUD operations (minimal)
│   │   │   └── types.ts       # Project API types
│   │   ├── label/
│   │   │   ├── label.ts       # Label CRUD operations (to implement)
│   │   │   └── types.ts       # Label API types
│   │   └── user/
│   │       ├── user.ts        # User auth operations (partial)
│   │       └── types.ts       # User API types
│   │
│   ├── components/             # Reusable UI components
│   │   ├── ui/                # Radix UI wrappers (existing)
│   │   └── logo/              # App branding
│   │
│   ├── features/               # Feature-specific components
│   │   ├── note/
│   │   │   ├── note-card.tsx         # Individual note display
│   │   │   ├── note-list.tsx         # Note list with infinite scroll
│   │   │   ├── note-editor.tsx       # Note editing (enhance existing)
│   │   │   ├── note-actions.tsx      # Pin/Archive/Delete actions
│   │   │   └── note-filter.tsx       # Filter by status
│   │   ├── project/
│   │   │   ├── project-list.tsx      # Sidebar project list
│   │   │   ├── project-dialog.tsx    # Create/Edit project dialog
│   │   │   ├── project-actions.tsx   # Archive/Delete/Favorite
│   │   │   └── project-color-picker.tsx
│   │   ├── label/
│   │   │   ├── label-list.tsx        # Sidebar label list
│   │   │   ├── label-dialog.tsx      # Create/Edit label dialog
│   │   │   ├── label-selector.tsx    # Multi-select for notes
│   │   │   └── label-actions.tsx     # Favorite/Delete
│   │   ├── editor/
│   │   │   └── text-editor.tsx       # TipTap rich text editor (existing)
│   │   └── search/
│   │       ├── search-bar.tsx        # Global search input
│   │       └── search-results.tsx    # Search results display
│   │
│   ├── hooks/                  # Custom React hooks
│   │   ├── use-notes.ts               # Note data fetching/mutations
│   │   ├── use-projects.ts            # Project data management
│   │   ├── use-labels.ts              # Label data management
│   │   ├── use-infinite-scroll.ts     # Generic infinite scroll
│   │   ├── use-optimistic-update.ts   # Optimistic UI pattern
│   │   └── use-debounce.ts            # Search debouncing
│   │
│   ├── layout/                 # Layout components
│   │   ├── layout.tsx                 # Main app layout (existing)
│   │   ├── header/                    # Header with search
│   │   ├── nav/                       # Sidebar navigation (existing)
│   │   ├── main/                      # Content area
│   │   └── footer/                    # Footer
│   │
│   ├── pages/                  # Route pages
│   │   ├── home/                      # Landing page
│   │   ├── auth/                      # Login/Signup (existing)
│   │   ├── inbox/                     # Inbox view (existing)
│   │   ├── project/                   # Project view (existing)
│   │   ├── label/                     # Label view (stub exists)
│   │   ├── attachment/                # Attachments view (stub)
│   │   ├── shared-notes-page/         # Shared note display component
│   │   └── error/                     # Error boundary
│   │
│   ├── routes/                 # React Router configuration
│   │   ├── routes.tsx                 # Route definitions (existing)
│   │   ├── loader.ts                  # Route loaders
│   │   └── action.ts                  # Route actions
│   │
│   ├── store/                  # State management
│   │   ├── use-inbox-store.ts         # Inbox state (existing)
│   │   ├── use-filter-store.ts        # Filter state (to add)
│   │   └── use-search-store.ts        # Search state (to add)
│   │
│   ├── types/                  # TypeScript type definitions
│   │   ├── note.ts                    # Note types (existing)
│   │   ├── project.ts                 # Project types (existing)
│   │   ├── label.ts                   # Label types (to add)
│   │   ├── user.ts                    # User types (existing)
│   │   └── common.ts                  # Shared types
│   │
│   ├── lib/                    # Utility libraries
│   │   ├── http.ts                    # Axios wrapper (existing)
│   │   ├── auth.ts                    # Auth provider (existing)
│   │   ├── storage.ts                 # LocalStorage wrapper
│   │   └── utils.ts                   # Utility functions
│   │
│   ├── constants/              # Application constants
│   │   ├── index.ts                   # General constants
│   │   ├── project-constants.ts       # Project colors, etc.
│   │   └── router-constants.ts        # Route paths
│   │
│   ├── styles/                 # Global styles
│   │   ├── index.css                  # Main stylesheet
│   │   ├── variables.css              # CSS custom properties
│   │   └── tiptap.css                 # Editor styles
│   │
│   ├── app.tsx                        # App root component
│   └── main.tsx                       # Entry point
│
├── tests/                      # Test files (to organize)
│   ├── unit/                          # Unit tests
│   ├── integration/                   # API integration tests
│   └── e2e/                           # End-to-end tests (Playwright)
│
├── public/                     # Static assets
├── components.json             # Shadcn UI config
├── package.json                # Dependencies
├── tsconfig.json               # TypeScript config
├── vite.config.ts              # Vite configuration
└── tailwind.config.ts          # Tailwind CSS config

backend/                        # Java Spring Boot backend (reference only)
├── noteverso-core/
│   └── src/main/java/com/noteverso/core/
│       ├── controller/                # REST endpoints
│       │   ├── NoteController.java
│       │   ├── ProjectController.java
│       │   └── LabelController.java
│       ├── service/                   # Business logic
│       └── model/                     # Data models
└── noteverso-common/                  # Shared utilities
```

**Structure Decision**: Monorepo with separate frontend and backend. Frontend follows feature-based organization with clear separation of concerns: API layer, components by feature, shared UI components, custom hooks for logic, and centralized routing. This structure supports incremental development and testing.

## Phase 0: Research & Technical Decisions

### Key Research Areas

1. **State Management Strategy**
   - Current: React Context + local state
   - Evaluate: TanStack Query for server state (already installed)
   - Decision: Use TanStack Query for all API calls (caching, optimistic updates, infinite queries)
   - Justification: Reduces boilerplate, built-in caching, handles race conditions

2. **Form Management**
   - Current: Manual form handling
   - Evaluate: React Hook Form (already installed with @hookform/resolvers)
   - Decision: Use React Hook Form for project/label dialogs
   - Justification: Already installed, reduces validation boilerplate

3. **Infinite Scroll Implementation**
   - Current: Custom IntersectionObserver in SharedNotesPage
   - Decision: Extract to reusable `use-infinite-scroll` hook, leverage TanStack Query's `useInfiniteQuery`
   - Justification: Reusable pattern, built-in pagination management

4. **Optimistic Updates Pattern**
   - Current: Manual optimistic UI in SharedNotesPage
   - Decision: Standardize with TanStack Query's optimistic update pattern
   - Justification: Consistent UX, automatic rollback on errors

5. **Error Handling**
   - Current: Basic error responses
   - Decision: Global error boundary + toast notifications (Radix UI Toast already installed)
   - Justification: Consistent error UX, recoverable errors

6. **Search Implementation**
   - Backend support: Unknown - needs clarification
   - Frontend: Debounced input + API call
   - Decision: Client-side filtering for MVP if backend search not available

7. **Rich Text Content Storage**
   - TipTap outputs: HTML
   - Backend storage: String content field
   - Decision: Store as HTML, sanitize on display
   - Justification: Preserves formatting, no additional schema changes

### Technology Choices Validation

| Technology | Status | Notes |
|------------|--------|-------|
| React 18.2+ | ✅ Installed | Concurrent rendering, Suspense |
| TypeScript 5.x | ✅ Installed | Strict mode enabled |
| React Router 6.x | ✅ Installed | Data router pattern in use |
| TipTap 2.5+ | ✅ Installed | Rich text editor with extensions |
| Radix UI | ✅ Installed | Accessible component primitives |
| TanStack Query | ✅ Installed | Server state management |
| Tailwind CSS | ✅ Installed | Utility-first styling |
| Vite | ✅ Installed | Fast dev server, HMR |
| Vitest | ⚠️ Needs setup | Testing framework |
| React Testing Library | ⚠️ Needs setup | Component testing |

### API Integration Patterns

**Existing Pattern** (from `note.ts`):
```typescript
export function addNote(newNote: NewNote) {
  return request<string>({
    url: '/api/v1/notes',
    method: 'post',
    data: newNote,
  })
}
```

**Decision**: Continue this pattern, enhance with TanStack Query mutations:
```typescript
const { mutate: createNote } = useMutation({
  mutationFn: (newNote: NewNote) => addNote(newNote),
  onSuccess: (noteId) => {
    queryClient.invalidateQueries(['notes'])
  }
})
```

## Phase 1: Design & Contracts

### Data Model (Frontend)

See [data-model.md](./data-model.md) for complete entity definitions and state management patterns.

**Key Models**:
- **Note**: Content, project relationship, labels array, status flags (pinned, archived, deleted)
- **Project**: Name, color, favorite status, note count, inbox flag
- **Label**: Name, favorite status, note count
- **User**: Email, authentication state

**State Organization**:
- Server state: TanStack Query (notes, projects, labels)
- UI state: React Context (filters, search, selected items)
- Form state: React Hook Form (dialogs)
- Route state: React Router (loaders, actions)

### API Contracts

See [contracts/](./contracts/) directory for OpenAPI specifications.

**Note API** (`/api/v1/notes`):
- POST / - Create note
- GET /:id - Get note
- PATCH /:id - Update note
- DELETE /:id - Soft delete
- PATCH /:id/pin - Pin/unpin
- PATCH /:id/archive - Archive/unarchive
- PATCH /:id/restore - Restore from trash
- DELETE /:id/permanent - Permanent delete

**Project API** (`/api/v1/projects`):
- GET / - List projects
- POST / - Create project
- PATCH /:id - Update project
- DELETE /:id - Delete project
- PATCH /:id/favorite - Toggle favorite
- PATCH /:id/archive - Archive/unarchive
- GET /:id/notes - Get project notes (paginated)
- GET /inbox/notes - Get inbox notes

**Label API** (`/api/v1/labels`):
- GET / - List labels
- POST / - Create label
- PATCH /:id - Update label
- DELETE /:id - Delete label
- PATCH /:id/favorite - Toggle favorite
- GET /:id/notes - Get notes by label (paginated)

### Component Architecture

**Container Components** (pages):
- `<Inbox />` - Displays inbox notes
- `<Project />` - Displays project notes
- `<Label />` - Displays notes by label
- `<Attachment />` - Displays attachments

**Presentational Components**:
- `<NoteCard />` - Single note display
- `<NoteList />` - List of notes with infinite scroll
- `<ProjectList />` - Sidebar project list
- `<LabelList />` - Sidebar label list

**Form Components**:
- `<ProjectDialog />` - Create/edit project
- `<LabelDialog />` - Create/edit label
- `<LabelSelector />` - Multi-select labels for note

**Smart Components** (with data fetching):
- `<SharedNotesPage />` - Reusable note list view with filters

### Routing Structure

```
/ - Home (public)
/auth/login - Login page
/auth/signup - Signup page (if implementing)
/app - Protected layout
  /app/inbox - Inbox view (default)
  /app/projects/:projectId - Project view
  /app/labels/:labelId - Label view (to implement)
  /app/attachments - Attachments view (to implement)
  /app/trash - Trash view (to implement)
  /app/search?q=query - Search results (to implement)
```

## Complexity Tracking

No violations requiring justification. Design leverages existing patterns and React ecosystem best practices.

## Next Steps

1. **Complete Phase 0**: Generate `research.md` with detailed technology research
2. **Complete Phase 1**: Generate `data-model.md`, API contracts in `contracts/`, and `quickstart.md`
3. **Phase 2**: Run `/speckit.tasks` to generate implementation task breakdown
