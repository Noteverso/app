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

## Mobile Considerations

### Compact Label Display
Mobile views show labels as chips with abbreviated text:

```tsx
// Desktop: Full label names
// Mobile: Truncated labels with tooltip
function LabelChip({ label }: { label: { labelId: string; name: string } }) {
  const isMobile = useMediaQuery('(max-width: 768px)')
  
  return (
    <span 
      className={cn(
        "inline-flex items-center rounded-full bg-gray-100 px-2 py-1 text-xs",
        isMobile && "max-w-[80px]"
      )}
      title={label.name}
    >
      <span className={isMobile ? "truncate" : ""}>
        {label.name}
      </span>
    </span>
  )
}
```

### Label Selector Optimization
On mobile, use bottom sheet instead of dropdown:

```tsx
// Mobile-optimized label selector
function LabelSelectorMobile({ selectedLabels, onChange }) {
  const { data: labels } = useLabels()
  const [isOpen, setIsOpen] = useState(false)
  
  return (
    <>
      <button 
        onClick={() => setIsOpen(true)}
        className="flex items-center gap-2 p-3 w-full text-left"
      >
        <TagIcon className="w-5 h-5" />
        <span className="text-sm">
          {selectedLabels.length === 0 
            ? "Add labels" 
            : `${selectedLabels.length} labels`}
        </span>
      </button>
      
      <ResponsiveDialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="max-h-[70vh] overflow-y-auto">
          <DialogTitle>Select Labels</DialogTitle>
          <div className="space-y-2">
            {labels?.map(label => (
              <label 
                key={label.labelId}
                className="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50"
              >
                <input
                  type="checkbox"
                  checked={selectedLabels.includes(label.labelId)}
                  onChange={(e) => {
                    if (e.target.checked) {
                      onChange([...selectedLabels, label.labelId])
                    } else {
                      onChange(selectedLabels.filter(id => id !== label.labelId))
                    }
                  }}
                  className="w-5 h-5"
                />
                <span className="flex-1 text-sm">{label.name}</span>
              </label>
            ))}
          </div>
        </DialogContent>
      </ResponsiveDialog>
    </>
  )
}
```

### Touch Gestures
- **Swipe left** on label item → delete label
- **Swipe right** on label item → toggle favorite
- **Long press** → rename label dialog

```typescript
function LabelItem({ label }: { label: FullLabel }) {
  const { remove, favorite } = useLabelActions()
  
  const swipeHandlers = useSwipeActions({
    onSwipeLeft: () => {
      if (confirm(`Delete "${label.name}"? This will remove it from all notes.`)) {
        remove.mutate(label.labelId)
      }
    },
    onSwipeRight: () => favorite.mutate(label.labelId),
  })
  
  return (
    <div {...swipeHandlers} className="touch-manipulation">
      {/* Label content */}
    </div>
  )
}
```

### Performance
- **No pagination**: Labels typically < 100 per user
- **Aggressive caching**: Labels change infrequently
- **Prefetch**: Load labels on app initialization

```typescript
export function useLabels() {
  return useQuery({
    queryKey: ['labels'],
    queryFn: getLabels,
    staleTime: 300000, // 5 minutes
    cacheTime: 600000, // 10 minutes
  })
}

// Prefetch labels on app mount
export function usePrefetchLabels() {
  const queryClient = useQueryClient()
  
  useEffect(() => {
    queryClient.prefetchQuery({
      queryKey: ['labels'],
      queryFn: getLabels,
    })
  }, [])
}
```

### Label Creation on Mobile
Simplified inline creation:

```tsx
function QuickLabelCreate() {
  const [name, setName] = useState('')
  const { mutate: createLabel } = useLabelCreate()
  
  return (
    <div className="flex gap-2 p-3 border-t">
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="New label..."
        className="flex-1 px-3 py-2 text-sm border rounded-lg"
        maxLength={50}
      />
      <button
        onClick={() => {
          createLabel({ name })
          setName('')
        }}
        disabled={!name.trim()}
        className="px-4 py-2 text-sm bg-blue-500 text-white rounded-lg disabled:opacity-50"
      >
        Add
      </button>
    </div>
  )
}
```
