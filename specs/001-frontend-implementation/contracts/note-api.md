# Note API Contract

**Base URL**: `/api/v1/notes`  
**Authentication**: Required (JWT token in header)

## Endpoints

### Create Note
```http
POST /api/v1/notes
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "content": "string (required, 1-100000 chars)",
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
- 400: Invalid request body
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
  "content": "string",
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
  "content": "string" (optional),
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
