# Feature Specification: Complete Frontend Implementation for Note-Taking Application

**Feature Branch**: `001-frontend-implementation`  
**Created**: 2026-01-27  
**Status**: Draft  
**Input**: User description: "The project is a note-taking monorepo. The note-taking manage notes using projects and labels (aka tags). The data model is one to one for note and project and one to multiple for note and labels. Every user must login with email to use this app. The backend service has already implemented basical api, like creating,updating,deleting for note, project and labels. But The frontend doesn't implement much more. Please read the existing project and make the spec doc."

## Clarifications

### Session 2026-01-28

- Q: The specification states "desktop-only" but the implementation plan mandates mobile-first responsive design. Which approach should be implemented? → A: Mobile-first responsive design - 320px-2560px viewports, touch interactions, progressive enhancement to desktop
- Q: How should bidirectional note links be displayed and managed? → A: Inline links + separate backlinks section - Links appear within content as clickable text; backlinks shown in dedicated panel/section at bottom
- Q: Which note operations should use optimistic updates? → A: Mutations with instant rollback - Create, update, delete, pin, archive, label changes show instant UI updates; rollback on error with toast notification
- Q: What is the search implementation approach? → A: Client-side filtering (MVP), backend later - Filter loaded notes in browser initially; migrate to backend search when needed
- Q: What keyboard shortcuts are required for MVP? → A: Essential actions only - Save note (Ctrl+S), New note (Ctrl+N), Search (Ctrl+K), Quick project switch (Ctrl+P), Close/Escape dialogs (Esc)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Complete Note Management (Priority: P1)

Users need to create, view, edit, update, and delete notes within projects with rich text formatting capabilities. Each note belongs to exactly one project and can be tagged with multiple labels.

**Why this priority**: This is the core functionality of a note-taking application. Without complete note management, users cannot effectively capture and organize their thoughts.

**Independent Test**: Can be fully tested by logging in, creating a new note in the inbox or a project, editing its content, adding labels, moving it between projects, and deleting it. Delivers immediate value as users can manage their notes end-to-end.

**Acceptance Scenarios**:

1. **Given** I am viewing a project or inbox, **When** I type content in the text editor and click save, **Then** a new note is created with the specified content and appears at the top of the note list
2. **Given** I have an existing note, **When** I click on the note to expand it and modify the content, **Then** the note content is updated and the changes are saved immediately
3. **Given** I have a note, **When** I select a different project from the dropdown before saving, **Then** the note is created in or moved to the selected project
4. **Given** I have a note, **When** I delete the note, **Then** the note is removed from the visible list and moved to trash
5. **Given** I am creating or editing a note, **When** I add labels to the note, **Then** the labels are associated with the note and displayed on the note card
6. **Given** I have multiple notes in a project, **When** I scroll to the bottom of the list, **Then** more notes are loaded automatically (infinite scroll)
7. **Given** I have a note with formatting, **When** I view the note, **Then** the rich text formatting (bold, italic, lists, links, etc.) is preserved and displayed correctly

---

### User Story 2 - Project Organization and Management (Priority: P1)

Users need to create custom projects to organize their notes, view projects in a sidebar navigation, manage project properties (name, color, favorites), and archive or delete projects.

**Why this priority**: Projects are the primary organizational structure for notes. Without project management, users cannot organize their notes effectively beyond the inbox.

**Independent Test**: Can be fully tested by creating new projects, assigning colors, marking as favorites, viewing project-specific notes, archiving/unarchiving projects, and deleting empty projects. Delivers standalone value for note organization.

**Acceptance Scenarios**:

1. **Given** I am on the main application page, **When** I click the "create project" button and enter a project name and color, **Then** a new project is created and appears in the sidebar navigation
2. **Given** I have created projects, **When** I click on a project in the sidebar, **Then** I see all notes associated with that project
3. **Given** I have a project, **When** I edit the project name, **Then** the project name is updated throughout the application
4. **Given** I have a project, **When** I change the project color, **Then** the project icon color updates in the sidebar and note cards
5. **Given** I have a project, **When** I mark it as favorite, **Then** the project appears at the top of the project list with a star indicator
6. **Given** I have a project, **When** I archive the project, **Then** the project is hidden from the main project list but notes are preserved
7. **Given** I have an empty project, **When** I delete the project, **Then** the project is permanently removed
8. **Given** I have a project with notes, **When** I attempt to delete the project, **Then** I receive a warning that notes will be moved to inbox or the action is prevented

---

### User Story 3 - Label System for Cross-Project Organization (Priority: P2)

Users need to create labels (tags) to organize notes across multiple projects, view all notes with a specific label, and manage label properties (name, color, favorites).

