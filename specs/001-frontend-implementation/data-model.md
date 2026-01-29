# Data Model: Frontend State Management

**Feature**: Complete Frontend Implementation  
**Date**: 2026-01-27  
**Phase**: 1 - Design and data modeling

## Overview

This document defines the frontend data models, TypeScript interfaces, state management patterns, and data flow for the note-taking application. All models are derived from the backend API contracts and specification requirements.

## Core Entities

### 1. Note

**Purpose**: Represents a single note with **block-based JSON content** (Notion/Editor.js style), project assignment, labels, and status flags.

**Content Architecture**: Notes use structured block-based JSON stored in PostgreSQL JSONB, NOT HTML strings or monolithic Markdown.

**TypeScript Interface**:
```typescript
// types/note.ts

// Block-based content structure
export interface ContentBlock {
  id: string  // Unique block ID
  type: 'paragraph' | 'heading' | 'bulletList' | 'orderedList' | 'codeBlock' | 'blockquote' | 'image' | 'divider'
  attrs?: Record<string, any>  // Block-specific attributes (e.g., level for headings, language for code)
  content?: ContentNode[]  // Inline content (text, marks)
}

export interface ContentNode {
  type: 'text' | 'hardBreak'
  text?: string
  marks?: Array<{
    type: 'bold' | 'italic' | 'code' | 'strike' | 'link'
    attrs?: Record<string, any>  // Mark-specific attributes (e.g., href for links)
  }>
}

export interface NoteContent {
  type: 'doc'
  content: ContentBlock[]
  version?: string  // Content schema version (default: '1.0')
}

// Base note with block-based content
export interface BaseNote {
  content: NoteContent  // Block-based JSON structure, NOT string
  labels: { labelId: string; name: string }[]
  project: { projectId: string; name: string }
}

export interface FullNote extends BaseNote {
  noteId: string
  addedAt: string  // ISO 8601 datetime
  updatedAt: string  // ISO 8601 datetime
  isArchived: 0 | 1  // Boolean flag (backend uses integers)
  isDeleted: 0 | 1
  isPinned: 0 | 1
  attachmentCount: number | null
  referencedCount: number | null  // Notes referencing this note
  referencingCount: number | null  // Notes this note references
  creator: string  // User ID
}

export interface NewNote {
  content: NoteContent  // Block-based JSON
  projectId: string
  labels?: string[]  // Array of label IDs
  files?: string[]  // Array of attachment IDs
  linkedNotes?: string[]  // Array of note IDs
}

export interface UpdateNote {
  content?: NoteContent  // Block-based JSON
  projectId?: string
  labels?: string[]
  files?: string[]
  linkedNotes?: string[]
}

export interface NotePageRequestParams {
  objectId?: string  // Project ID or Label ID
  pageSize: number
  pageIndex: number
}

export interface NotePageLoaderData {
  pageIndex: number
  pageSize: number
  total: number
  records: FullNote[]
}

// Computed properties
export interface NoteWithStatus extends FullNote {
  isPinnedBool: boolean  // Convert 0/1 to boolean for easier conditionals
  isArchivedBool: boolean
  isDeletedBool: boolean
  labelCount: number  // Derived from labels.length
}

// Helper: Extract plain text from block-based content
export function extractPlainText(content: NoteContent, maxLength?: number): string {
  let text = ''
  
  for (const block of content.content) {
    if (block.content) {
      for (const node of block.content) {
        if (node.type === 'text' && node.text) {
          text += node.text + ' '
        }
      }
    }
  }
  
  const trimmed = text.trim()
  return maxLength ? trimmed.slice(0, maxLength) + (trimmed.length > maxLength ? '...' : '') : trimmed
}

// Example note content structure:
const exampleContent: NoteContent = {
  type: 'doc',
  content: [
    {
      id: 'block-1',
      type: 'heading',
      attrs: { level: 1 },
      content: [{ type: 'text', text: 'Meeting Notes' }]
    },
    {
      id: 'block-2',
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Discussed ', marks: [] },
        { type: 'text', text: 'important', marks: [{ type: 'bold' }] },
        { type: 'text', text: ' topics' }
      ]
    },
    {
      id: 'block-3',
      type: 'bulletList',
      content: [
        { type: 'text', text: 'Task 1' },
        { type: 'text', text: 'Task 2' }
      ]
    },
    {
      id: 'block-4',
      type: 'codeBlock',
      attrs: { language: 'javascript' },
      content: [{ type: 'text', text: 'console.log("hello")' }]
    }
  ],
  version: '1.0'
}
```

