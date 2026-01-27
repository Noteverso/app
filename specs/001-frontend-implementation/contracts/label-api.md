# Label API Contract

**Base URL**: `/api/v1/labels`  
**Authentication**: Required (JWT token in header)

## Endpoints

### List Labels
```http
GET /api/v1/labels
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "labelId": "string",
    "name": "string",
    "isFavorite": 0 | 1,
    "noteCount": number (optional)
  }
]

Errors:
- 401: Unauthorized
```

### Create Label
```http
POST /api/v1/labels
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "name": "string (required, 1-50 chars)"
}

Response: 201 Created
{
  "labelId": "string"
}

Errors:
- 400: Invalid request body
- 401: Unauthorized
- 409: Label name already exists
```

### Update Label
```http
PATCH /api/v1/labels/:labelId
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "name": "string (required, 1-50 chars)"
}

Response: 200 OK
(no body)

Errors:
- 400: Invalid request body
- 401: Unauthorized
- 404: Label not found
- 409: Label name already exists
```

### Delete Label
```http
DELETE /api/v1/labels/:labelId
Authorization: Bearer {token}

Response: 200 OK
(no body)

Note: Deletes label and removes it from all notes

Errors:
- 401: Unauthorized
- 404: Label not found
```

### Toggle Favorite
```http
PATCH /api/v1/labels/:labelId/favorite
Authorization: Bearer {token}

Response: 200 OK
(no body)

PATCH /api/v1/labels/:labelId/unfavorite
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Label not found
```

### Get Notes by Label (Paginated)
```http
GET /api/v1/labels/:labelId/notes?pageIndex=1&pageSize=10
Authorization: Bearer {token}

Query Parameters:
- pageIndex: number (required, 1-based)
- pageSize: number (required, 1-100)

Response: 200 OK
{
  "pageIndex": number,
  "pageSize": number,
  "total": number,
  "records": [
    {
      "noteId": "string",
      "content": "string",
      "project": { "projectId": "string", "name": "string" },
      "labels": [{ "labelId": "string", "name": "string" }],
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
  ]
}

Errors:
- 401: Unauthorized
- 404: Label not found
```

### Get Label Select Items
```http
GET /api/v1/labels/select
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "value": "string (labelId)",
    "label": "string (name)"
  }
]

Note: Simplified format for dropdown selects

Errors:
- 401: Unauthorized
```

## TypeScript Integration

```typescript
// api/label/label.ts

import { request } from '@/lib/http'
import type { NewLabel, UpdateLabel, FullLabel } from '@/types/label'
import type { NotePageLoaderData, NotePageRequestParams } from '@/types/note'

export function getLabels() {
  return request<FullLabel[]>({
    url: '/api/v1/labels',
    method: 'get',
  })
}

export function createLabel(newLabel: NewLabel) {
  return request<string>({
    url: '/api/v1/labels',
    method: 'post',
    data: newLabel,
  })
}

export function updateLabel(labelId: string, updates: UpdateLabel) {
  return request<void>({
    url: `/api/v1/labels/${labelId}`,
    method: 'patch',
    data: updates,
  })
}

export function deleteLabel(labelId: string) {
  return request<void>({
    url: `/api/v1/labels/${labelId}`,
    method: 'delete',
  })
}

export function toggleLabelFavorite(labelId: string, isFavorite: boolean) {
  const endpoint = isFavorite ? 'favorite' : 'unfavorite'
  return request<void>({
    url: `/api/v1/labels/${labelId}/${endpoint}`,
    method: 'patch',
  })
}

export function getLabelNotes(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: `/api/v1/labels/${params.objectId}/notes`,
    method: 'get',
    params: {
      pageIndex: params.pageIndex,
      pageSize: params.pageSize,
    },
  })
}

export function getLabelSelectItems() {
  return request<Array<{ value: string; label: string }>>({
    url: '/api/v1/labels/select',
    method: 'get',
  })
}
```

## Usage in Notes

Labels are applied to notes through the note update endpoint:

```typescript
// Add labels to a note
updateNote(noteId, {
  labels: ['label-id-1', 'label-id-2', 'label-id-3']
})

// Remove all labels from a note
updateNote(noteId, {
  labels: []
})
```

## Business Rules
- Label names must be unique per user
- Deleting a label removes it from all notes (cascade delete)
- Labels can be applied to notes across different projects
- Maximum 10 labels per note (enforced by backend)
- Favorite labels appear first in sidebar
- Labels are sorted alphabetically within favorite/non-favorite groups
