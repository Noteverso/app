# Project API Contract

**Base URL**: `/api/v1/projects`  
**Authentication**: Required (JWT token in header)

## Endpoints

### List Projects
```http
GET /api/v1/projects?showNoteCount=true
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "projectId": "string",
    "name": "string",
    "color": "string",
    "isFavorite": 0 | 1,
    "isArchived": 0 | 1,
    "noteCount": number,
    "inboxProject": boolean
  }
]

Errors:
- 401: Unauthorized
```

### Create Project
```http
POST /api/v1/projects
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "name": "string (required, 1-100 chars)",
  "color": "string (required)",
  "isFavorite": 0 | 1 (optional, default 0)
}

Response: 201 Created
{
  "projectId": "string"
}

Errors:
- 400: Invalid request body
- 401: Unauthorized
- 409: Project name already exists
```

### Update Project
```http
PATCH /api/v1/projects/:projectId
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "name": "string" (optional),
  "color": "string" (optional)
}

Response: 200 OK
(no body)

Errors:
- 400: Invalid request body
- 401: Unauthorized
- 404: Project not found
- 409: Project name already exists
```

### Delete Project
```http
DELETE /api/v1/projects/:projectId
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 400: Cannot delete project with notes
- 401: Unauthorized
- 403: Cannot delete inbox project
- 404: Project not found
```

### Archive/Unarchive Project
```http
PATCH /api/v1/projects/:projectId/archive
Authorization: Bearer {token}

Response: 200 OK
(no body)

PATCH /api/v1/projects/:projectId/unarchive
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 403: Cannot archive inbox project
- 404: Project not found
```

### Toggle Favorite
```http
PATCH /api/v1/projects/:projectId/favorite
Authorization: Bearer {token}

Response: 200 OK
(no body)

PATCH /api/v1/projects/:projectId/unfavorite
Authorization: Bearer {token}

Response: 200 OK
(no body)

Errors:
- 401: Unauthorized
- 404: Project not found
```

### Get Project Notes (Paginated)
```http
GET /api/v1/projects/:projectId/notes?pageIndex=1&pageSize=10
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
- 404: Project not found
```

### Get Inbox Notes
```http
GET /api/v1/projects/inbox/notes?pageIndex=1&pageSize=10
Authorization: Bearer {token}

Query Parameters:
- pageIndex: number (required, 1-based)
- pageSize: number (required, 1-100)

Response: 200 OK
(Same structure as Get Project Notes)

Errors:
- 401: Unauthorized
```

## TypeScript Integration

```typescript
// api/project/project.ts

import { request } from '@/lib/http'
import type { NewProject, UpdateProject, FullProject } from '@/types/project'
import type { NotePageLoaderData, NotePageRequestParams } from '@/types/note'

export function getProjects() {
  return request<FullProject[]>({
    url: '/api/v1/projects',
    method: 'get',
    params: { showNoteCount: true },
  })
}

export function createProject(newProject: NewProject) {
  return request<string>({
    url: '/api/v1/projects',
    method: 'post',
    data: newProject,
  })
}

export function updateProject(projectId: string, updates: UpdateProject) {
  return request<void>({
    url: `/api/v1/projects/${projectId}`,
    method: 'patch',
    data: updates,
  })
}

export function deleteProject(projectId: string) {
  return request<void>({
    url: `/api/v1/projects/${projectId}`,
    method: 'delete',
  })
}

export function archiveProject(projectId: string) {
  return request<void>({
    url: `/api/v1/projects/${projectId}/archive`,
    method: 'patch',
  })
}

export function unarchiveProject(projectId: string) {
  return request<void>({
    url: `/api/v1/projects/${projectId}/unarchive`,
    method: 'patch',
  })
}

export function toggleProjectFavorite(projectId: string, isFavorite: boolean) {
  const endpoint = isFavorite ? 'favorite' : 'unfavorite'
  return request<void>({
    url: `/api/v1/projects/${projectId}/${endpoint}`,
    method: 'patch',
  })
}

export function getProjectNotes(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: `/api/v1/projects/${params.objectId}/notes`,
    method: 'get',
    params: {
      pageIndex: params.pageIndex,
      pageSize: params.pageSize,
    },
  })
}

export function getInboxNotes(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: '/api/v1/projects/inbox/notes',
    method: 'get',
    params: {
      pageIndex: params.pageIndex,
      pageSize: params.pageSize,
    },
  })
}
```

## Project Colors

Predefined color options (from constants):
```typescript
export const PROJECT_COLORS = [
  'blue', 'red', 'green', 'yellow', 'purple',
  'pink', 'orange', 'teal', 'indigo', 'gray'
]
```

## Business Rules
- Inbox project (`inboxProject: true`) is created automatically for each user
- Inbox project cannot be deleted or archived
- Projects with notes cannot be deleted (must be empty or move notes first)
- Project names must be unique per user
- Favorite projects appear first in sidebar
- Archived projects hidden from main view but accessible

## Mobile Considerations

### Responsive List Display
- **Mobile**: Show project name + note count only (compact view)
- **Desktop**: Show project name, color indicator, note count, favorite icon

```tsx
// Mobile-optimized project list
function ProjectListMobile({ projects }: { projects: FullProject[] }) {
  return (
    <div className="space-y-1">
      {projects.map(project => (
        <Link
          key={project.projectId}
          to={`/projects/${project.projectId}`}
          className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-100"
        >
          <div className="flex items-center gap-2">
            <div 
              className="w-3 h-3 rounded-full"
              style={{ backgroundColor: `var(--color-${project.color})` }}
            />
            <span className="text-sm font-medium truncate max-w-[180px]">
              {project.name}
            </span>
          </div>
          {project.noteCount > 0 && (
            <span className="text-xs text-gray-500">{project.noteCount}</span>
          )}
        </Link>
      ))}
    </div>
  )
}
```

### Color Picker Optimization
On mobile, use simplified color picker:
```tsx
// Full grid on desktop, horizontal scroll on mobile
<div className="grid grid-cols-5 gap-2 lg:grid-cols-10">
  {PROJECT_COLORS.map(color => (
    <button
      key={color}
      className="w-10 h-10 rounded-full lg:w-8 lg:h-8"
      style={{ backgroundColor: `var(--color-${color})` }}
      onClick={() => setColor(color)}
    />
  ))}
</div>
```

### Touch Interactions
- Long-press project item → context menu (rename, archive, delete)
- Swipe left → quick archive
- Swipe right → favorite toggle

```typescript
// Project item with swipe actions
function ProjectItem({ project }: { project: FullProject }) {
  const { archive, favorite } = useProjectActions()
  
  const swipeHandlers = useSwipeActions({
    onSwipeLeft: () => archive.mutate(project.projectId),
    onSwipeRight: () => favorite.mutate(project.projectId),
  })
  
  const longPressHandlers = useLongPress({
    onLongPress: () => openContextMenu(project),
  })
  
  return (
    <div {...swipeHandlers} {...longPressHandlers}>
      {/* Project content */}
    </div>
  )
}
```

### Performance
- **Pagination**: Not needed for projects (typically < 50 per user)
- **Cache**: Aggressive caching (projects change infrequently)
- **Prefetch**: Prefetch project notes on hover (desktop) or tap (mobile with delay)

```typescript
export function useProjects() {
  return useQuery({
    queryKey: ['projects'],
    queryFn: getProjects,
    staleTime: 300000, // 5 minutes (projects change rarely)
    cacheTime: 600000, // 10 minutes
  })
}
```