**Relationships**:
- Belongs to one **Project** (one-to-one)
- Has many **Labels** (one-to-many)
- Has many **Attachments** (one-to-many)
- References many **Notes** (many-to-many, bidirectional)

**State flags** (0 = false, 1 = true):
- `isPinned`: Note appears at top of lists
- `isArchived`: Note hidden from default views
- `isDeleted`: Note in trash (soft delete)

**Status hierarchy**:
```
deleted > archived > pinned > normal
```
If deleted, ignore archived/pinned status.

### 2. Project

**Purpose**: Organizational container for notes. Each user has one inbox project (default) and can create custom projects.

**TypeScript Interface**:
```typescript
// types/project.ts

export interface BaseProject {
  name: string
  noteCount: number
  isFavorite: 0 | 1
  color: string  // Color key (e.g., 'blue', 'red', 'green')
}

export interface FullProject extends BaseProject {
  projectId: string
  inboxProject: boolean  // Special flag for inbox
  isArchived: 0 | 1  // Added based on backend API
}

export interface NewProject {
  name: string
  color: string
  isFavorite?: 0 | 1
}

export interface UpdateProject {
  name?: string
  color?: string
}

export interface ProjectOutletContext {
  projects: FullProject[]
  inboxProject: FullProject
}

// Project colors (predefined palette)
export const PROJECT_COLORS = [
  'blue', 'red', 'green', 'yellow', 'purple', 
  'pink', 'orange', 'teal', 'indigo', 'gray'
] as const

export type ProjectColor = typeof PROJECT_COLORS[number]
```

**Relationships**:
- Has many **Notes** (one-to-many)
- Belongs to one **User** (one-to-many)

**Special project**:
- Inbox: `inboxProject: true`, one per user, cannot be deleted

**Display rules**:
- Favorites appear first
- Sorted alphabetically within favorite/non-favorite groups
- Archived projects hidden by default

### 3. Label

**Purpose**: Tags for cross-project note organization. Users can apply multiple labels to any note.

**TypeScript Interface**:
```typescript
// types/label.ts

export interface BaseLabel {
  name: string
  isFavorite: 0 | 1
}

export interface FullLabel extends BaseLabel {
  labelId: string
  noteCount?: number  // Optional, included in list responses
  color?: string  // Optional, for future color coding
}

export interface NewLabel {
  name: string
}

export interface UpdateLabel {
  name: string
}

// Label for display on note cards (minimal)
export interface LabelBadge {
  labelId: string
  name: string
}
```

**Relationships**:
- Has many **Notes** (many-to-many through join table)
- Belongs to one **User** (one-to-many)

**Display rules**:
- Favorites appear first
- Sorted alphabetically within favorite/non-favorite groups
- Show note count next to label name in sidebar

### 4. User

**Purpose**: Authenticated user with isolated data.

**TypeScript Interface**:
```typescript
// types/user.ts

export interface User {
  userId: string
  email: string
  displayName?: string  // Optional display name
}

export interface UserResponse {
  user: User | null
  isAuthenticated: boolean
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterCredentials {
  email: string
  password: string
  confirmPassword: string
}

export interface AuthToken {
  token: string
  expiresAt: string  // ISO 8601 datetime
}
```

**Session management**:
- Token stored in localStorage/sessionStorage
- Axios interceptor adds to requests
- Refresh on expiry or redirect to login

### 5. Attachment

**Purpose**: Files or images attached to notes.

**TypeScript Interface**:
```typescript
// types/attachment.ts

export interface Attachment {
  attachmentId: string
  filename: string
  fileType: string  // MIME type
  fileSize: number  // Bytes
  fileUrl: string  // Download URL
  thumbnailUrl?: string  // For images
  noteId: string
  uploadedAt: string  // ISO 8601 datetime
}

export interface NewAttachment {
  file: File  // Browser File object
  noteId: string
}
```

**File types**:
- Images: Display inline with thumbnails
- Documents: Show download link with icon
- Max size: 10MB (enforced by backend)

### 6. NoteLink

**Purpose**: Bidirectional references between notes.

