# Implementation Plan: Complete Frontend Implementation

**Branch**: `001-frontend-implementation` | **Date**: 2026-01-27 | **Spec**: [spec.md](./spec.md)

## Summary

Complete the frontend implementation of a note-taking application with comprehensive CRUD operations for notes, projects, and labels. The application uses React 18+ with TypeScript, React Router for navigation, TipTap for rich text editing, and integrates with existing Spring Boot backend APIs. Implementation focuses on user experience with infinite scroll, optimistic updates, comprehensive state management, **mobile-first responsive design**, and **block-based JSON content storage**.

## Technical Context

**Language/Version**: TypeScript 5.x with React 18.2+  
**Primary Dependencies**: React, React Router 6.x, TipTap 2.5+ (rich text), Radix UI (component primitives), TanStack Query (data fetching), Vite (build tool)  
**Storage**: Backend REST API (Spring Boot 3.x, Java 17), PostgreSQL with **JSONB for block-based content storage** (Notion/Editor.js style)  
**Content Architecture**: Block-based JSON structure (NOT HTML strings or monolithic Markdown) - each note contains array of typed blocks (paragraph, heading, list, code, etc.)  
**Testing**: Vitest (unit tests), React Testing Library (component tests), Playwright/Cypress (E2E - to be added)  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge - latest 2 versions), **Mobile-first responsive design** (320px - 2560px viewport)  
**Mobile Support**: iOS Safari 14+, Chrome Mobile 90+, responsive touch interactions, optimized mobile performance  
**Project Type**: Web application (monorepo: frontend + backend separate)  
**Performance Goals**: <2s page loads (desktop), <3s (mobile 3G), <1s infinite scroll pagination, <5s note creation end-to-end, 60 FPS UI interactions, <100KB initial JS bundle  
**Mobile Performance**: <50ms touch response, smooth 60fps scrolling, optimized for 4G/3G networks, service worker caching  
**Responsive Strategy**: Mobile-first CSS, touch-optimized UI (44px+ tap targets), collapsible navigation, responsive typography (16px+ base), viewport-aware layouts  
**Constraints**: Online-only (no offline support), No real-time collaboration  
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
- ✅ Mobile-first responsive design with progressive enhancement
- ✅ Block-based JSON content architecture for structured data
- ✅ Optimistic updates with rollback for all mutations
- ✅ Client-side search MVP with future backend migration path
- ✅ Essential keyboard shortcuts only (5 shortcuts)

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
backend/                           # Spring Boot 3.x, Java 17 (EXISTING - no changes)
├── noteverso-common/             # Shared utilities
└── noteverso-core/               # Main application
    ├── src/main/java/com/noteverso/
    │   ├── controller/           # REST APIs (existing)
    │   ├── service/              # Business logic
    │   ├── mapper/               # MyBatis mappers
    │   └── pojo/                 # DTOs, entities
    └── src/main/resources/
        ├── application.yml
        ├── mapper/               # SQL mappings
        └── noteverso-pg.sql      # PostgreSQL schema

frontend/web/                      # React + TypeScript (ACTIVE DEVELOPMENT)
├── src/
│   ├── components/               # Reusable UI components
│   │   ├── ui/                   # shadcn/ui primitives (Button, Dialog, etc.)
│   │   └── logo/                 # Brand components
│   ├── features/                 # Feature-specific components
│   │   ├── editor/               # TipTap editor components
│   │   └── note/                 # Note-specific features
│   ├── pages/                    # Route-level page components
│   │   ├── home/                 # Dashboard/home page
│   │   ├── project/              # Project management pages
│   │   ├── label/                # Label management pages
│   │   ├── inbox/                # Inbox view
│   │   ├── shared-notes-page/    # Shared notes feature
│   │   ├── attachment/           # Attachment management
│   │   └── auth/                 # Authentication pages
│   ├── api/                      # Backend API client modules
│   │   ├── note/                 # Note CRUD operations
│   │   ├── project/              # Project CRUD operations
│   │   └── user/                 # User operations
│   ├── routes/                   # React Router configuration
│   │   ├── routes.tsx            # Route definitions
│   │   ├── loader.ts             # Route data loaders
│   │   └── action.ts             # Route actions
│   ├── store/                    # Global state management
│   │   └── use-inbox-store.ts   # Inbox state (example)
│   ├── contexts/                 # React Context providers
│   │   └── fake-auth-provider.ts # Auth context
│   ├── lib/                      # Utilities and shared logic
│   │   ├── http.ts               # Axios/fetch wrapper
│   │   ├── auth.ts               # Auth helpers
│   │   ├── storage.ts            # LocalStorage helpers
│   │   └── utils.ts              # General utilities
│   ├── types/                    # TypeScript type definitions
│   │   ├── common.ts             # Shared types
│   │   ├── note.ts               # Note-related types (block-based JSON)
│   │   ├── project.ts            # Project types
│   │   └── user.ts               # User types
│   ├── constants/                # Application constants
│   │   ├── index.ts              # General constants
│   │   ├── project-constants.ts  # Project-related constants
│   │   └── router-constants.ts   # Route path constants
│   ├── styles/                   # Global CSS
│   │   ├── index.css             # Main entry point
│   │   ├── reset.css             # CSS reset
│   │   ├── layout.css            # Layout styles
│   │   ├── variables.css         # CSS custom properties
│   │   └── tiptap.css            # TipTap editor styles
│   ├── layout/                   # Application shell components
│   │   ├── layout.tsx            # Main layout wrapper
│   │   ├── header/               # Header component
│   │   ├── nav/                  # Navigation sidebar
│   │   ├── main/                 # Main content area
│   │   └── footer/               # Footer component
│   └── assets/                   # Static assets
│       └── svg/                  # SVG icons
├── public/                        # Static files (served directly)
├── tests/                         # Test files (to be created)
│   ├── unit/                     # Unit tests (utilities, hooks)
│   ├── integration/              # Integration tests (API calls)
│   └── components/               # Component tests (React Testing Library)
├── vite.config.ts                # Vite configuration
├── tsconfig.json                 # TypeScript configuration
├── tailwind.config.ts            # Tailwind CSS configuration
├── postcss.config.js             # PostCSS configuration
└── package.json                  # Dependencies

