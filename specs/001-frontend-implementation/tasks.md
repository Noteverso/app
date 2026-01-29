# Tasks: Complete Frontend Implementation

**Input**: Design documents from `/specs/001-frontend-implementation/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/  
**Generated**: 2026-01-29

**Tests**: No test tasks included - tests not explicitly requested in feature specification. Implementation tasks only.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **Checkbox**: `- [ ]` for markdown task tracking
- **[ID]**: Sequential task ID (T001, T002, ...)
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: User story label (US1, US2, US3...) - ONLY for user story phases
- Include exact file paths in descriptions

## Path Conventions

- **Frontend**: `frontend/web/src/`
- **Backend**: No changes (already implemented)
- All paths relative to repository root: `E:\code\personal\app\`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and TanStack Query configuration

- [ ] T001 Verify all dependencies installed from package.json (React 18.2+, TypeScript 5.x, TipTap 2.5+, TanStack Query 5.51+, React Router 6.x, Radix UI, Tailwind CSS)
- [ ] T002 Configure TanStack Query client with global defaults in frontend/web/src/lib/query-client.ts (staleTime: 5min, cacheTime: 30min, retry: 3)
- [ ] T003 [P] Create QueryClientProvider wrapper in frontend/web/src/app.tsx
- [ ] T004 [P] Configure Tailwind CSS mobile-first breakpoints in frontend/web/tailwind.config.ts (320px, 768px, 1024px, 1440px, 2560px)
- [ ] T005 [P] Create CSS custom properties for responsive spacing in frontend/web/src/styles/variables.css (touch targets 44px+, mobile margins)
- [ ] T006 [P] Setup Vitest configuration for unit tests in frontend/web/vitest.config.ts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T007 Create base HTTP client with axios interceptors in frontend/web/src/lib/http.ts (auth headers, error handling, request/response logging)
- [ ] T008 [P] Create authentication context provider in frontend/web/src/contexts/auth-provider.tsx (replace fake-auth-provider.ts with real implementation)
- [ ] T009 [P] Create useAuth hook in frontend/web/src/hooks/use-auth.ts (login, logout, session check, user state)
- [ ] T010 [P] Create protected route wrapper component in frontend/web/src/routes/protected-route.tsx (redirect to /login if unauthenticated)
- [ ] T011 [P] Create base TypeScript interfaces for block-based content in frontend/web/src/types/note.ts (ContentBlock, ContentNode, NoteContent per data-model.md)
- [ ] T012 [P] Create TypeScript interfaces for Project in frontend/web/src/types/project.ts (FullProject, NewProject, UpdateProject, PROJECT_COLORS)
- [ ] T013 [P] Create TypeScript interfaces for Label in frontend/web/src/types/label.ts (FullLabel, NewLabel, UpdateLabel, LabelBadge)
- [ ] T014 [P] Create TypeScript interfaces for User in frontend/web/src/types/user.ts (User, UserResponse, LoginCredentials, RegisterCredentials)
- [ ] T015 [P] Create keyboard shortcuts hook in frontend/web/src/hooks/use-keyboard-shortcuts.ts (Ctrl+S, Ctrl+N, Ctrl+K, Ctrl+P, Esc with Mac/Windows detection)
- [ ] T016 [P] Create infinite scroll hook in frontend/web/src/hooks/use-infinite-scroll.ts (intersection observer wrapper for TanStack Query useInfiniteQuery)
- [ ] T017 [P] Create base Button component with mobile-first styles in frontend/web/src/components/ui/button.tsx (44px+ touch targets, loading states)
- [ ] T018 [P] Create base Dialog component from Radix UI in frontend/web/src/components/ui/dialog.tsx (accessible, keyboard navigable, mobile-friendly)
- [ ] T019 [P] Create base Input component in frontend/web/src/components/ui/input.tsx (mobile-optimized, validation states)
- [ ] T020 [P] Create base Select component from Radix UI in frontend/web/src/components/ui/select.tsx (mobile-friendly dropdowns, touch interactions)
- [ ] T021 [P] Create Toast notification system in frontend/web/src/components/ui/toast.tsx (for optimistic update rollback feedback)
- [ ] T022 Create error boundary component in frontend/web/src/components/error-boundary.tsx (catch React errors, display fallback UI)
- [ ] T023 [P] Create loading skeleton components in frontend/web/src/components/ui/skeleton.tsx (note card skeleton, project list skeleton, mobile-optimized)
- [ ] T024 [P] Create helper function extractPlainText in frontend/web/src/lib/note-utils.ts (extract text from block-based JSON for previews/search)
- [ ] T025 Configure React Router routes structure in frontend/web/src/routes/routes.tsx (add protected routes, layout hierarchy, loaders)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 6 - User Authentication and Profile (Priority: P1) 🎯 MVP Foundation

**Goal**: Users must register with email, log in to access notes, and log out securely. Essential for data isolation.

**Independent Test**: Register new account → Login → Verify session persists → Logout → Verify redirect to login when accessing protected routes

### Implementation for User Story 6

- [ ] T026 [P] [US6] Create user API client in frontend/web/src/api/user/index.ts (registerUser, loginUser, logoutUser, getCurrentUser, checkSession)
- [ ] T027 [P] [US6] Create useRegister mutation hook in frontend/web/src/hooks/use-register.ts (TanStack Query mutation, validation, error handling)
- [ ] T028 [P] [US6] Create useLogin mutation hook in frontend/web/src/hooks/use-login.ts (TanStack Query mutation, set auth context on success)
- [ ] T029 [P] [US6] Create useLogout mutation hook in frontend/web/src/hooks/use-logout.ts (TanStack Query mutation, clear auth context)
- [ ] T030 [P] [US6] Create useCurrentUser query hook in frontend/web/src/hooks/use-current-user.ts (fetch user data, session validation)
- [ ] T031 [US6] Create register page component in frontend/web/src/pages/auth/register-page.tsx (React Hook Form, email/password validation, mobile-responsive form)
- [ ] T032 [US6] Create login page component in frontend/web/src/pages/auth/login-page.tsx (React Hook Form, remember me option, mobile-responsive form)
- [ ] T033 [US6] Update authentication context provider in frontend/web/src/contexts/auth-provider.tsx (integrate real API hooks, session persistence with localStorage)
- [ ] T034 [US6] Update protected route wrapper in frontend/web/src/routes/protected-route.tsx (check auth status, redirect logic, loading state)
- [ ] T035 [US6] Add login/register routes to React Router config in frontend/web/src/routes/routes.tsx (/login, /register paths)
- [ ] T036 [US6] Create logout button in header component frontend/web/src/layout/header/header.tsx (trigger useLogout, redirect to login)
- [ ] T037 [US6] Add session expiry handling in frontend/web/src/lib/http.ts (401 interceptor, redirect to login, toast notification)

**Checkpoint**: Authentication complete - users can register, login, logout. Session management working.

---

## Phase 4: User Story 2 - Project Organization and Management (Priority: P1) 🎯 MVP Core

**Goal**: Users create custom projects to organize notes, view projects in sidebar, manage properties (name, color, favorites), archive/delete projects.

**Independent Test**: Create new project with color → Mark as favorite → View project in sidebar → Rename project → Archive project → Delete empty project

### Implementation for User Story 2

- [ ] T038 [P] [US2] Create project API client in frontend/web/src/api/project/index.ts (createProject, updateProject, deleteProject, getProjects, getProjectById, archiveProject, unarchiveProject, toggleFavorite)
- [ ] T039 [P] [US2] Create useProjects query hook in frontend/web/src/hooks/use-projects.ts (TanStack Query, fetch all projects, cache by user)
- [ ] T040 [P] [US2] Create useCreateProject mutation hook in frontend/web/src/hooks/use-create-project.ts (optimistic update, rollback on error, toast notification)
- [ ] T041 [P] [US2] Create useUpdateProject mutation hook in frontend/web/src/hooks/use-update-project.ts (optimistic update, rollback on error)
- [ ] T042 [P] [US2] Create useDeleteProject mutation hook in frontend/web/src/hooks/use-delete-project.ts (optimistic update, rollback on error, validate empty project)
- [ ] T043 [P] [US2] Create useToggleProjectFavorite mutation hook in frontend/web/src/hooks/use-toggle-project-favorite.ts (optimistic update)
- [ ] T044 [P] [US2] Create useArchiveProject mutation hook in frontend/web/src/hooks/use-archive-project.ts (optimistic update)
- [ ] T045 [US2] Create ProjectList component in frontend/web/src/components/project/project-list.tsx (render projects, favorites first, note counts, mobile-responsive list)
- [ ] T046 [US2] Create ProjectListItem component in frontend/web/src/components/project/project-list-item.tsx (project name, color indicator, favorite star, note count, touch-optimized 44px+ height)
- [ ] T047 [US2] Create CreateProjectDialog component in frontend/web/src/components/project/create-project-dialog.tsx (React Hook Form, color picker, name validation, mobile-friendly dialog)
- [ ] T048 [US2] Create EditProjectDialog component in frontend/web/src/components/project/edit-project-dialog.tsx (React Hook Form, update name/color, mobile-friendly)
- [ ] T049 [US2] Create DeleteProjectConfirmDialog component in frontend/web/src/components/project/delete-project-confirm-dialog.tsx (warn if project has notes, confirm action)
- [ ] T050 [US2] Create ProjectColorPicker component in frontend/web/src/components/project/project-color-picker.tsx (10 predefined colors, mobile touch-friendly selection)
- [ ] T051 [US2] Update nav sidebar component in frontend/web/src/layout/nav/nav-sidebar.tsx (integrate ProjectList, collapsible on mobile, 320px breakpoint)
- [ ] T052 [US2] Create project detail page in frontend/web/src/pages/project/project-detail-page.tsx (show project name as title, placeholder for note list)
- [ ] T053 [US2] Add project routes to React Router config in frontend/web/src/routes/routes.tsx (/projects/:projectId)
- [ ] T054 [US2] Add project loaders in frontend/web/src/routes/loader.ts (prefetch projects, handle project not found)

**Checkpoint**: Project management complete - users can create, edit, delete, organize projects. Sidebar navigation working.

---

## Phase 5: User Story 1 - Complete Note Management (Priority: P1) 🎯 MVP Core

**Goal**: Users create, view, edit, update, delete notes with rich text formatting. Notes belong to one project, can have multiple labels.

**Independent Test**: Create new note with TipTap editor → Edit block-based content → Move to different project → Add labels → Delete note → Verify infinite scroll loads more notes

### Implementation for User Story 1

- [ ] T055 [P] [US1] Create note API client in frontend/web/src/api/note/index.ts (createNote, updateNote, deleteNote, getNotes with pagination, getNoteById, pinNote, archiveNote, restoreNote, permanentDelete)
- [ ] T056 [P] [US1] Create useNotes infinite query hook in frontend/web/src/hooks/use-notes.ts (TanStack Query useInfiniteQuery, pagination params, filter by project/label/status)
- [ ] T057 [P] [US1] Create useCreateNote mutation hook in frontend/web/src/hooks/use-create-note.ts (optimistic update, add to cache, rollback on error with toast)
- [ ] T058 [P] [US1] Create useUpdateNote mutation hook in frontend/web/src/hooks/use-update-note.ts (optimistic update, rollback on error with toast)
- [ ] T059 [P] [US1] Create useDeleteNote mutation hook in frontend/web/src/hooks/use-delete-note.ts (optimistic remove from list, soft delete, rollback on error)
- [ ] T060 [P] [US1] Create usePinNote mutation hook in frontend/web/src/hooks/use-pin-note.ts (optimistic reorder, move to top of list)
- [ ] T061 [P] [US1] Create useArchiveNote mutation hook in frontend/web/src/hooks/use-archive-note.ts (optimistic remove from active view)
- [ ] T062 [US1] Create TipTap editor configuration in frontend/web/src/features/editor/tiptap-config.ts (configure extensions: StarterKit, Link, CodeBlock, Image, heading levels, list extensions)
- [ ] T063 [US1] Create TipTap editor component in frontend/web/src/features/editor/tiptap-editor.tsx (controlled component, block-based JSON input/output, mobile-optimized toolbar, touch-friendly formatting buttons)
- [ ] T064 [US1] Create editor toolbar component in frontend/web/src/features/editor/editor-toolbar.tsx (bold, italic, headings, lists, code, link, mobile collapsible, 44px+ buttons)
- [ ] T065 [US1] Create note-to-JSON converter in frontend/web/src/lib/tiptap-utils.ts (convert TipTap document to block-based JSON structure per data-model.md)
- [ ] T066 [US1] Create JSON-to-note converter in frontend/web/src/lib/tiptap-utils.ts (convert block-based JSON to TipTap document format)
- [ ] T067 [US1] Create NoteCard component in frontend/web/src/components/note/note-card.tsx (display note preview, labels, project, metadata, mobile-responsive, touch-optimized)
- [ ] T068 [US1] Create NoteList component in frontend/web/src/components/note/note-list.tsx (render notes with infinite scroll, pinned notes first, loading skeleton, mobile-optimized)
- [ ] T069 [US1] Create CreateNoteDialog component in frontend/web/src/components/note/create-note-dialog.tsx (TipTap editor, project selector, label multi-select, save with Ctrl+S, mobile-friendly dialog)
- [ ] T070 [US1] Create EditNoteDialog component in frontend/web/src/components/note/edit-note-dialog.tsx (TipTap editor, update content/project/labels, auto-save on blur, mobile-friendly)
- [ ] T071 [US1] Create NoteDetailView component in frontend/web/src/components/note/note-detail-view.tsx (full note display, metadata, actions toolbar, mobile-responsive)
- [ ] T072 [US1] Create note actions menu component in frontend/web/src/components/note/note-actions-menu.tsx (pin, archive, delete, move to project, mobile-friendly dropdown)
- [ ] T073 [US1] Integrate keyboard shortcuts in note components (Ctrl+S save, Ctrl+N new note, Esc close dialog - use useKeyboardShortcuts hook)
- [ ] T074 [US1] Create note list page in frontend/web/src/pages/home/home-page.tsx (default view, inbox notes, infinite scroll integration)
- [ ] T075 [US1] Create project notes page in frontend/web/src/pages/project/project-notes-page.tsx (filter notes by projectId, reuse NoteList)
- [ ] T076 [US1] Add note routes to React Router config in frontend/web/src/routes/routes.tsx (/notes, /notes/:noteId)
- [ ] T077 [US1] Add note loaders in frontend/web/src/routes/loader.ts (prefetch notes for project/label/inbox, pagination)
- [ ] T078 [US1] Add infinite scroll integration in note list pages (use useInfiniteScroll hook, trigger fetchNextPage, loading indicator at bottom)

**Checkpoint**: Note management complete - users can create, edit, delete notes with rich text. Infinite scroll working. Optimistic updates with rollback.

---

## Phase 6: User Story 3 - Label System for Cross-Project Organization (Priority: P2)

**Goal**: Users create labels to organize notes across projects, view all notes with a label, manage label properties.

**Independent Test**: Create new label → Apply to notes in different projects → Click label to view all labeled notes → Rename label → Delete label

### Implementation for User Story 3

- [ ] T079 [P] [US3] Create label API client in frontend/web/src/api/label/index.ts (createLabel, updateLabel, deleteLabel, getLabels, toggleFavorite)
- [ ] T080 [P] [US3] Create useLabels query hook in frontend/web/src/hooks/use-labels.ts (TanStack Query, fetch all labels, cache by user)
- [ ] T081 [P] [US3] Create useCreateLabel mutation hook in frontend/web/src/hooks/use-create-label.ts (optimistic update, rollback on error with toast)
- [ ] T082 [P] [US3] Create useUpdateLabel mutation hook in frontend/web/src/hooks/use-update-label.ts (optimistic update, reflect on all labeled notes)
- [ ] T083 [P] [US3] Create useDeleteLabel mutation hook in frontend/web/src/hooks/use-delete-label.ts (optimistic update, remove from all notes, rollback on error)
- [ ] T084 [P] [US3] Create useToggleLabelFavorite mutation hook in frontend/web/src/hooks/use-toggle-label-favorite.ts (optimistic update)
- [ ] T085 [US3] Create LabelList component in frontend/web/src/components/label/label-list.tsx (render labels, favorites first, note counts, mobile-responsive)
- [ ] T086 [US3] Create LabelListItem component in frontend/web/src/components/label/label-list-item.tsx (label name, favorite star, note count, touch-optimized)
- [ ] T087 [US3] Create LabelBadge component in frontend/web/src/components/label/label-badge.tsx (small label display for note cards, removable, mobile-friendly)
- [ ] T088 [US3] Create CreateLabelDialog component in frontend/web/src/components/label/create-label-dialog.tsx (React Hook Form, name validation, mobile-friendly)
- [ ] T089 [US3] Create EditLabelDialog component in frontend/web/src/components/label/edit-label-dialog.tsx (React Hook Form, update name, mobile-friendly)
- [ ] T090 [US3] Create DeleteLabelConfirmDialog component in frontend/web/src/components/label/delete-label-confirm-dialog.tsx (warn about removal from notes, confirm)
- [ ] T091 [US3] Create LabelMultiSelect component in frontend/web/src/components/label/label-multi-select.tsx (Radix UI Checkbox group, search labels, mobile touch-friendly)
- [ ] T092 [US3] Update nav sidebar in frontend/web/src/layout/nav/nav-sidebar.tsx (add labels section below projects, collapsible on mobile)
- [ ] T093 [US3] Update NoteCard component in frontend/web/src/components/note/note-card.tsx (display LabelBadge components for each label)
- [ ] T094 [US3] Update CreateNoteDialog in frontend/web/src/components/note/create-note-dialog.tsx (integrate LabelMultiSelect)
- [ ] T095 [US3] Update EditNoteDialog in frontend/web/src/components/note/edit-note-dialog.tsx (integrate LabelMultiSelect, optimistic label add/remove)
- [ ] T096 [US3] Create label notes page in frontend/web/src/pages/label/label-notes-page.tsx (filter notes by labelId, show notes across all projects, reuse NoteList)
- [ ] T097 [US3] Create label management page in frontend/web/src/pages/label/label-management-page.tsx (view all labels, create/edit/delete actions)
- [ ] T098 [US3] Add label routes to React Router config in frontend/web/src/routes/routes.tsx (/labels, /labels/:labelId)
- [ ] T099 [US3] Add label loaders in frontend/web/src/routes/loader.ts (prefetch labels, fetch notes by label with pagination)

**Checkpoint**: Label system complete - users can create, edit, delete labels. Labels work across projects. Note filtering by label working.

---

## Phase 7: User Story 4 - Note Filtering and Search (Priority: P2)

**Goal**: Users filter notes by status (pinned, archived, deleted) and search note content to find information quickly.

**Independent Test**: Pin note → View pinned filter → Archive note → View archived filter → Move note to trash → View trash → Search for text in notes → Verify results

### Implementation for User Story 4

- [ ] T100 [P] [US4] Create client-side search utility in frontend/web/src/lib/search-utils.ts (filter notes by text content, search in block-based JSON, highlight matching blocks)
- [ ] T101 [P] [US4] Create useNoteSearch hook in frontend/web/src/hooks/use-note-search.ts (client-side filtering of loaded notes, debounced search input, performance optimized)
- [ ] T102 [US4] Create SearchBar component in frontend/web/src/components/search/search-bar.tsx (input with icon, debounced onChange, keyboard shortcut Ctrl+K, mobile-optimized)
- [ ] T103 [US4] Create SearchResultsList component in frontend/web/src/components/search/search-results-list.tsx (display matching notes, highlight search terms, mobile-responsive)
- [ ] T104 [US4] Create SearchHighlight component in frontend/web/src/components/search/search-highlight.tsx (highlight matching text in note previews)
- [ ] T105 [US4] Create NoteStatusFilter component in frontend/web/src/components/note/note-status-filter.tsx (tabs/buttons for All, Pinned, Archived, Deleted, mobile-friendly)
- [ ] T106 [US4] Update useNotes hook in frontend/web/src/hooks/use-notes.ts (add status filter params: all, pinned, archived, deleted)
- [ ] T107 [US4] Create trash page in frontend/web/src/pages/trash/trash-page.tsx (view deleted notes, restore/permanent delete actions, mobile-responsive)
- [ ] T108 [US4] Create archived notes page in frontend/web/src/pages/archived/archived-notes-page.tsx (view archived notes, unarchive action, mobile-responsive)
- [ ] T109 [US4] Integrate SearchBar in header component frontend/web/src/layout/header/header.tsx (global search, collapsible on mobile, search icon button)
- [ ] T110 [US4] Integrate NoteStatusFilter in home page frontend/web/src/pages/home/home-page.tsx (filter current view)
- [ ] T111 [US4] Integrate NoteStatusFilter in project notes page frontend/web/src/pages/project/project-notes-page.tsx (filter by status within project)
- [ ] T112 [US4] Add trash and archived routes to React Router config in frontend/web/src/routes/routes.tsx (/trash, /archived)
- [ ] T113 [US4] Create keyboard shortcut handler for search (Ctrl+K) in app.tsx (focus search bar, open search dialog on mobile)

**Checkpoint**: Search and filtering complete - users can search notes by text, filter by status (pinned, archived, deleted). Trash and archived views working.

---

## Phase 8: User Story 5 - Note Linking and Attachments (Priority: P3)

**Goal**: Users link notes together to create relationships and attach files/images to notes for richer content.

**Independent Test**: Create note link → Click link to navigate → View backlinks panel on target note → Upload attachment → View attachment inline → Delete attachment

### Implementation for User Story 5

- [ ] T114 [P] [US5] Create attachment API client in frontend/web/src/api/attachment/index.ts (uploadAttachment, deleteAttachment, getAttachments, downloadAttachment)
- [ ] T115 [P] [US5] Create note link API client in frontend/web/src/api/note/note-links.ts (createNoteLink, deleteNoteLink, getBacklinks, getForwardLinks)
- [ ] T116 [P] [US5] Create useCreateNoteLink mutation hook in frontend/web/src/hooks/use-create-note-link.ts (optimistic update)
- [ ] T117 [P] [US5] Create useDeleteNoteLink mutation hook in frontend/web/src/hooks/use-delete-note-link.ts (optimistic update)
- [ ] T118 [P] [US5] Create useBacklinks query hook in frontend/web/src/hooks/use-backlinks.ts (fetch notes that reference current note)
- [ ] T119 [P] [US5] Create useUploadAttachment mutation hook in frontend/web/src/hooks/use-upload-attachment.ts (upload file, progress tracking, size validation)
- [ ] T120 [P] [US5] Create useDeleteAttachment mutation hook in frontend/web/src/hooks/use-delete-attachment.ts (optimistic update)
- [ ] T121 [US5] Create TipTap note link extension in frontend/web/src/features/editor/extensions/note-link-extension.ts (custom extension for [[note-title]] syntax, autocomplete)
- [ ] T122 [US5] Create TipTap image extension configuration in frontend/web/src/features/editor/extensions/image-extension.ts (inline image display, resize handles)
- [ ] T123 [US5] Create NoteLinkAutocomplete component in frontend/web/src/components/note/note-link-autocomplete.tsx (search notes as you type [[, mobile-friendly dropdown)
- [ ] T124 [US5] Create BacklinksPanel component in frontend/web/src/components/note/backlinks-panel.tsx (show list of notes that reference current note, clickable links, mobile-responsive)
- [ ] T125 [US5] Create AttachmentUpload component in frontend/web/src/components/attachment/attachment-upload.tsx (drag-drop zone, file picker, progress bar, mobile file picker)
- [ ] T126 [US5] Create AttachmentList component in frontend/web/src/components/attachment/attachment-list.tsx (display attachments with icons, download button, delete action, mobile-responsive)
- [ ] T127 [US5] Create AttachmentPreview component in frontend/web/src/components/attachment/attachment-preview.tsx (inline image preview, file type icons, mobile-optimized)
- [ ] T128 [US5] Update TipTap editor component in frontend/web/src/features/editor/tiptap-editor.tsx (integrate note link extension, image extension, attachment handling)
- [ ] T129 [US5] Update NoteDetailView component in frontend/web/src/components/note/note-detail-view.tsx (add BacklinksPanel at bottom, show attachments)
- [ ] T130 [US5] Update CreateNoteDialog in frontend/web/src/components/note/create-note-dialog.tsx (add attachment upload option)
- [ ] T131 [US5] Update EditNoteDialog in frontend/web/src/components/note/edit-note-dialog.tsx (add attachment management, note linking)
- [ ] T132 [US5] Create attachments page in frontend/web/src/pages/attachment/attachment-page.tsx (view all attachments across notes, filter by type, mobile-responsive)
- [ ] T133 [US5] Add attachment routes to React Router config in frontend/web/src/routes/routes.tsx (/attachments)

**Checkpoint**: Note linking and attachments complete - users can create inline note links, view backlinks, upload/view/delete attachments.

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories, mobile optimization, keyboard shortcuts polish, performance

- [ ] T134 [P] Add loading states to all mutation buttons (disable while loading, show spinner, prevent double-click)
- [ ] T135 [P] Add error handling and user-friendly error messages for all API calls (network errors, validation errors, server errors)
- [ ] T136 [P] Implement toast notifications for all optimistic update rollbacks (use Toast component from Phase 2)
- [ ] T137 [P] Add mobile responsive meta tags in frontend/web/index.html (viewport, touch icons, theme color)
- [ ] T138 [P] Optimize mobile navigation (hamburger menu on <768px, slide-out drawer, touch gestures)
- [ ] T139 [P] Add mobile-specific styles in frontend/web/src/styles/layout.css (collapsible sidebar, bottom navigation option, safe area insets)
- [ ] T140 [P] Implement touch gestures for note cards on mobile (swipe to delete, swipe to pin, long-press for context menu)
- [ ] T141 [P] Optimize TipTap editor for mobile (virtual keyboard handling, toolbar positioning, selection handles)
- [ ] T142 [P] Add performance monitoring (measure page load times, infinite scroll latency, editor typing latency)
- [ ] T143 [P] Optimize bundle size (code splitting by route, lazy load TipTap editor, tree-shake unused Radix components)
- [ ] T144 [P] Add service worker for offline asset caching in frontend/web/src/service-worker.ts (cache static assets, fonts, images)
- [ ] T145 [P] Implement keyboard shortcut help dialog (show all shortcuts, triggered by Ctrl+/ or ?)
- [ ] T146 [P] Add empty states for all lists (empty inbox, no projects, no labels, no search results, mobile-friendly illustrations)
- [ ] T147 [P] Add confirmation dialogs for destructive actions (delete note, delete project with notes, permanent delete from trash)
- [ ] T148 [P] Implement form validation error messages for all dialogs (inline field errors, mobile-friendly error display)
- [ ] T149 [P] Add accessibility improvements (ARIA labels, keyboard navigation, focus management, screen reader testing)
- [ ] T150 [P] Add dark mode support (CSS custom properties, theme toggle, persist preference, mobile-friendly toggle)
- [ ] T151 [P] Test and fix mobile browser compatibility (iOS Safari touch issues, Android Chrome keyboard issues, viewport height issues)
- [ ] T152 [P] Add analytics tracking for key user actions (note created, project created, label applied, search performed)
- [ ] T153 [P] Create end-to-end test for quickstart.md workflows (register → create project → create note → search → delete)
- [ ] T154 [P] Update documentation in README.md (setup instructions, mobile development guide, keyboard shortcuts reference)
- [ ] T155 Run quickstart.md validation (verify all workflows work on desktop and mobile viewports)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 6 - Authentication (Phase 3)**: Depends on Foundational phase - BLOCKS all other user stories (auth required)
- **User Story 2 - Projects (Phase 4)**: Depends on Authentication - can proceed after US6
- **User Story 1 - Notes (Phase 5)**: Depends on Authentication AND Projects (notes need projects) - can proceed after US6 and US2
- **User Story 3 - Labels (Phase 6)**: Depends on Authentication and Notes - can proceed after US6 and US1
- **User Story 4 - Search/Filter (Phase 7)**: Depends on Notes and Labels - can proceed after US1 and US3
- **User Story 5 - Linking/Attachments (Phase 8)**: Depends on Notes - can proceed after US1
- **Polish (Phase 9)**: Depends on all desired user stories being complete

### User Story Dependencies

```
Phase 1: Setup (T001-T006)
    ↓
