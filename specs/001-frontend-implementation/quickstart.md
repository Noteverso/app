# Quickstart: Development Guide

**Feature**: Complete Frontend Implementation  
**Date**: 2026-01-27  
**For**: Frontend developers working on the note-taking application

## Prerequisites

- **Node.js**: 18.x or higher
- **pnpm**: 8.x or higher (workspace manager)
- **Git**: For version control
- **VS Code**: Recommended IDE
- **Browser**: Chrome/Firefox DevTools for debugging

## Project Setup

### 1. Clone and Install

```bash
# Clone repository
git clone https://github.com/byodian/noteverso
cd noteverso

# Checkout feature branch
git checkout 001-frontend-implementation

# Install dependencies (uses pnpm workspace)
pnpm install

# Install frontend dependencies specifically
pnpm web:install
```

### 2. Environment Configuration

Create `.env` file in `frontend/web/`:

```env
# API Base URL (development)
VITE_API_BASE_URL=http://localhost:8080

# Feature flags (optional)
VITE_ENABLE_SEARCH=true
VITE_ENABLE_ATTACHMENTS=true
```

### 3. Start Development Server

```bash
# Start frontend dev server (from repo root)
pnpm web:dev

# Or navigate to frontend/web
cd frontend/web
pnpm dev
```

Dev server runs at: `http://localhost:5173`

### 4. Start Backend (Optional)

To test with real API:

```bash
# In separate terminal, navigate to backend
cd backend

# Run Spring Boot application
./mvnw spring-boot:run

# Or use your IDE to run NoteversoApplication.java
```

Backend API runs at: `http://localhost:8080`

## Project Structure Overview

```
frontend/web/
├── src/
│   ├── api/           # API service layer
│   ├── components/    # Reusable UI components
│   ├── features/      # Feature-specific components
│   ├── hooks/         # Custom React hooks
│   ├── lib/           # Utilities and helpers
│   ├── pages/         # Route page components
│   ├── routes/        # React Router configuration
│   ├── store/         # State management
│   ├── styles/        # Global CSS
│   └── types/         # TypeScript definitions
├── tests/             # Test files
├── public/            # Static assets
└── package.json       # Dependencies
```

## Development Workflow

### 1. Create a New Feature

**Scenario**: Implement label management

```bash
# Create feature branch (if not already on 001-frontend-implementation)
git checkout 001-frontend-implementation
git pull origin 001-frontend-implementation

# Create feature components
mkdir -p src/features/label
touch src/features/label/label-list.tsx
touch src/features/label/label-dialog.tsx
touch src/features/label/label-actions.tsx

# Create custom hook
touch src/hooks/use-labels.ts

# Create API service
mkdir -p src/api/label
touch src/api/label/label.ts
touch src/api/label/types.ts
```

### 2. Implement API Service

```typescript
// src/api/label/label.ts

import { request } from '@/lib/http'
import type { FullLabel, NewLabel } from '@/types/label'

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
```

### 3. Create Custom Hook with TanStack Query

```typescript
// src/hooks/use-labels.ts

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getLabels, createLabel, updateLabel, deleteLabel } from '@/api/label/label'
import type { NewLabel, UpdateLabel } from '@/types/label'

export function useLabels() {
  return useQuery({
    queryKey: ['labels'],
    queryFn: getLabels,
    staleTime: 60000,  // 1 minute
  })
}

export function useLabelCreate() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (newLabel: NewLabel) => createLabel(newLabel),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['labels'] })
    },
  })
}

export function useLabelActions() {
  const queryClient = useQueryClient()
  
  const update = useMutation({
    mutationFn: ({ id, updates }: { id: string; updates: UpdateLabel }) => 
      updateLabel(id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['labels'] })
    },
  })
  
  const remove = useMutation({
    mutationFn: (labelId: string) => deleteLabel(labelId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['labels'] })
      queryClient.invalidateQueries({ queryKey: ['notes'] })
    },
  })
  
  return { update, remove }
}
```

### 4. Create React Component

```typescript
// src/features/label/label-list.tsx

import { useLabels } from '@/hooks/use-labels'
import { LabelItem } from './label-item'

export function LabelList() {
  const { data: labels, isLoading, error } = useLabels()
  
  if (isLoading) return <div>Loading labels...</div>
  if (error) return <div>Error loading labels</div>
  if (!labels) return null
  
  // Sort: favorites first, then alphabetically
  const sortedLabels = [...labels].sort((a, b) => {
    if (a.isFavorite !== b.isFavorite) {
      return b.isFavorite - a.isFavorite
    }
    return a.name.localeCompare(b.name)
  })
  
  return (
    <div className="space-y-1">
      {sortedLabels.map(label => (
        <LabelItem key={label.labelId} label={label} />
      ))}
    </div>
  )
}
```

### 5. Add Route

```typescript
// src/routes/routes.tsx

import { Label } from '@/pages/label/label'

// In children array
{
  path: 'labels/:labelId',
  element: <Label />,
  loader: protectedLoader(labelNotesLoader),
}
```

### 6. Test Your Changes

```bash
# Run type check
pnpm tsc --noEmit

# Run linter
pnpm lint

# Fix linting issues
pnpm lint:fix

# Run tests (when configured)
pnpm test
```

### 7. Commit Changes