**TypeScript Interface**:
```typescript
// types/note-link.ts

export interface NoteLink {
  sourceNoteId: string
  targetNoteId: string
  createdAt: string
}

// For displaying linked notes
export interface LinkedNote {
  noteId: string
  content: string  // First 100 characters
  projectName: string
  addedAt: string
}

// Backlinks (notes that reference current note)
export interface Backlink extends LinkedNote {
  linkType: 'incoming'  // Future: 'outgoing', 'bidirectional'
}
```

**Display**:
- Show linked notes in note detail view
- Show backlinks section (notes referencing this note)
- Clickable to navigate to linked note

## State Management Architecture

### Server State (TanStack Query)

**Notes**:
```typescript
// hooks/use-notes.ts

export function useNotes(
  filter: { projectId?: string; labelId?: string; status?: NoteStatus }
) {
  return useQuery({
    queryKey: ['notes', filter],
    queryFn: () => fetchNotes(filter),
    staleTime: 30000,  // 30 seconds
    cacheTime: 300000,  // 5 minutes
  })
}

export function useInfiniteNotes(
  filter: { projectId?: string; labelId?: string }
) {
  return useInfiniteQuery({
    queryKey: ['notes', 'infinite', filter],
    queryFn: ({ pageParam = 1 }) => 
      fetchNotes({ ...filter, pageIndex: pageParam, pageSize: 10 }),
    getNextPageParam: (lastPage, pages) => 
      lastPage.total > pages.length * 10 ? pages.length + 1 : undefined,
  })
}

export function useNoteCreate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (newNote: NewNote) => createNote(newNote),
    onMutate: async (newNote) => {
      // Optimistic update logic
      await queryClient.cancelQueries(['notes'])
      const previousNotes = queryClient.getQueryData(['notes'])
      
      queryClient.setQueryData(['notes'], (old: any) => ({
        ...old,
        records: [
          { ...newNote, noteId: `temp-${Date.now()}`, ...defaultNoteFields },
          ...old.records
        ]
      }))
      
      return { previousNotes }
    },
    onError: (err, newNote, context) => {
      // Rollback on error
      queryClient.setQueryData(['notes'], context.previousNotes)
    },
    onSettled: () => {
      queryClient.invalidateQueries(['notes'])
    },
  })
}

export function useNoteUpdate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ noteId, updates }: { noteId: string; updates: UpdateNote }) => 
      updateNote(noteId, updates),
    onSuccess: () => {
      queryClient.invalidateQueries(['notes'])
    },
  })
}

export function useNoteActions() {
  const queryClient = useQueryClient()
  
  const pin = useMutation({
    mutationFn: (noteId: string) => pinNote(noteId),
    onSuccess: () => queryClient.invalidateQueries(['notes']),
  })
  
  const archive = useMutation({
    mutationFn: (noteId: string) => archiveNote(noteId),
    onSuccess: () => queryClient.invalidateQueries(['notes']),
  })
  
  const deleteNote = useMutation({
    mutationFn: (noteId: string) => softDeleteNote(noteId),
    onSuccess: () => queryClient.invalidateQueries(['notes']),
  })
  
  return { pin, archive, deleteNote }
}
```

**Projects**:
```typescript
// hooks/use-projects.ts

export function useProjects() {
  return useQuery({
    queryKey: ['projects'],
    queryFn: fetchProjects,
    staleTime: 60000,  // 1 minute
  })
}

export function useProjectCreate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (newProject: NewProject) => createProject(newProject),
    onSuccess: () => {
      queryClient.invalidateQueries(['projects'])
    },
  })
}

export function useProjectUpdate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, updates }: { id: string; updates: UpdateProject }) => 
      updateProject(id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries(['projects'])
    },
  })
}

export function useProjectActions() {
  const queryClient = useQueryClient()
  
  const favorite = useMutation({
    mutationFn: (projectId: string) => toggleProjectFavorite(projectId),
    onSuccess: () => queryClient.invalidateQueries(['projects']),
  })
  
  const archive = useMutation({
    mutationFn: (projectId: string) => archiveProject(projectId),
    onSuccess: () => queryClient.invalidateQueries(['projects']),
  })
  
  const deleteProject = useMutation({
    mutationFn: (projectId: string) => deleteProject(projectId),
    onSuccess: () => queryClient.invalidateQueries(['projects']),
  })
  
  return { favorite, archive, deleteProject }
}
```

