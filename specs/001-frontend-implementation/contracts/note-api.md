# Note API Contract

**Base URL**: `/api/v1/notes`  
**Authentication**: Required (JWT token in header)  
**Content Format**: Block-based JSON structure (Notion/Editor.js style), stored in PostgreSQL JSONB

## Content Structure

Notes use **block-based JSON** (NOT HTML strings or Markdown):

```typescript
{
  "content": {
    "type": "doc",
    "content": [
      {
        "id": "block-1",
        "type": "heading",
        "attrs": { "level": 1 },
        "content": [
          { "type": "text", "text": "Title" }
        ]
      },
      {
        "id": "block-2",
        "type": "paragraph",
        "content": [
          { "type": "text", "text": "Bold ", "marks": [{ "type": "bold" }] },
          { "type": "text", "text": "text" }
        ]
      },
      {
        "id": "block-3",
        "type": "codeBlock",
        "attrs": { "language": "javascript" },
        "content": [
          { "type": "text", "text": "console.log('hello')" }
        ]
      }
    ],
    "version": "1.0"
  }
}
```

**Supported block types**: `paragraph`, `heading`, `bulletList`, `orderedList`, `codeBlock`, `blockquote`, `image`, `divider`

**Supported marks**: `bold`, `italic`, `code`, `strike`, `link`

## Endpoints

### Create Note
```http
POST /api/v1/notes
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "content": {
    "type": "doc",
    "content": [
      {
        "id": "block-1",
        "type": "paragraph",
        "content": [{ "type": "text", "text": "Note content" }]
      }
    ],
    "version": "1.0"
  },
  "projectId": "string (required)",
  "labels": ["string"] (optional, max 10),
  "files": ["string"] (optional, attachment IDs),
  "linkedNotes": ["string"] (optional, note IDs)
}

Response: 201 Created
{
  "noteId": "string"
}

Errors:
- 400: Invalid request body or malformed content structure
- 401: Unauthorized
- 404: Project not found
```

### Get Note
```http
GET /api/v1/notes/:noteId
Authorization: Bearer {token}

Response: 200 OK
{
  "noteId": "string",
  "content": {
    "type": "doc",
    "content": [...],  // Block-based JSON array
    "version": "1.0"
  },
  "projectId": "string",
  "projectName": "string",
  "labels": [
    { "labelId": "string", "name": "string" }
  ],
  "isPinned": 0 | 1,
  "isArchived": 0 | 1,
  "isDeleted": 0 | 1,
  "attachmentCount": number | null,
  "referencingCount": number | null,
  "referencedCount": number | null,
  "addedAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime",
  "creator": "string"
}

Errors:
- 401: Unauthorized
- 404: Note not found
```

### Update Note
```http
PATCH /api/v1/notes/:noteId
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "content": {
    "type": "doc",
    "content": [...],  // Block-based JSON (optional)
    "version": "1.0"
  },
  "projectId": "string" (optional),
  "labels": ["string"] (optional),
  "files": ["string"] (optional),
  "linkedNotes": ["string"] (optional)
}

Response: 200 OK
(no body)

Errors:
- 400: Invalid request body
- 401: Unauthorized
- 404: Note not found
```

### Delete Note (Soft Delete)
```http
DELETE /api/v1/notes/:noteId
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Note not found
```

### Permanent Delete
```http
DELETE /api/v1/notes/:noteId/permanent
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Note not found
```

### Pin/Unpin Note
```http
PATCH /api/v1/notes/:noteId/pin
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Note not found
```

### Archive/Unarchive Note
```http
PATCH /api/v1/notes/:noteId/archive
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Note not found
```

### Restore Note from Trash
```http
PATCH /api/v1/notes/:noteId/restore
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Note not found
```

## TypeScript Integration

```typescript
// api/note/note.ts

import { request } from '@/lib/http'
import type { NewNote, UpdateNote, FullNote } from '@/types/note'

export function createNote(newNote: NewNote) {
  return request<string>({
    url: '/api/v1/notes',
    method: 'post',
    data: newNote,
  })
}

export function getNote(noteId: string) {
  return request<FullNote>({
    url: `/api/v1/notes/${noteId}`,
    method: 'get',
  })
}

export function updateNote(noteId: string, updates: UpdateNote) {
  return request<void>({
    url: `/api/v1/notes/${noteId}`,
    method: 'patch',
    data: updates,
  })
}

export function deleteNote(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}`,
    method: 'delete',
  })
}

export function permanentDeleteNote(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}/permanent`,
    method: 'delete',
  })
}

export function pinNote(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}/pin`,
    method: 'patch',
  })
}