```bash
git add .
git commit -m "feat: implement label management UI"
git push origin 001-frontend-implementation
```

## Common Tasks

### Adding a New Page

```bash
# Create page directory
mkdir src/pages/my-feature

# Create page component
cat > src/pages/my-feature/my-feature.tsx << 'EOF'
export function MyFeature() {
  return (
    <div>
      <h1>My Feature</h1>
    </div>
  )
}
EOF

# Add to routes
# Edit src/routes/routes.tsx
```

### Creating a Custom Hook

```typescript
// src/hooks/use-my-hook.ts

import { useState, useEffect } from 'react'

export function useMyHook(initialValue: string) {
  const [value, setValue] = useState(initialValue)
  
  useEffect(() => {
    // Side effect logic
  }, [value])
  
  return { value, setValue }
}
```

### Adding Radix UI Component

```bash
# Example: Adding a dialog
pnpm add @radix-ui/react-dialog

# Create wrapper in components/ui/
touch src/components/ui/dialog/dialog.tsx
```

### Debugging API Calls

```typescript
// Enable request/response logging
// In src/lib/http.ts

axiosInstance.interceptors.request.use(
  (config) => {
    console.log('Request:', config.method?.toUpperCase(), config.url)
    console.log('Data:', config.data)
    return config
  }
)

axiosInstance.interceptors.response.use(
  (response) => {
    console.log('Response:', response.status, response.data)
    return response
  }
)
```

## Useful Commands

```bash
# Development
pnpm web:dev          # Start dev server
pnpm web:build        # Build for production
pnpm web:preview      # Preview production build

# Code Quality
pnpm lint             # Run ESLint
pnpm lint:fix         # Fix ESLint issues
pnpm tsc --noEmit     # Type check only

# Testing (when configured)
pnpm test             # Run all tests
pnpm test:watch       # Watch mode
pnpm test:coverage    # Coverage report

# Dependencies
pnpm add <package>    # Add dependency
pnpm add -D <package> # Add dev dependency
pnpm update           # Update dependencies
```

## VS Code Setup

### Recommended Extensions

```json
{
  "recommendations": [
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "bradlc.vscode-tailwindcss",
    "ms-vscode.vscode-typescript-next"
  ]
}
```

### Workspace Settings

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.tsdk": "node_modules/typescript/lib",
  "typescript.enablePromptUseWorkspaceTsdk": true
}
```

### Keyboard Shortcuts

- `Ctrl/Cmd + P`: Quick file open
- `Ctrl/Cmd + Shift + P`: Command palette
- `F12`: Go to definition
- `Shift + F12`: Find all references
- `Ctrl/Cmd + .`: Quick fix

## Troubleshooting

### Port already in use

```bash
# Find process using port 5173
lsof -i :5173  # macOS/Linux
netstat -ano | findstr :5173  # Windows

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows

# Or use different port
pnpm dev -- --port 5174
```

### Module not found errors

```bash
# Clear node_modules and reinstall
rm -rf node_modules
rm pnpm-lock.yaml
pnpm install
```

### TypeScript errors

```bash
# Restart TS server in VS Code
# Cmd+Shift+P > "TypeScript: Restart TS Server"

# Or rebuild types
pnpm tsc --build --force
```

### Hot reload not working

```bash
# Check Vite config for HMR settings
# Restart dev server
# Clear browser cache
```

## Testing Guide

### Unit Test Example

```typescript
// src/hooks/__tests__/use-labels.test.ts

import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useLabels } from '../use-labels'

const createWrapper = () => {
  const queryClient = new QueryClient()
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  )
}

test('useLabels fetches labels', async () => {
  const { result } = renderHook(() => useLabels(), {
    wrapper: createWrapper(),
  })
  
  await waitFor(() => expect(result.current.isSuccess).toBe(true))
  
  expect(result.current.data).toBeDefined()
  expect(Array.isArray(result.current.data)).toBe(true)
})
```

### Component Test Example

```typescript
// src/features/label/__tests__/label-list.test.tsx

import { render, screen } from '@testing-library/react'
import { LabelList } from '../label-list'

test('renders label list', () => {
  render(<LabelList />)
  
  expect(screen.getByText(/labels/i)).toBeInTheDocument()
})
```

## Performance Tips

1. **Use React DevTools** to identify unnecessary re-renders
2. **Use React Query DevTools** to inspect cache and queries
3. **Lazy load routes** with `React.lazy()`
4. **Debounce search** inputs (300ms recommended)
5. **Optimize images** in `public/` directory
6. **Use production build** for realistic performance testing

## Resources

- **React Docs**: https://react.dev
- **TypeScript Handbook**: https://www.typescriptlang.org/docs/
- **TanStack Query**: https://tanstack.com/query/latest
- **Radix UI**: https://www.radix-ui.com/
- **Tailwind CSS**: https://tailwindcss.com/
- **Vite**: https://vitejs.dev/

## Getting Help

1. Check existing code for similar patterns
2. Review [data-model.md](./data-model.md) for type definitions
3. Check [contracts/](./contracts/) for API documentation
4. Search project issues on GitHub
5. Ask team members in Slack/Discord

## Next Steps

After setup, proceed to implementation tasks defined in `tasks.md` (generated by `/speckit.tasks` command).