**Labels**:
```typescript
// hooks/use-labels.ts

export function useLabels() {
  return useQuery({
    queryKey: ['labels'],
    queryFn: fetchLabels,
    staleTime: 60000,  // 1 minute
  })
}

export function useLabelCreate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (newLabel: NewLabel) => createLabel(newLabel),
    onSuccess: () => {
      queryClient.invalidateQueries(['labels'])
    },
  })
}

export function useLabelUpdate() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, updates }: { id: string; updates: UpdateLabel }) => 
      updateLabel(id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries(['labels'])
    },
  })
}

export function useLabelActions() {
  const queryClient = useQueryClient()
  
  const favorite = useMutation({
    mutationFn: (labelId: string) => toggleLabelFavorite(labelId),
    onSuccess: () => queryClient.invalidateQueries(['labels']),
  })
  
  const deleteLabel = useMutation({
    mutationFn: (labelId: string) => deleteLabel(labelId),
    onSuccess: () => {
      queryClient.invalidateQueries(['labels'])
      queryClient.invalidateQueries(['notes'])  // Notes may have this label
    },
  })
  
  return { favorite, deleteLabel }
}
```

### UI State (React Context)

**Filter State**:
```typescript
// store/use-filter-store.ts

export type NoteStatus = 'all' | 'pinned' | 'archived' | 'deleted'

interface FilterState {
  status: NoteStatus
  sortBy: 'addedAt' | 'updatedAt' | 'title'
  sortOrder: 'asc' | 'desc'
}

interface FilterContextType {
  filters: FilterState
  setStatus: (status: NoteStatus) => void
  setSortBy: (sortBy: FilterState['sortBy']) => void
  toggleSortOrder: () => void
  resetFilters: () => void
}

export const FilterContext = createContext<FilterContextType | undefined>(undefined)

export function useFilterStore() {
  const context = useContext(FilterContext)
  if (!context) {
    throw new Error('useFilterStore must be used within FilterProvider')
  }
  return context
}
```

**Search State**:
```typescript
// store/use-search-store.ts

interface SearchState {
  query: string
  results: FullNote[]
  isSearching: boolean
}

interface SearchContextType {
  search: SearchState
  setQuery: (query: string) => void
  clearSearch: () => void
}

export const SearchContext = createContext<SearchContextType | undefined>(undefined)

export function useSearchStore() {
  const context = useContext(SearchContext)
  if (!context) {
    throw new Error('useSearchStore must be used within SearchProvider')
  }
  return context
}
```

### Form State (React Hook Form)

**Project Form**:
```typescript
// features/project/project-dialog.tsx

interface ProjectFormData {
  name: string
  color: ProjectColor
  isFavorite: boolean
}

const schema = z.object({
  name: z.string().min(1, 'Project name is required').max(100),
  color: z.enum(PROJECT_COLORS),
  isFavorite: z.boolean().default(false),
})

const form = useForm<ProjectFormData>({
  resolver: zodResolver(schema),
  defaultValues: {
    name: '',
    color: 'blue',
    isFavorite: false,
  },
})
```

**Label Form**:
```typescript
// features/label/label-dialog.tsx

interface LabelFormData {
  name: string
}

const schema = z.object({
  name: z.string().min(1, 'Label name is required').max(50),
})

const form = useForm<LabelFormData>({
  resolver: zodResolver(schema),
  defaultValues: { name: '' },
})
```

## Data Flow Diagrams

### Note Creation Flow
```
User types in TipTap editor
         ↓
Clicks "Save" button
         ↓
useNoteCreate mutation fires
         ↓
onMutate: Optimistic update (add temp note to UI)
         ↓
API call: POST /api/v1/notes
         ↓
onSuccess: Replace temp ID with real ID
         ↓
queryClient.invalidateQueries(['notes'])
         ↓
UI updates with final note data
```

### Note Status Change Flow
```
User clicks "Archive" button
         ↓
useNoteActions().archive.mutate(noteId)
         ↓
API call: PATCH /api/v1/notes/:id/archive
         ↓
onSuccess: Invalidate notes query
         ↓
TanStack Query refetches notes
         ↓
Note disappears from list (filtered out)
```

### Infinite Scroll Flow
```
User scrolls to bottom of list
         ↓
IntersectionObserver triggers
         ↓
useInfiniteNotes().fetchNextPage()
         ↓
API call: GET /api/v1/projects/:id/notes?pageIndex=2
         ↓
TanStack Query appends new page to cache
         ↓
Component renders additional notes
```