Phase 2: Foundational (T007-T025) ← BLOCKS EVERYTHING
    ↓
Phase 3: US6 Authentication (T026-T037) ← BLOCKS ALL FEATURE WORK
    ↓
    ├─→ Phase 4: US2 Projects (T038-T054)
    │       ↓
    │       └─→ Phase 5: US1 Notes (T055-T078) ← Needs Projects
    │               ↓
    │               ├─→ Phase 6: US3 Labels (T079-T099)
    │               │       ↓
    │               │       └─→ Phase 7: US4 Search/Filter (T100-T113)
    │               │
    │               └─→ Phase 8: US5 Linking/Attachments (T114-T133)
    │
    └─→ All complete → Phase 9: Polish (T134-T155)
```

**Critical Path**: Setup → Foundational → Authentication → Projects → Notes → Labels → Search → Polish

**Minimum Viable Product (MVP)**: Phase 1 + Phase 2 + Phase 3 (Auth) + Phase 4 (Projects) + Phase 5 (Notes) = Users can register, login, create projects, create/edit/delete notes with rich text

### Within Each User Story

#### US6 Authentication (Phase 3):
1. API client + hooks can be built in parallel (T026-T030 all [P])
2. Pages require hooks complete first (T031-T032 depend on T026-T030)
3. Integration tasks last (T033-T037)

#### US2 Projects (Phase 4):
1. API client + hooks can be built in parallel (T038-T044 all [P])
2. Components can be built in parallel (T045-T050 all [P] after hooks)
3. Integration tasks last (T051-T054)

#### US1 Notes (Phase 5):
1. API client + hooks can be built in parallel (T055-T061 all [P])
2. TipTap config can be built in parallel with hooks (T062, T063 [P])
3. Editor components depend on config (T064-T066)
4. Note components can be built after editor ready (T067-T072)
5. Integration tasks last (T073-T078)

#### US3 Labels (Phase 6):
1. API client + hooks in parallel (T079-T084 all [P])
2. Components in parallel (T085-T091)
3. Integration tasks last (T092-T099)

#### US4 Search/Filter (Phase 7):
1. Search utils + hooks in parallel (T100-T101 [P])
2. Components in parallel (T102-T105)
3. Integration tasks last (T106-T113)

#### US5 Linking/Attachments (Phase 8):
1. API clients + hooks in parallel (T114-T120 all [P])
2. Extensions and components in parallel (T121-T127)
3. Integration tasks last (T128-T133)

### Parallel Opportunities

**Within Setup (Phase 1)**: T003, T004, T005, T006 can all run in parallel (different files)

**Within Foundational (Phase 2)**: 
- T008-T024 all marked [P] - can run in parallel (different files, UI components independent)
- Must complete T007 (HTTP client) first before API hooks

**Across User Stories** (if team has capacity):
- After Foundational + Auth complete:
  - Team Member 1: Work on US2 Projects (Phase 4)
  - Team Member 2: Work on documentation
- After Projects complete:
  - Team Member 1: Work on US1 Notes (Phase 5)
  - Team Member 2: Work on mobile styles polish
- After Notes complete:
  - Team Member 1: Work on US3 Labels (Phase 6)
  - Team Member 2: Work on US5 Linking (Phase 8)
  - Team Member 3: Work on US4 Search (Phase 7)

**Within Polish (Phase 9)**: Almost all tasks marked [P] - can run in parallel (different concerns)

---

## Parallel Example: User Story 1 (Notes)

### Step 1: Launch all API hooks in parallel
```bash
# All independent, different files:
Task T055: "Create note API client in frontend/web/src/api/note/index.ts"
Task T056: "Create useNotes infinite query hook in frontend/web/src/hooks/use-notes.ts"
Task T057: "Create useCreateNote mutation hook in frontend/web/src/hooks/use-create-note.ts"
Task T058: "Create useUpdateNote mutation hook in frontend/web/src/hooks/use-update-note.ts"
Task T059: "Create useDeleteNote mutation hook in frontend/web/src/hooks/use-delete-note.ts"
Task T060: "Create usePinNote mutation hook in frontend/web/src/hooks/use-pin-note.ts"
Task T061: "Create useArchiveNote mutation hook in frontend/web/src/hooks/use-archive-note.ts"
```

### Step 2: Launch TipTap configuration in parallel with Step 1
```bash
# Independent of hooks:
Task T062: "Create TipTap editor configuration in frontend/web/src/features/editor/tiptap-config.ts"
Task T063: "Create TipTap editor component in frontend/web/src/features/editor/tiptap-editor.tsx"
```

### Step 3: After TipTap ready, launch editor utilities
```bash
# Depends on TipTap config:
Task T064: "Create editor toolbar component in frontend/web/src/features/editor/editor-toolbar.tsx"
Task T065: "Create note-to-JSON converter in frontend/web/src/lib/tiptap-utils.ts"
Task T066: "Create JSON-to-note converter in frontend/web/src/lib/tiptap-utils.ts"
```

### Step 4: After hooks + editor ready, launch note components in parallel
```bash
# All independent:
Task T067: "Create NoteCard component in frontend/web/src/components/note/note-card.tsx"
Task T068: "Create NoteList component in frontend/web/src/components/note/note-list.tsx"
Task T069: "Create CreateNoteDialog component in frontend/web/src/components/note/create-note-dialog.tsx"
Task T070: "Create EditNoteDialog component in frontend/web/src/components/note/edit-note-dialog.tsx"
Task T071: "Create NoteDetailView component in frontend/web/src/components/note/note-detail-view.tsx"
Task T072: "Create note actions menu component in frontend/web/src/components/note/note-actions-menu.tsx"
```

### Step 5: Integration tasks (sequential)
```bash
# Must complete components first:
Task T073: "Integrate keyboard shortcuts in note components"
Task T074: "Create note list page in frontend/web/src/pages/home/home-page.tsx"
Task T075: "Create project notes page in frontend/web/src/pages/project/project-notes-page.tsx"
Task T076: "Add note routes to React Router config"
Task T077: "Add note loaders in frontend/web/src/routes/loader.ts"
Task T078: "Add infinite scroll integration in note list pages"
```

---

## Implementation Strategy

### MVP First (Phases 1-5: Auth + Projects + Notes Only)

**Goal**: Get basic note-taking working as fast as possible

1. **Phase 1: Setup** (1 hour)
   - T001-T006: Install dependencies, configure TanStack Query, Tailwind, Vitest
   
2. **Phase 2: Foundational** (4-6 hours)
   - T007-T025: HTTP client, auth context, base types, keyboard shortcuts, UI components
   - **Checkpoint**: Foundation ready ✓
   
3. **Phase 3: Authentication** (3-4 hours)
   - T026-T037: User registration, login, logout, session management
   - **Checkpoint**: Auth working ✓
   
4. **Phase 4: Projects** (4-6 hours)
   - T038-T054: Project CRUD, sidebar navigation, favorites
   - **Checkpoint**: Project organization working ✓
   
5. **Phase 5: Notes** (8-12 hours)
   - T055-T078: TipTap editor, block-based JSON, note CRUD, infinite scroll, optimistic updates
   - **Checkpoint**: Notes working ✓
   
**STOP and VALIDATE MVP**: Users can register, login, create projects, create/edit/delete notes with rich text.

**Estimated MVP Time**: 20-29 hours of focused development

### Incremental Delivery (Add Features One by One)

After MVP is validated:

6. **Phase 6: Labels** (4-6 hours)
   - T079-T099: Label CRUD, cross-project organization, label filtering
   - **Deploy/Demo**: Users can now tag notes across projects
   
7. **Phase 7: Search/Filter** (3-5 hours)
   - T100-T113: Client-side search, status filters, trash, archived views
   - **Deploy/Demo**: Users can search and filter notes
   
8. **Phase 8: Linking/Attachments** (6-8 hours)
   - T114-T133: Note links, backlinks, file attachments, inline images
   - **Deploy/Demo**: Users can link notes and attach files
   
9. **Phase 9: Polish** (6-10 hours)
   - T134-T155: Mobile optimization, error handling, dark mode, accessibility
   - **Deploy/Demo**: Production-ready polish

**Total Estimated Time**: 39-58 hours for complete implementation

### Parallel Team Strategy

If you have 3 developers:

**Week 1: Foundation (Everyone Together)**
- Days 1-2: Phase 1 Setup + Phase 2 Foundational (T001-T025)
- Days 3-4: Phase 3 Authentication (T026-T037)
- Day 5: Phase 4 Projects (T038-T054)

**Week 2: Parallel User Stories**
- Developer A: Phase 5 Notes (T055-T078) - LONGEST, most complex
- Developer B: Phase 6 Labels (T079-T099)
- Developer C: Phase 7 Search/Filter (T100-T113)

**Week 3: Advanced Features + Polish**
- Developer A: Phase 8 Linking/Attachments (T114-T133)
- Developer B: Phase 9 Polish - Mobile (T137-T141)
- Developer C: Phase 9 Polish - Performance (T142-T144, T149-T151)

**Week 4: Final Polish + Testing**
- Everyone: Remaining polish tasks (T145-T155), integration testing, bug fixes

---

## Task Validation

### Completeness Check

✅ **Phase 1 (Setup)**: 6 tasks - Project initialization, TanStack Query config, Tailwind config, Vitest setup

✅ **Phase 2 (Foundational)**: 19 tasks - HTTP client, auth context, base types (Note, Project, Label, User), hooks (keyboard shortcuts, infinite scroll), UI components (Button, Dialog, Input, Select, Toast, Skeleton), error boundary, query client

✅ **Phase 3 (US6 Authentication)**: 12 tasks - User API, hooks (register, login, logout, current user), pages (register, login), auth context integration, protected routes, session management

✅ **Phase 4 (US2 Projects)**: 17 tasks - Project API, hooks (CRUD, favorite, archive), components (list, item, dialogs, color picker), sidebar integration, routes

✅ **Phase 5 (US1 Notes)**: 24 tasks - Note API, hooks (infinite query, CRUD, pin, archive), TipTap editor (config, component, toolbar, converters), note components (card, list, dialogs, detail view, actions), keyboard shortcuts, infinite scroll, routes

✅ **Phase 6 (US3 Labels)**: 21 tasks - Label API, hooks (CRUD, favorite), components (list, item, badge, dialogs, multi-select), sidebar integration, note updates, routes

✅ **Phase 7 (US4 Search/Filter)**: 14 tasks - Search utils, hooks (note search), components (search bar, results, highlight, status filter), trash page, archived page, integrations, keyboard shortcut

✅ **Phase 8 (US5 Linking/Attachments)**: 20 tasks - Attachment API, note link API, hooks (links, attachments, backlinks), TipTap extensions (note link, image), components (autocomplete, backlinks panel, attachment upload/list/preview), editor updates, routes

✅ **Phase 9 (Polish)**: 22 tasks - Loading states, error handling, toast notifications, mobile optimization (responsive, navigation, gestures, editor), performance (monitoring, bundle optimization, service worker), keyboard shortcut help, empty states, confirmations, validation, accessibility, dark mode, compatibility, analytics, E2E tests, documentation

**Total Tasks**: 155 tasks covering all 6 user stories + setup + foundational + polish

### Independence Check

✅ **US6 Authentication**: Independently testable - can register, login, logout without any other features
✅ **US2 Projects**: Independently testable after auth - can create/manage projects without notes
✅ **US1 Notes**: Independently testable after auth + projects - can create/edit/delete notes with editor
✅ **US3 Labels**: Independently testable after notes - can create/apply labels to existing notes
✅ **US4 Search**: Independently testable after notes - can search/filter existing notes
✅ **US5 Linking**: Independently testable after notes - can link existing notes and attach files

### Coverage Check

✅ All 6 user stories from spec.md covered
✅ All entities from data-model.md covered (Note with block-based JSON, Project, Label, User, Attachment, NoteLink)
✅ All API contracts from contracts/ covered (note-api.md, project-api.md, label-api.md)
✅ All tech decisions from research.md implemented (TanStack Query, React Hook Form, TipTap, Radix UI, mobile-first, optimistic updates, client-side search)
✅ All mobile requirements covered (320px-2560px responsive, touch interactions, 44px+ targets, mobile-optimized components)
✅ All keyboard shortcuts covered (Ctrl+S, Ctrl+N, Ctrl+K, Ctrl+P, Esc)
✅ All optimistic update patterns covered (mutations with rollback + toast notifications)
✅ All block-based JSON requirements covered (TipTap integration, converters, storage)

---

## Notes

- **[P]** tasks within the same phase can be worked on in parallel by different developers or in any order
- **[Story]** label (US1-US6) maps each task to its user story for traceability
- Each user story is independently completable and testable
- Tests are NOT included (not requested in specification)
- Stop at any checkpoint to validate user story independently before proceeding
- Commit after each task or logical group of tasks
- Mobile-first approach throughout - design for 320px, enhance for desktop
- Optimistic updates on all mutations - instant UI feedback with rollback on error
- Block-based JSON content throughout - NO HTML strings
- TipTap 2.5+ for all rich text editing
- TanStack Query 5.51+ for all server state management
- React Hook Form for all dialog forms
- Radix UI for all accessible primitives
- Tailwind CSS for all styling with mobile-first utilities

**Branch**: `001-frontend-implementation`  
**Next Steps**: Start with Phase 1 (Setup), proceed sequentially through Foundational and Authentication, then implement user stories in priority order or in parallel with team members.