**Why this priority**: Labels provide flexible cross-project organization and are essential for users who want to track themes, topics, or contexts that span multiple projects. This is secondary to basic project organization but critical for power users.

**Independent Test**: Can be fully tested by creating labels, assigning them to notes across different projects, filtering notes by label, and managing label properties. Delivers value by enabling cross-project note discovery.

**Acceptance Scenarios**:

1. **Given** I am viewing the labels page, **When** I click "create label" and enter a label name, **Then** a new label is created and available for tagging notes
2. **Given** I am creating or editing a note, **When** I select one or more labels to apply, **Then** the labels are associated with the note and visible on the note card
3. **Given** I have created labels, **When** I click on a label in the sidebar navigation, **Then** I see all notes across all projects that have that label
4. **Given** I have a label, **When** I rename the label, **Then** the label name is updated on all notes that use it
5. **Given** I have a label, **When** I mark it as favorite, **Then** the label appears at the top of the label list
6. **Given** I have a label, **When** I delete the label, **Then** the label is removed from all notes and the label list
7. **Given** I am viewing a note with multiple labels, **When** I remove a label from the note, **Then** the label is disassociated from that specific note

---

### User Story 4 - Note Filtering and Search (Priority: P2)

Users need to filter notes within projects or labels by status (pinned, archived, deleted) and search across note content to quickly find specific information.

**Why this priority**: As users accumulate notes, they need efficient ways to find and filter content. This enhances productivity but is not critical for initial note-taking functionality.

**Independent Test**: Can be fully tested by pinning notes, archiving notes, moving notes to trash, and searching for specific text within notes. Delivers value by improving note discoverability.

**Acceptance Scenarios**:

1. **Given** I have a note, **When** I pin the note, **Then** the note appears at the top of the note list with a pin indicator
2. **Given** I have a note, **When** I archive the note, **Then** the note is hidden from the default view but accessible via an "archived" filter
3. **Given** I have archived notes, **When** I view archived notes and unarchive one, **Then** the note returns to the active note list
4. **Given** I have deleted notes, **When** I view the trash, **Then** I see all deleted notes with options to restore or permanently delete
5. **Given** I am viewing a project or label, **When** I use the search function with a keyword, **Then** notes containing that keyword are displayed
6. **Given** I am viewing search results, **When** I click on a result, **Then** the full note is displayed with search terms highlighted

---

### User Story 5 - Note Linking and Attachments (Priority: P3)

Users need to link notes together to create relationships between ideas and attach files or images to notes for richer content.

**Why this priority**: This is an advanced feature that enhances note richness and interconnectivity. It's valuable for knowledge management but not essential for basic note-taking.

**Independent Test**: Can be fully tested by creating note links, navigating between linked notes, uploading attachments, and viewing attachments within notes. Delivers value for users building interconnected knowledge bases.

**Acceptance Scenarios**:

1. **Given** I am editing a note, **When** I create a link to another note, **Then** the linked note is referenced as clickable text within the note content (inline link)
2. **Given** I am viewing a note with linked notes, **When** I click on an inline note link, **Then** I navigate to the linked note
3. **Given** I am editing a note, **When** I upload an image or file, **Then** the attachment is associated with the note and displayed inline or as a downloadable link
4. **Given** I am viewing a note with attachments, **When** I click on an attachment, **Then** the file is downloaded or viewed
5. **Given** I have a note with backlinks (other notes linking to it), **When** I view the note, **Then** I see a dedicated backlinks section/panel at the bottom showing all notes that reference this note
6. **Given** I am viewing the backlinks section, **When** I click on a backlink, **Then** I navigate to the note that references the current note
7. **Given** I am viewing the attachments page, **When** I browse attachments, **Then** I see all attachments across all notes with options to view source notes

---

### User Story 6 - User Authentication and Profile (Priority: P1)

Users must register with an email address, log in to access their notes, and log out when finished. Each user's data is isolated and secure.

**Why this priority**: Authentication is fundamental for data security and multi-user support. Without it, the application cannot be used safely or by multiple users.

**Independent Test**: Can be fully tested by registering a new account, logging in, verifying session persistence, and logging out. Delivers essential security and user isolation.

**Acceptance Scenarios**:

1. **Given** I am on the home page, **When** I click "sign up" and enter my email and password, **Then** a new account is created and I am logged in automatically
2. **Given** I have an account, **When** I enter my email and password on the login page, **Then** I am authenticated and redirected to the inbox
3. **Given** I am logged in, **When** I close the browser and return later, **Then** my session persists and I remain logged in (session management)
4. **Given** I am logged in, **When** I click "log out", **Then** my session is terminated and I am redirected to the login page
5. **Given** I enter incorrect credentials, **When** I attempt to log in, **Then** I see an error message indicating invalid credentials
6. **Given** I am not logged in, **When** I attempt to access the application directly, **Then** I am redirected to the login page