## Query Key Conventions

Consistent query key structure for caching and invalidation:

```typescript
['notes']                              // All notes (rarely used)
['notes', { projectId: '123' }]        // Notes for project
['notes', { labelId: '456' }]          // Notes for label
['notes', { status: 'archived' }]      // Filtered notes
['notes', 'infinite', { projectId }]   // Infinite query

['projects']                           // All projects
['projects', projectId]                // Single project

['labels']                             // All labels
['labels', labelId]                    // Single label

['user']                               // Current user
```

Invalidation strategy:
- Creating/updating/deleting a note: Invalidate `['notes']` (all queries with 'notes' prefix)
- Creating/updating a project: Invalidate `['projects']`
- Deleting a project: Invalidate both `['projects']` and `['notes']`
- Deleting a label: Invalidate both `['labels']` and `['notes']`

## Validation Rules

### Note
- Content: 1-100,000 characters (enforced by backend)
- Project: Must be valid project ID (required)
- Labels: Max 10 labels per note (optional)
- Attachments: Max 10 files, 10MB each (optional)

### Project
- Name: 1-100 characters, unique per user (required)
- Color: One of predefined colors (required)
- Cannot delete project with notes (enforced by backend)

### Label
- Name: 1-50 characters, unique per user (required)
- Deleting label removes it from all notes

### User
- Email: Valid email format (required)
- Password: Min 8 characters, 1 uppercase, 1 number (required)

## Error Handling

### API Error Structure
```typescript
interface ApiError {
  status: number  // HTTP status code
  message: string  // User-friendly message
  details?: Record<string, string[]>  // Field-level validation errors
}
```

### Error Mapping
```typescript
// lib/error-mapper.ts

export function mapApiError(error: AxiosError): string {
  if (error.response) {
    switch (error.response.status) {
      case 400: return 'Invalid request. Please check your input.'
      case 401: return 'Session expired. Please log in again.'
      case 403: return 'You do not have permission to perform this action.'
      case 404: return 'The requested resource was not found.'
      case 409: return 'This name is already in use.'
      case 500: return 'Server error. Please try again later.'
      default: return 'An unexpected error occurred.'
    }
  }
  if (error.request) {
    return 'Network error. Please check your connection.'
  }
  return error.message || 'An unexpected error occurred.'
}
```

## Performance Considerations

### Cache Configuration
```typescript
// lib/query-client.ts

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30000,  // 30 seconds
      cacheTime: 300000,  // 5 minutes
      refetchOnWindowFocus: false,
      retry: 1,
    },
    mutations: {
      retry: 0,  // Don't retry mutations
    },
  },
})
```

### Pagination Strategy
- **Page size**: 10 notes (fast initial load)
- **Prefetch**: Load next page when user reaches 80% of current list
- **Cache**: Keep 3 pages in memory, discard older pages

### Optimistic Updates
- **Create**: Add to top of list immediately
- **Update**: Modify in place
- **Delete**: Remove from list, show undo toast for 5 seconds
- **Rollback**: Automatic on API error

## Mobile-Specific Patterns

### Touch Gesture Handling

**Swipe Actions**:
```typescript
// hooks/use-swipe-actions.ts

interface SwipeHandlers {
  onSwipeLeft?: () => void
  onSwipeRight?: () => void
  threshold?: number  // Minimum distance for swipe (default: 50px)
}

export function useSwipeActions(handlers: SwipeHandlers) {
  const [touchStart, setTouchStart] = useState<number | null>(null)
  const [touchEnd, setTouchEnd] = useState<number | null>(null)
  
  const minSwipeDistance = handlers.threshold || 50
  
  const onTouchStart = (e: React.TouchEvent) => {
    setTouchEnd(null)
    setTouchStart(e.targetTouches[0].clientX)
  }
  
  const onTouchMove = (e: React.TouchEvent) => {
    setTouchEnd(e.targetTouches[0].clientX)
  }
  
  const onTouchEnd = () => {
    if (!touchStart || !touchEnd) return
    
    const distance = touchStart - touchEnd
    const isLeftSwipe = distance > minSwipeDistance
    const isRightSwipe = distance < -minSwipeDistance
    
    if (isLeftSwipe && handlers.onSwipeLeft) {
      handlers.onSwipeLeft()
    }
    if (isRightSwipe && handlers.onSwipeRight) {
      handlers.onSwipeRight()
    }
  }
  
  return { onTouchStart, onTouchMove, onTouchEnd }
}

// Usage in NoteCard
function NoteCard({ note }: { note: FullNote }) {
  const { archive } = useNoteActions()
  const { remove } = useNoteActions()
  
  const swipeHandlers = useSwipeActions({
    onSwipeLeft: () => archive.mutate(note.noteId),
    onSwipeRight: () => remove.mutate(note.noteId),
  })
  
  return (
    <div {...swipeHandlers}>
      {/* Note content */}
    </div>
  )
}
```

