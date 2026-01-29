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

### Working with Block-Based Content

**IMPORTANT**: Notes use block-based JSON structure (Notion/Editor.js style), NOT HTML strings.

```typescript
// types/note.ts - Content structure
import type { NoteContent } from '@/types/note'

// Example: Creating a note with block-based content
const newNoteContent: NoteContent = {
  type: 'doc',
  content: [
    {
      id: 'block-1',
      type: 'heading',
      attrs: { level: 1 },
      content: [{ type: 'text', text: 'My First Note' }]
    },
    {
      id: 'block-2',
      type: 'paragraph',
      content: [
        { type: 'text', text: 'This is ' },
        { type: 'text', text: 'bold', marks: [{ type: 'bold' }] },
        { type: 'text', text: ' text.' }
      ]
    },
    {
      id: 'block-3',
      type: 'codeBlock',
      attrs: { language: 'javascript' },
      content: [{ type: 'text', text: 'console.log("Hello World")' }]
    }
  ],
  version: '1.0'
}

// Using TipTap editor (handles block-based JSON natively)
import { useEditor } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'

function NoteEditor() {
  const editor = useEditor({
    extensions: [StarterKit],
    content: note.content,  // TipTap accepts block-based JSON directly
    onUpdate: ({ editor }) => {
      const json = editor.getJSON() as NoteContent  // Get block structure
      saveNote({ content: json })  // Save as JSON, NOT HTML
    },
  })
  
  return <EditorContent editor={editor} />
}

// Extract plain text from blocks (for previews, search)
import { extractPlainText } from '@/types/note'

function NotePreview({ note }: { note: FullNote }) {
  const preview = extractPlainText(note.content, 200)  // First 200 chars
  return <p className="line-clamp-3">{preview}</p>
}

// DON'T: Store HTML
const wrongContent = "<p><strong>Bold</strong> text</p>"  // ❌ Wrong

// DO: Store block-based JSON
const correctContent: NoteContent = {  // ✅ Correct
  type: 'doc',
  content: [
    {
      id: 'block-1',
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Bold', marks: [{ type: 'bold' }] },
        { type: 'text', text: ' text' }
      ]
    }
  ],
  version: '1.0'
}
```

**Why block-based JSON?**
- ✅ Structured data in PostgreSQL JSONB
- ✅ Query specific blocks (find all code blocks, headings)
- ✅ Partial updates (modify single blocks)
- ✅ Platform-independent format
- ✅ TipTap native format (no conversion)
- ❌ HTML is monolithic, hard to query, XSS risks

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

## Mobile Development

### Testing on Mobile Devices

**Option 1: Network Access (Recommended)**

```bash
# Find your local IP address
# Windows
ipconfig | findstr IPv4

# Start dev server with network access
pnpm dev --host

# Access from mobile device at:
# http://192.168.x.x:5173
```

**Option 2: Browser DevTools**

```bash
# Chrome DevTools Device Emulation
# 1. Open DevTools (F12)
# 2. Click device toggle (Ctrl+Shift+M)
# 3. Select device preset or custom dimensions

# Common test devices:
# - iPhone 12 Pro (390x844)
# - iPhone SE (375x667)
# - iPad Air (820x1180)
# - Samsung Galaxy S20 (360x800)
```

**Option 3: Real Device Testing**

Use Chrome Remote Debugging:
```bash
# 1. Enable USB debugging on Android device
# 2. Connect via USB
# 3. Open chrome://inspect in desktop Chrome
# 4. Select device and inspect page
```

### Responsive Design Workflow

**1. Mobile-First Development**

Start with mobile styles, enhance for desktop:

```tsx
// ❌ Wrong: Desktop-first
<div className="w-96 md:w-64"> // Scales down on mobile

// ✅ Correct: Mobile-first
<div className="w-full md:w-96"> // Scales up on desktop
```

**2. Test Breakpoints**

```css
/* Tailwind breakpoints */
sm: 640px  /* Mobile landscape */
md: 768px  /* Tablet */
lg: 1024px /* Desktop */
xl: 1280px /* Large desktop */
```

**3. Touch Target Guidelines**

```tsx
// Minimum 44px tap targets
<button className="min-h-[44px] min-w-[44px] p-3">
  <Icon className="w-5 h-5" />
</button>

// Increase spacing on mobile
<div className="space-y-3 lg:space-y-2">
  {items.map(item => <Item key={item.id} {...item} />)}
</div>
```

**4. Responsive Typography**

```css
/* globals.css */
html {
  font-size: 16px; /* Mobile base */
}

@media (min-width: 768px) {
  html {
    font-size: 18px; /* Desktop base */
  }
}
```

### Mobile-Specific Testing

**Touch Gestures**

```typescript
// Test swipe actions
// 1. Add swipe handlers to component
// 2. Test on real device (simulators may not work well)
// 3. Verify swipe distance threshold (50px recommended)

// Test in DevTools:
// 1. Enable touch simulation
// 2. Use mouse to simulate swipe
// 3. Check console logs for debug info
```