---

### Edge Cases

- What happens when a user tries to create a note with no content? System should prevent empty note creation or auto-delete empty notes.
- What happens when a user tries to move a note to a deleted or archived project? System should prevent the action or automatically unarchive the project.
- What happens when a user deletes a project with notes inside? System should move notes to inbox or prevent deletion with a warning.
- What happens when the user loses internet connection while editing? System should queue changes and sync when connection is restored, or warn the user of unsaved changes.
- What happens when the user's session expires while using the app? System should gracefully prompt re-authentication without losing unsaved work.
- What happens when a user applies the same label multiple times to a note? System should prevent duplicate labels on a single note.
- What happens when a user searches with special characters or very long queries? System should handle edge cases gracefully without errors.
- What happens when infinite scroll reaches the last note? System should indicate "no more notes" and stop attempting to load.
- What happens when a user uploads a very large file as an attachment? System should enforce file size limits and provide clear error messages.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to create new notes with rich text content (bold, italic, underline, lists, links, headings)
- **FR-002**: System MUST allow users to edit existing note content with real-time updates
- **FR-003**: System MUST allow users to delete notes (move to trash)
- **FR-004**: System MUST allow users to permanently delete notes from trash
- **FR-005**: System MUST allow users to restore notes from trash
- **FR-006**: System MUST assign each note to exactly one project (one-to-one relationship)
- **FR-007**: System MUST allow users to move notes between projects
- **FR-008**: System MUST allow users to assign multiple labels to a single note (one-to-many relationship)
- **FR-009**: System MUST display all notes for a selected project in chronological order (newest first)
- **FR-010**: System MUST implement infinite scroll for note lists to load notes progressively
- **FR-011**: System MUST allow users to create new projects with a name and color
- **FR-012**: System MUST allow users to edit project properties (name, color)
- **FR-013**: System MUST allow users to mark projects as favorites
- **FR-014**: System MUST display favorite projects at the top of the project list
- **FR-015**: System MUST allow users to archive projects to hide them from the main view
- **FR-016**: System MUST allow users to unarchive projects to restore them to the main view
- **FR-017**: System MUST allow users to delete empty projects
- **FR-018**: System MUST prevent deletion of projects with notes or prompt to move notes to inbox
- **FR-019**: System MUST provide a default "inbox" project for quick note capture
- **FR-020**: System MUST allow users to create new labels with a name
- **FR-021**: System MUST allow users to edit label names
- **FR-022**: System MUST allow users to delete labels (removes label from all notes)
- **FR-023**: System MUST allow users to mark labels as favorites
- **FR-024**: System MUST display favorite labels at the top of the label list
- **FR-025**: System MUST display all notes with a selected label across all projects
- **FR-026**: System MUST allow users to pin notes to keep them at the top of lists
- **FR-027**: System MUST allow users to unpin notes
- **FR-028**: System MUST allow users to archive notes to hide them from default views
- **FR-029**: System MUST allow users to unarchive notes to restore them to active views
- **FR-030**: System MUST provide a trash view showing all deleted notes
- **FR-031**: System MUST allow users to filter notes by status (all, pinned, archived, deleted)
- **FR-032**: System MUST allow users to search notes by text content using client-side filtering (searches within currently loaded notes in the active view)
- **FR-033**: System MUST highlight search terms in search results
- **FR-034**: System MUST allow users to create links between notes
- **FR-035**: System MUST display clickable note links within note content
- **FR-036**: System MUST show backlinks (notes that reference the current note)
- **FR-037**: System MUST allow users to attach files to notes
- **FR-038**: System MUST allow users to attach images to notes with inline display
- **FR-039**: System MUST allow users to view and download attachments
- **FR-040**: System MUST provide an attachments page showing all attachments across notes
- **FR-041**: System MUST require email-based authentication for all users
- **FR-042**: System MUST allow users to register new accounts with email and password
- **FR-043**: System MUST allow users to log in with email and password credentials
- **FR-044**: System MUST allow users to log out and terminate their session
- **FR-045**: System MUST persist user sessions across browser restarts
- **FR-046**: System MUST redirect unauthenticated users to the login page
- **FR-047**: System MUST isolate each user's data (notes, projects, labels) from other users
- **FR-048**: System MUST display validation errors for invalid form inputs
- **FR-049**: System MUST display loading states while fetching data from the backend
- **FR-050**: System MUST display error messages when backend operations fail
- **FR-051**: System MUST provide optimistic UI updates for mutations (create, update, delete, pin, archive, label changes) to improve perceived performance
- **FR-052**: System MUST rollback optimistic updates with toast notification if the backend operation fails
- **FR-053**: System MUST display project colors consistently throughout the interface (sidebar, note cards, dropdowns)
- **FR-054**: System MUST show note counts on project list items
- **FR-055**: System MUST show the current project name as a page title when viewing project notes
- **FR-056**: System MUST provide a responsive layout that works on desktop screens
- **FR-057**: System MUST use a sidebar navigation for projects and labels
- **FR-058**: System MUST provide keyboard shortcuts for essential actions: Save note (Ctrl+S/Cmd+S), New note (Ctrl+N/Cmd+N), Search (Ctrl+K/Cmd+K), Quick project switch (Ctrl+P/Cmd+P), Close/Escape dialogs (Esc)
- **FR-059**: System MUST preserve note formatting when saving and retrieving notes
- **FR-060**: System MUST display note metadata (creation date, update date, label count, attachment count)