**Long Press Context Menu**:
```typescript
// hooks/use-long-press.ts

interface LongPressOptions {
  onLongPress: () => void
  delay?: number  // Default: 500ms
}

export function useLongPress(options: LongPressOptions) {
  const [longPressTriggered, setLongPressTriggered] = useState(false)
  const timeout = useRef<NodeJS.Timeout>()
  const target = useRef<EventTarget>()
  
  const start = useCallback((event: React.TouchEvent | React.MouseEvent) => {
    event.preventDefault()
    target.current = event.target
    timeout.current = setTimeout(() => {
      options.onLongPress()
      setLongPressTriggered(true)
    }, options.delay || 500)
  }, [options])
  
  const clear = useCallback(() => {
    timeout.current && clearTimeout(timeout.current)
    setLongPressTriggered(false)
  }, [])
  
  return {
    onMouseDown: start,
    onTouchStart: start,
    onMouseUp: clear,
    onMouseLeave: clear,
    onTouchEnd: clear,
  }
}
```

**Pull-to-Refresh**:
```typescript
// hooks/use-pull-to-refresh.ts

interface PullToRefreshOptions {
  onRefresh: () => Promise<void>
  threshold?: number  // Default: 80px
}

export function usePullToRefresh(options: PullToRefreshOptions) {
  const [isPulling, setIsPulling] = useState(false)
  const [pullDistance, setPullDistance] = useState(0)
  const touchStart = useRef<number | null>(null)
  
  const onTouchStart = (e: React.TouchEvent) => {
    const scrollTop = window.scrollY || document.documentElement.scrollTop
    if (scrollTop === 0) {
      touchStart.current = e.touches[0].clientY
    }
  }
  
  const onTouchMove = (e: React.TouchEvent) => {
    if (touchStart.current === null) return
    
    const currentY = e.touches[0].clientY
    const distance = currentY - touchStart.current
    
    if (distance > 0) {
      setIsPulling(true)
      setPullDistance(Math.min(distance, options.threshold || 80))
    }
  }
  
  const onTouchEnd = async () => {
    if (pullDistance >= (options.threshold || 80)) {
      await options.onRefresh()
    }
    setIsPulling(false)
    setPullDistance(0)
    touchStart.current = null
  }
  
  return {
    isPulling,
    pullDistance,
    onTouchStart,
    onTouchMove,
    onTouchEnd,
  }
}
```

### Responsive Layout Patterns

**Navigation State**:
```typescript
// store/use-mobile-nav-store.ts

import { create } from 'zustand'

interface MobileNavState {
  isOpen: boolean
  activeView: 'projects' | 'labels' | 'search'
  open: () => void
  close: () => void
  toggle: () => void
  setActiveView: (view: 'projects' | 'labels' | 'search') => void
}

export const useMobileNavStore = create<MobileNavState>((set) => ({
  isOpen: false,
  activeView: 'projects',
  open: () => set({ isOpen: true }),
  close: () => set({ isOpen: false }),
  toggle: () => set((state) => ({ isOpen: !state.isOpen })),
  setActiveView: (view) => set({ activeView: view }),
}))
```

**Responsive Dialog**:
```typescript
// components/ui/responsive-dialog.tsx

import * as Dialog from '@radix-ui/react-dialog'
import { useMediaQuery } from '@/hooks/use-media-query'

export function ResponsiveDialog({ children, ...props }: Dialog.DialogProps) {
  const isMobile = useMediaQuery('(max-width: 768px)')
  
  return (
    <Dialog.Root {...props}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 bg-black/50" />
        <Dialog.Content
          className={cn(
            "fixed bg-white rounded-lg",
            isMobile
              ? "inset-x-0 bottom-0 rounded-b-none" // Full-width bottom sheet on mobile
              : "top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 max-w-md" // Centered on desktop
          )}
        >
          {children}
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}
```