**Performance Testing**

```bash
# Test on slow 3G network
# Chrome DevTools > Network tab > Throttling > Slow 3G

# Lighthouse mobile audit
pnpm build
pnpm preview
# DevTools > Lighthouse > Mobile > Generate report

# Target scores:
# Performance: >90
# Accessibility: >95
# Best Practices: >90
```

### Common Mobile Issues

**Issue 1: 300ms tap delay**

```css
/* Add to globals.css */
button, a, [role="button"] {
  touch-action: manipulation;
}
```

**Issue 2: Viewport on iOS**

```html
<!-- index.html -->
<meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover">
```

**Issue 3: Input zoom on iOS**

```css
/* Prevent input zoom on iOS */
input, textarea, select {
  font-size: 16px; /* Minimum to prevent zoom */
}
```

**Issue 4: Horizontal scroll**

```css
/* Prevent horizontal overflow */
html, body {
  overflow-x: hidden;
  max-width: 100vw;
}
```

### Mobile Performance Optimization

**Code Splitting by Route**

```typescript
// routes/routes.tsx
import { lazy } from 'react'

const NoteEditor = lazy(() => import('@/features/note/note-editor'))
const ProjectPage = lazy(() => import('@/pages/project/project'))

// Heavy components load only when needed
```

**Conditional Loading**

```typescript
// Only load TipTap on desktop or when needed
const isMobile = useMediaQuery('(max-width: 768px)')

return (
  <Suspense fallback={<EditorSkeleton />}>
    {!isMobile && <RichTextEditor />}
    {isMobile && <SimplifiedEditor />}
  </Suspense>
)
```

**Image Optimization**

```tsx
// Lazy load images
<img
  loading="lazy"
  src={imageUrl}
  alt={alt}
  className="w-full h-auto"
/>

// Or use intersection observer
const { ref, inView } = useInView({ triggerOnce: true })

return (
  <div ref={ref}>
    {inView && <img src={imageUrl} alt={alt} />}
  </div>
)
```

### Mobile Debug Tools

**Console Logging**

```typescript
// Add mobile debug overlay
function DebugOverlay() {
  const [info, setInfo] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
    online: navigator.onLine,
  })
  
  useEffect(() => {
    const update = () => setInfo({
      width: window.innerWidth,
      height: window.innerHeight,
      online: navigator.onLine,
    })
    
    window.addEventListener('resize', update)
    window.addEventListener('online', update)
    window.addEventListener('offline', update)
    
    return () => {
      window.removeEventListener('resize', update)
      window.removeEventListener('online', update)
      window.removeEventListener('offline', update)
    }
  }, [])
  
  if (import.meta.env.PROD) return null
  
  return (
    <div className="fixed bottom-0 left-0 p-2 bg-black/80 text-white text-xs font-mono">
      <div>Viewport: {info.width}x{info.height}</div>
      <div>Network: {info.online ? 'Online' : 'Offline'}</div>
    </div>
  )
}
```

**Touch Event Visualization**

```typescript
// Visualize touch points (development only)
function TouchDebugger() {
  const [touches, setTouches] = useState<Array<{ x: number; y: number }>>([])
  
  useEffect(() => {
    const handleTouch = (e: TouchEvent) => {
      const points = Array.from(e.touches).map(touch => ({
        x: touch.clientX,
        y: touch.clientY,
      }))
      setTouches(points)
    }
    
    window.addEventListener('touchstart', handleTouch)
    window.addEventListener('touchmove', handleTouch)
    window.addEventListener('touchend', () => setTouches([]))
    
    return () => {
      window.removeEventListener('touchstart', handleTouch)
      window.removeEventListener('touchmove', handleTouch)
      window.removeEventListener('touchend', () => setTouches([]))
    }
  }, [])
  
  if (import.meta.env.PROD) return null
  
  return (
    <>
      {touches.map((touch, i) => (
        <div
          key={i}
          className="fixed w-12 h-12 -ml-6 -mt-6 rounded-full bg-red-500/50 pointer-events-none"
          style={{ left: touch.x, top: touch.y }}
        />
      ))}
    </>
  )
}
```

## Resources

- **React Docs**: https://react.dev
- **TypeScript Handbook**: https://www.typescriptlang.org/docs/
- **TanStack Query**: https://tanstack.com/query/latest
- **Radix UI**: https://www.radix-ui.com/
- **Tailwind CSS**: https://tailwindcss.com/
- **Vite**: https://vitejs.dev/
- **Mobile Web Best Practices**: https://web.dev/mobile/
- **Touch Events**: https://developer.mozilla.org/en-US/docs/Web/API/Touch_events

## Getting Help

1. Check existing code for similar patterns
2. Review [data-model.md](./data-model.md) for type definitions
3. Check [contracts/](./contracts/) for API documentation
4. Search project issues on GitHub
5. Ask team members in Slack/Discord

## Next Steps

After setup, proceed to implementation tasks defined in `tasks.md` (generated by `/speckit.tasks` command).
