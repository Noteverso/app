# Testing Setup Guide

## Unit & Component Tests (Vitest + React Testing Library)

### Installation

```bash
cd frontend/web
pnpm add -D vitest @testing-library/react @testing-library/user-event @testing-library/jest-dom jsdom
```

### Configuration

Create `frontend/web/vitest.config.ts`:

```typescript
import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
})
```

Create `frontend/web/src/test/setup.ts`:

```typescript
import '@testing-library/jest-dom'
import { expect, afterEach } from 'vitest'
import { cleanup } from '@testing-library/react'

afterEach(() => {
  cleanup()
})
```

### Add test script to package.json

```json
{
  "scripts": {
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage"
  }
}
```

### Run Tests

```bash
pnpm test                    # Run all tests
pnpm test signup             # Run tests matching "signup"
pnpm test:ui                 # Run with UI
pnpm test:coverage           # Run with coverage report
```

## E2E Tests (Playwright)

### Installation

```bash
cd frontend/web
pnpm add -D @playwright/test
npx playwright install
```

### Configuration

Create `frontend/web/playwright.config.ts`:

```typescript
import { defineConfig, devices } from '@playwright/test'

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'pnpm dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
})
```

### Add E2E script to package.json

```json
{
  "scripts": {
    "test:e2e": "playwright test",
    "test:e2e:ui": "playwright test --ui",
    "test:e2e:debug": "playwright test --debug"
  }
}
```

### Run E2E Tests

```bash
pnpm test:e2e                # Run all E2E tests
pnpm test:e2e:ui             # Run with UI mode
pnpm test:e2e:debug          # Run in debug mode
npx playwright show-report   # Show test report
```

## Test Files Created

### Unit Tests
- `src/pages/auth/__tests__/signup-validation.test.ts` - Form validation logic tests
- `src/pages/auth/__tests__/signup-page.test.tsx` - Component interaction tests

### E2E Tests
- `e2e/signup.spec.ts` - Complete signup flow tests

## Test Coverage

### Validation Tests
- ✅ Email validation (required, format)
- ✅ Username validation (length, characters)
- ✅ Password validation (length, complexity, match)
- ✅ Captcha validation (length, numeric)

### Component Tests
- ✅ Form rendering
- ✅ Validation error display
- ✅ Captcha sending
- ✅ Form submission
- ✅ Navigation after success

### E2E Tests
- ✅ Complete signup flow
- ✅ Validation errors
- ✅ Captcha flow
- ✅ Loading states
- ✅ Navigation between pages

## Running All Tests

```bash
# Unit tests
cd frontend/web && pnpm test

# E2E tests (requires dev server running)
cd frontend/web && pnpm test:e2e
```
