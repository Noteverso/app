import { fireEvent, render, screen } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { Layout } from './layout'

const CREATED_PROJECT = {
  projectId: 'project-inline-1',
  name: 'Inline Created Project',
  color: '#123456',
  noteCount: 0,
  isFavorite: 0,
  inboxProject: false,
}

const layoutTestState = vi.hoisted(() => ({
  loadedProjects: [
    {
      projectId: 'inbox-1',
      name: 'Inbox',
      color: '#000000',
      noteCount: 12,
      isFavorite: 0,
      inboxProject: true,
    },
    {
      projectId: 'project-1',
      name: 'Project One',
      color: '#111111',
      noteCount: 3,
      isFavorite: 0,
      inboxProject: false,
    },
  ],
  revalidate: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    useLoaderData: () => layoutTestState.loadedProjects,
    useLocation: () => ({ pathname: '/app/inbox' }),
    useRevalidator: () => ({ revalidate: layoutTestState.revalidate }),
    Outlet: ({ context }: { context: { upsertProject: (project: typeof CREATED_PROJECT) => void } }) => (
      <button type="button" onClick={() => context.upsertProject(CREATED_PROJECT)}>
        Create from outlet
      </button>
    ),
  }
})

vi.mock('./nav/nav', () => ({
  Nav: ({ projects }: { projects: Array<{ name: string }> }) => (
    <div data-testid="nav-projects">{projects.map(project => project.name).join(',')}</div>
  ),
}))

vi.mock('@/components/ui/sheet/sheet', () => ({
  Sheet: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SheetContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SheetDescription: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SheetHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SheetTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SheetTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/button/button', () => ({
  Button: ({ children, ...props }: React.ComponentProps<'button'>) => <button {...props}>{children}</button>,
}))

vi.mock('@/components/ui/toast/toaster', () => ({
  Toaster: () => null,
}))

vi.mock('@/features/note/note-create-dialog', () => ({
  NoteCreateDialog: () => null,
}))

describe('Layout project syncing', () => {
  beforeEach(() => {
    layoutTestState.revalidate.mockReset()
    layoutTestState.loadedProjects = [
      {
        projectId: 'inbox-1',
        name: 'Inbox',
        color: '#000000',
        noteCount: 12,
        isFavorite: 0,
        inboxProject: true,
      },
      {
        projectId: 'project-1',
        name: 'Project One',
        color: '#111111',
        noteCount: 3,
        isFavorite: 0,
        inboxProject: false,
      },
    ]

    globalThis.ResizeObserver = class {
      observe() {}
      unobserve() {}
      disconnect() {}
    } as unknown as typeof ResizeObserver
  })

  it('updates the sidebar project list when outlet content upserts a created project', () => {
    render(<Layout />)

    expect(screen.getAllByTestId('nav-projects')[0]).toHaveTextContent('Inbox,Project One')

    fireEvent.click(screen.getByRole('button', { name: 'Create from outlet' }))

    for (const nav of screen.getAllByTestId('nav-projects')) {
      expect(nav).toHaveTextContent('Inline Created Project')
    }
  })

  it('keeps app-main content full width without the old max-width constraint', () => {
    const { container } = render(<Layout />)

    const appMainContent = container.querySelector('#app-main__content')

    expect(appMainContent).not.toBeNull()
    expect(appMainContent).toHaveClass('w-full')
    expect(appMainContent?.className).not.toContain('max-w-[var(--main-content-max-width)]')
  })
})