export function archiveNote(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}/archive`,
    method: 'patch',
  })
}

export function restoreNote(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}/restore`,
    method: 'patch',
  })
}
```

## Notes
- All endpoints require authentication
- Note IDs are UUIDs
- Status flags use integers (0/1) instead of booleans
- Soft delete moves note to trash (isDeleted = 1)
- Permanent delete removes from database
- Label and attachment operations modify the note entity
- **Content is stored as block-based JSON in PostgreSQL JSONB column**
- **Frontend uses TipTap which natively outputs/consumes this JSON format**

## TipTap Integration

```typescript
// features/editor/text-editor.tsx

import { useEditor, EditorContent } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import type { NoteContent } from '@/types/note'

interface TextEditorProps {
  initialContent?: NoteContent
  onSave: (content: NoteContent) => void
}

export function TextEditor({ initialContent, onSave }: TextEditorProps) {
  const editor = useEditor({
    extensions: [StarterKit],
    content: initialContent,  // TipTap accepts block-based JSON directly
    onUpdate: ({ editor }) => {
      const json = editor.getJSON() as NoteContent  // Get block-based JSON
      onSave(json)
    },
  })
  
  return <EditorContent editor={editor} />
}

// Usage in note creation
function CreateNote() {
  const { mutate: createNote } = useNoteCreate()
  
  const handleSave = (content: NoteContent) => {
    createNote({
      content,  // Block-based JSON, NOT HTML string
      projectId: selectedProject,
    })
  }
  
  return <TextEditor onSave={handleSave} />
}

// Usage in note editing
function EditNote({ note }: { note: FullNote }) {
  const { mutate: updateNote } = useNoteUpdate()
  
  const handleSave = (content: NoteContent) => {
    updateNote({
      noteId: note.noteId,
      updates: { content },  // Block-based JSON
    })
  }
  
  return <TextEditor initialContent={note.content} onSave={handleSave} />
}
```

## Content Validation

Backend MUST validate block structure:

```typescript
// Example validation rules (backend)
interface BlockValidation {
  allowedBlockTypes: string[]
  allowedMarks: string[]
  maxBlockDepth: number
  maxContentLength: number
}

const validation: BlockValidation = {
  allowedBlockTypes: ['paragraph', 'heading', 'bulletList', 'orderedList', 'codeBlock', 'blockquote', 'image', 'divider'],
  allowedMarks: ['bold', 'italic', 'code', 'strike', 'link'],
  maxBlockDepth: 10,
  maxContentLength: 100000,  // Total characters across all blocks
}
```

## Mobile Considerations

### Pagination Recommendations
- **Desktop**: 10 notes per page
- **Mobile**: 5 notes per page (faster load, less memory)
- **Tablet**: 7 notes per page

```typescript
// Adaptive page size based on device
const isMobile = useMediaQuery('(max-width: 768px)')
const isTablet = useMediaQuery('(min-width: 769px) and (max-width: 1024px)')

const pageSize = isMobile ? 5 : isTablet ? 7 : 10
```

### Response Optimization
- **Content truncation**: Extract plain text from blocks for list views
- **Selective fields**: Consider adding `fields` query param to reduce payload
- **Block-level queries**: PostgreSQL JSONB enables querying specific blocks

```typescript
// Extract plain text from block-based content for previews
import { extractPlainText } from '@/types/note'

function NoteCard({ note }: { note: FullNote }) {
  const preview = extractPlainText(note.content, 200)  // First 200 chars
  
  return (
    <div>
      <p className="truncate">{preview}</p>
    </div>
  )
}

// Search within blocks (future backend enhancement)
// SELECT * FROM notes WHERE content @> '[{"type": "codeBlock"}]'
// Find all notes containing code blocks
```

### Network Resilience
- **Retry strategy**: Exponential backoff for failed requests
- **Timeout**: Longer timeouts for mobile networks (10s vs 5s)
- **Offline detection**: Disable mutations when offline

```typescript
// Mobile-aware axios configuration
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: isMobile ? 10000 : 5000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Retry logic for mobile
axiosInstance.interceptors.response.use(
  response => response,
  async error => {
    const config = error.config
    if (!config || !config.retry) config.retry = 0
    
    if (config.retry < 3 && error.code === 'ECONNABORTED') {
      config.retry += 1
      await new Promise(resolve => setTimeout(resolve, 1000 * config.retry))
      return axiosInstance(config)
    }
    
    return Promise.reject(error)
  }
)
```

### Touch-Optimized Actions
For mobile swipe gestures, batch operations may be needed:

```typescript
// Batch archive multiple notes (future enhancement)
export function batchArchiveNotes(noteIds: string[]) {
  return Promise.all(noteIds.map(id => archiveNote(id)))
}

// Batch delete with undo support
export function batchDeleteNotes(noteIds: string[]) {
  return Promise.all(noteIds.map(id => deleteNote(id)))
}
```