.specify/                          # Speckit workflow (meta)
├── memory/
│   └── constitution.md           # Project principles (template only)
├── scripts/
│   └── powershell/               # Workflow automation scripts
└── templates/
    └── commands/                 # Command templates
```

**Structure Decision**: Web application (Option 2) - Frontend (React) + Backend (Spring Boot) monorepo. Frontend follows feature-based organization with clear separation of concerns (components, pages, api, routes). Backend uses layered architecture (controller → service → mapper). Mobile-first responsive design within single web codebase.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

**Status**: No constitution violations. All decisions align with React/TypeScript community best practices.

---

## Phase 0: Research & Technical Decisions

**Output**: [research.md](./research.md)  
**Status**: ✅ COMPLETE

**Key Decisions**:
1. **TipTap 2.5+ for Rich Text**: Pros/modular extensibility, React integration, block-based JSON content model. Cons/initial bundle size ~200KB. Alternatives/Draft.js (outdated), Slate (complex API), ProseMirror (low-level). Decision: TipTap best balance.
2. **TanStack Query 5.51+ for Data Fetching**: Pros/built-in caching, optimistic updates, automatic retries, background refetch. Cons/learning curve. Alternatives/SWR (simpler but less features), manual useState+useEffect (boilerplate). Decision: TanStack Query for comprehensive feature set.
3. **Radix UI for Accessibility**: Pros/unstyled primitives, WAI-ARIA compliant, keyboard navigation. Cons/no visual styling (need Tailwind). Alternatives/Headless UI (less components), Material-UI (opinionated styling). Decision: Radix + Tailwind for flexibility.
4. **React Router 6.x with Loaders/Actions**: Pros/colocated data fetching, type-safe routes, suspense integration. Cons/breaking changes from v5. Alternatives/Next.js (overkill for SPA), TanStack Router (immature). Decision: React Router for maturity.
5. **Vitest for Unit Testing**: Pros/Vite-native, fast (ESM), Jest-compatible API. Cons/smaller ecosystem than Jest. Alternatives/Jest (mature but slower). Decision: Vitest for speed.
6. **Client-Side Search (MVP)**: Pros/instant results, no backend changes, <100ms latency. Cons/limited to loaded notes, not full-text search. Alternatives/Backend Elasticsearch (complex setup). Decision: Client-side MVP, migrate to backend later.
7. **Block-Based JSON Content Storage**: Pros/structured editing, extensible block types, Notion-like UX, backend JSONB support. Cons/migration from existing HTML. Alternatives/HTML strings (unstructured), Markdown (limited formatting). Decision: Block-based JSON for future-proof architecture.
8. **Optimistic Updates Pattern**: Pros/instant UX feedback, <50ms perceived latency, rollback on error. Cons/requires careful rollback logic. Alternatives/Loading spinners (poor UX), disabled UI (frustrating). Decision: Optimistic updates for all mutations with toast notifications on rollback.
9. **React Hook Form**: Pros/minimal re-renders, uncontrolled components, Zod integration. Cons/learning curve. Alternatives/Formik (heavy re-renders), manual useState (boilerplate). Decision: React Hook Form for performance.
10. **CSS Modules + Tailwind CSS**: Pros/scoped styles, utility-first, mobile-first responsive utilities. Cons/large generated CSS. Alternatives/Styled Components (runtime cost), CSS-in-JS (complexity). Decision: Tailwind for rapid responsive development.
11. **Mobile-First Responsive Design**: Pros/progressive enhancement, better mobile performance, forced constraint-based design. Cons/desktop may feel limited. Alternatives/Desktop-first (poor mobile UX), separate mobile app (expensive). Decision: Mobile-first with responsive breakpoints (320px, 768px, 1024px, 1440px, 2560px).

All decisions documented with rationale and alternatives in [research.md](./research.md).

---

## Phase 1: Design & Contracts

### 1.1 Data Model

**Output**: [data-model.md](./data-model.md)  
**Status**: ✅ COMPLETE

**Core Entities**:
- **Note**: Block-based JSON content (`ContentBlock[]`), metadata (title, projectId, labelIds, pinned, archived), timestamps, optimistic UI state
- **Project**: name, description, color, icon, archived, timestamps
- **Label**: name, color, timestamps
- **ContentBlock**: Block-based JSON structure with type unions (paragraph, heading, bulletList, orderedList, codeBlock, blockquote, image, divider, callout)
- **UI State**: Optimistic mutation tracking, infinite scroll pagination, search filters, selected items

**Key Patterns**:
- Block-based content: `NoteContent { blocks: ContentBlock[] }` where each block has `{ id, type, content, attrs? }`
- Optimistic updates: `useMutation` with `onMutate` (instant UI) + `onError` (rollback) + toast notifications
- Infinite scroll: `useInfiniteQuery` with `getNextPageParam` + intersection observer
- Form validation: React Hook Form + Zod schemas
- Keyboard shortcuts: `useKeyboardShortcuts` hook with 5 essential shortcuts (Ctrl+S, Ctrl+N, Ctrl+K, Ctrl+P, Esc)
- Note linking: Inline `[[note-title]]` links + separate backlinks panel showing incoming links

Full TypeScript interfaces and validation schemas in [data-model.md](./data-model.md).

### 1.2 API Contracts

**Output**: [contracts/](./contracts/)  
**Status**: ✅ COMPLETE

**Files**:
- [note-api.md](./contracts/note-api.md): Note CRUD with block-based JSON request/response, infinite scroll pagination, search/filter, batch operations, mobile-optimized payloads
- [project-api.md](./contracts/project-api.md): Project CRUD, mobile-friendly compact displays
- [label-api.md](./contracts/label-api.md): Label CRUD, mobile label selectors with touch interactions

**Key Endpoints** (examples):
- `POST /api/notes`: Create note with `{ title, content: { blocks: ContentBlock[] }, projectId?, labelIds? }`
- `GET /api/notes?page=0&size=20&projectId=123`: Paginated notes with block content
- `PATCH /api/notes/:id`: Update note (optimistic, with rollback)
- `DELETE /api/notes/:id`: Delete note (optimistic, with rollback)

All contracts include request/response schemas, error codes, validation rules, and mobile considerations.

### 1.3 Developer Quickstart

**Output**: [quickstart.md](./quickstart.md)  
**Status**: ✅ COMPLETE

**Content**:
- Setup: Clone repo, install dependencies (`pnpm install`), environment variables, start backend + frontend
- Mobile Development: Responsive testing (Chrome DevTools, BrowserStack), touch interaction debugging, performance profiling (3G throttling)
- Block-Based Content: Creating custom block types, TipTap extensions, JSON schema examples
- Development Workflow: Creating new features (component → hook → API → test), testing patterns, debugging techniques
- Common Tasks: Adding routes, creating forms, implementing infinite scroll, optimistic updates pattern, keyboard shortcuts

Includes code examples for typical implementation patterns.

### 1.4 Agent Context Update

**Action**: Run `.specify/scripts/powershell/update-agent-context.ps1 -AgentType copilot`  
**Status**: ⏸️ PENDING (Phase 1 documents complete, ready for agent context update)

---

## Phase 2: Constitution Re-Check

**Status**: ✅ PASS (No violations)

Post-design review confirms:
- ✅ Leveraging existing React patterns (hooks, components, contexts)
- ✅ Component composition throughout (no class inheritance)
- ✅ Custom hooks for reusable logic (useKeyboardShortcuts, useNoteEditor, useInfiniteScroll)
- ✅ Type safety maintained (TypeScript strict mode, Zod validation)
- ✅ Testing strategy defined (Vitest unit tests, React Testing Library component tests)
- ✅ No unnecessary state management (React Context + TanStack Query sufficient)
- ✅ Mobile-first responsive design with progressive enhancement (320px base)
- ✅ Block-based JSON content architecture (future-proof, extensible)
- ✅ Optimistic updates with rollback for all mutations (instant UX)
- ✅ Client-side search MVP with backend migration path (documented in research.md)
- ✅ Essential keyboard shortcuts only (5 shortcuts: save, new note, search, project switch, close)

**Ready for Task Breakdown**: Run `/speckit.tasks` to generate implementation tasks.

---

## Next Steps

1. ✅ Phase 0 Research complete ([research.md](./research.md))
2. ✅ Phase 1 Design complete ([data-model.md](./data-model.md), [contracts/](./contracts/), [quickstart.md](./quickstart.md))
3. ⏸️ Update agent context: Run `.specify/scripts/powershell/update-agent-context.ps1 -AgentType copilot`
4. ✅ Constitution re-check passed
5. 🚀 **READY**: Run `/speckit.tasks` to generate task breakdown from this plan

**Branch**: `001-frontend-implementation`  
**Planning Artifacts**: All Phase 0-1 deliverables complete in `specs/001-frontend-implementation/`