### Key Entities

- **Note**: Represents a single note with rich text content, belongs to one project, can have multiple labels, can have attachments and note links. Key attributes: noteId, content, projectId, labelIds, addedAt, updatedAt, isPinned, isArchived, isDeleted, attachmentCount, referencingCount, referencedCount, creator.

- **Project**: Represents an organizational container for notes. Key attributes: projectId, name, color, isFavorite, isArchived, noteCount, isInboxProject. Each note belongs to exactly one project (one-to-one relationship).

- **Label**: Represents a tag that can be applied to multiple notes for cross-project organization. Key attributes: labelId, name, isFavorite. Multiple labels can be assigned to a note (one-to-many relationship from note to labels).

- **User**: Represents an authenticated user with isolated data. Key attributes: userId, email, password (hashed). All notes, projects, and labels belong to a specific user.

- **Attachment**: Represents a file or image attached to a note. Key attributes: attachmentId, filename, fileType, fileSize, noteId. Multiple attachments can belong to a note.

- **NoteLink**: Represents a bidirectional relationship between two notes. Key attributes: sourceNoteId, targetNoteId. Used to track which notes reference other notes (backlinks).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can create a new note and save it in under 5 seconds from opening the application
- **SC-002**: Users can navigate between projects and view project-specific notes with page load under 2 seconds
- **SC-003**: Note lists load additional notes (infinite scroll) within 1 second of reaching the bottom
- **SC-004**: 90% of users successfully complete their first note creation on the first attempt without assistance
- **SC-005**: Users can organize notes into at least 5 custom projects with distinct colors and favorites
- **SC-006**: Users can apply up to 10 labels to a single note for flexible organization
- **SC-007**: Search functionality returns relevant results within 2 seconds for queries across 1000+ notes
- **SC-008**: Users can successfully authenticate (login) within 10 seconds from landing on the page
- **SC-009**: 95% of note creation and update operations complete successfully without backend errors
- **SC-010**: Users can access their notes from any device with session persistence across browser restarts
- **SC-011**: Zero data leakage between users (all notes, projects, and labels are properly isolated)
- **SC-012**: Users can view and interact with rich text formatting (bold, italic, lists, headings) in all saved notes
- **SC-013**: Application handles concurrent users with no performance degradation up to 100 simultaneous sessions
- **SC-014**: Users report high satisfaction (4+ out of 5) with the note organization and retrieval experience
- **SC-015**: 80% of users utilize both projects and labels for note organization within the first week of use

## Assumptions

- Users have modern web browsers with JavaScript enabled (Chrome, Firefox, Safari, Edge - latest 2 versions)
- Backend APIs are fully functional and documented with proper error handling
- User authentication uses session-based cookies or JWT tokens (implementation determined by existing backend)
- File upload size limits are enforced by the backend (assuming 10MB per file as industry standard)
- Note content is stored as block-based JSON structure (Notion/Editor.js style) in PostgreSQL JSONB column, NOT as HTML strings or monolithic Markdown
- Database supports pagination for efficient note loading (page size: 5 notes mobile, 7 tablet, 10 desktop)
- Users understand basic note-taking concepts (projects, tags/labels, folders)
- The application uses mobile-first responsive design (320px-2560px viewports) with touch interactions and progressive enhancement for desktop
- Mobile browsers supported: iOS Safari 14+, Chrome Mobile 90+; touch-optimized UI with 44px+ tap targets
- Real-time collaboration is not required; single-user editing is sufficient
- Offline support is not required; application requires internet connection
- Export/import functionality is not included in this phase
- User password reset and email verification are handled by existing backend or will be added later
- The text editor component already provides rich text formatting capabilities (based on existing code review)
- Internationalization (multiple languages) is not required in this phase; English UI is sufficient