### Mobile Performance

**Lazy Load Heavy Components**:
```typescript
// Defer TipTap editor on mobile
const TextEditor = lazy(() => import('@/features/editor/text-editor'))

function NoteEditor() {
  const isMobile = useMediaQuery('(max-width: 768px)')
  
  return (
    <Suspense fallback={<EditorSkeleton />}>
      {isMobile ? (
        <TextEditor placeholder="Tap to start writing..." />
      ) : (
        <TextEditor />
      )}
    </Suspense>
  )
}
```

**Reduce Mobile Payload**:
```typescript
// Fetch fewer notes per page on mobile
export function useNotes(projectId: string) {
  const isMobile = useMediaQuery('(max-width: 768px)')
  const pageSize = isMobile ? 5 : 10  // Smaller pages on mobile
  
  return useInfiniteQuery({
    queryKey: ['notes', projectId, { pageSize }],
    queryFn: ({ pageParam = 1 }) => 
      getNotes({ projectId, pageIndex: pageParam, pageSize }),
    // ...
  })
}
```

**Touch-Friendly Tap Targets**:
```css
/* Ensure minimum 44px tap targets */
.mobile-tap-target {
  @apply min-h-[44px] min-w-[44px] p-3;
}

/* Increase spacing on mobile */
.note-list-mobile {
  @apply space-y-3 lg:space-y-2;
}

/* Larger interactive elements */
.button-mobile {
  @apply h-12 px-6 text-base lg:h-10 lg:px-4 lg:text-sm;
}
```

## Keyboard Shortcuts

**Essential shortcuts for desktop productivity** (from spec clarifications):

```typescript
// hooks/use-keyboard-shortcuts.ts

import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

interface KeyboardShortcuts {
  onSave?: () => void
  onNewNote?: () => void
  onSearch?: () => void
  onProjectSwitch?: () => void
}

export function useKeyboardShortcuts(handlers: KeyboardShortcuts) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const isMac = navigator.platform.toUpperCase().indexOf('MAC') >= 0
      const modifier = isMac ? e.metaKey : e.ctrlKey
      
      // Ctrl/Cmd + S: Save note
      if (modifier && e.key === 's') {
        e.preventDefault()
        handlers.onSave?.()
      }
      
      // Ctrl/Cmd + N: New note
      if (modifier && e.key === 'n') {
        e.preventDefault()
        handlers.onNewNote?.()
      }
      
      // Ctrl/Cmd + K: Search
      if (modifier && e.key === 'k') {
        e.preventDefault()
        handlers.onSearch?.()
      }
      
      // Ctrl/Cmd + P: Quick project switch
      if (modifier && e.key === 'p') {
        e.preventDefault()
        handlers.onProjectSwitch?.()
      }
      
      // Esc: Close dialogs
      if (e.key === 'Escape') {
        // Handled by dialog components
      }
    }
    
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [handlers])
}

// Usage example
function NotePage() {
  const [isSearchOpen, setIsSearchOpen] = useState(false)
  const [isProjectSwitchOpen, setIsProjectSwitchOpen] = useState(false)
  const { mutate: saveNote } = useNoteSave()
  
  useKeyboardShortcuts({
    onSave: () => saveNote(currentNote),
    onNewNote: () => navigate('/notes/new'),
    onSearch: () => setIsSearchOpen(true),
    onProjectSwitch: () => setIsProjectSwitchOpen(true),
  })
  
  return (
    <>
      {/* Page content */}
      <SearchDialog open={isSearchOpen} onOpenChange={setIsSearchOpen} />
      <ProjectSwitchDialog open={isProjectSwitchOpen} onOpenChange={setIsProjectSwitchOpen} />
    </>
  )
}
```

**Keyboard shortcuts summary**:
- `Ctrl+S` / `Cmd+S`: Save note
- `Ctrl+N` / `Cmd+N`: New note
- `Ctrl+K` / `Cmd+K`: Search
- `Ctrl+P` / `Cmd+P`: Quick project switch
- `Esc`: Close/escape dialogs

## Next Steps

1. Create API contracts in `contracts/` directory
2. Generate `quickstart.md` for development workflow
3. Proceed to task breakdown with `/speckit.tasks`
