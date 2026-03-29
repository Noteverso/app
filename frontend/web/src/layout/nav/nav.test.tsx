import type React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { Nav } from './nav'

const navTestState = vi.hoisted(() => ({
  navigate: vi.fn(),
  location: {
    pathname: '/app/inbox',
  },
  toast: vi.fn(),
  deleteProjectApi: vi.fn(),
  archiveProjectApi: vi.fn(),
  createProjectApi: vi.fn(),
  updateProjectApi: vi.fn(),
  favoriteProjectApi: vi.fn(),
  unfavoriteProjectApi: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    Form: ({ children, ...props }: React.ComponentProps<'form'>) => <form {...props}>{children}</form>,
    NavLink: ({
      children,
      className,
      ...props
    }: Omit<React.ComponentProps<'a'>, 'className'> & {
      children: React.ReactNode;
      className?: string | ((args: { isActive: boolean }) => string);
    }) => (
      <a {...props} className={typeof className === 'function' ? className({ isActive: false }) : className}>
        {children}
      </a>
    ),
    useNavigate: () => navTestState.navigate,
    useLocation: () => navTestState.location,
  }
})

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: navTestState.toast,
    dismiss: vi.fn(),
    toasts: [],
  }),
}))

vi.mock('@/api/project/project', () => ({
  createProjectApi: navTestState.createProjectApi,
  updateProjectApi: navTestState.updateProjectApi,
  deleteProjectApi: navTestState.deleteProjectApi,
  archiveProjectApi: navTestState.archiveProjectApi,
  favoriteProjectApi: navTestState.favoriteProjectApi,
  unfavoriteProjectApi: navTestState.unfavoriteProjectApi,
}))

vi.mock('./nav-main-button', () => ({
  NavMainButton: ({ route }: { route: { routeName: string } }) => <div>{route.routeName}</div>,
}))

vi.mock('./nav-breadcrumb-button', () => ({
  BreadcrumbButton: ({ onClick }: { onClick?: () => void }) => (
    <button type="button" onClick={onClick}>
      Toggle
    </button>
  ),
}))

vi.mock('@/components/ui/button/button', () => ({
  Button: ({ children, ...props }: React.ComponentProps<'button'>) => <button {...props}>{children}</button>,
}))

vi.mock('@/components/ui/collapsible/collapsible', () => ({
  Collapsible: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CollapsibleContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CollapsibleTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/context-menu/context-menu', () => ({
  ContextMenu: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  ContextMenuContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  ContextMenuItem: ({ children, onClick, disabled, className }: React.ComponentProps<'button'>) => (
    <button type="button" className={className} onClick={onClick} disabled={disabled}>
      {children}
    </button>
  ),
  ContextMenuSeparator: () => <div />,
  ContextMenuShortcut: ({ children }: { children: React.ReactNode }) => <span>{children}</span>,
  ContextMenuTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/dropdown-menu/dropdown-menu', () => ({
  DropdownMenu: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DropdownMenuContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DropdownMenuItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DropdownMenuTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/dialog/dialog', () => ({
  Dialog: ({ open, children }: { open?: boolean; children: React.ReactNode }) => (open ? <div>{children}</div> : null),
  DialogContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogDescription: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogFooter: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/input/input', () => ({
  Input: (props: React.ComponentProps<'input'>) => <input {...props} />,
}))

vi.mock('@/components/ui/label/label', () => ({
  Label: ({ children, ...props }: React.ComponentProps<'label'>) => <label {...props}>{children}</label>,
}))

vi.mock('@/components/ui/select/select', () => ({
  Select: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectValue: ({ placeholder }: { placeholder?: string }) => <span>{placeholder}</span>,
}))

vi.mock('@/components/ui/alert-dialog/alert-dialog', () => ({
  AlertDialog: ({ open, children }: { open?: boolean; children: React.ReactNode }) => (open ? <div>{children}</div> : null),
  AlertDialogAction: ({ children, onClick, disabled }: React.ComponentProps<'button'>) => (
    <button type="button" onClick={onClick} disabled={disabled}>
      {children}
    </button>
  ),
  AlertDialogCancel: ({ children, onClick, disabled }: React.ComponentProps<'button'>) => (
    <button type="button" onClick={onClick} disabled={disabled}>
      {children}
    </button>
  ),
  AlertDialogContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogDescription: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogFooter: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/command/command', () => ({
  CommandDialog: ({ open, children }: { open?: boolean; children: React.ReactNode }) => (open ? <div>{children}</div> : null),
  CommandEmpty: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CommandGroup: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CommandInput: (props: React.ComponentProps<'input'>) => <input {...props} />,
  CommandItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CommandList: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CommandSeparator: () => <div />,
  CommandShortcut: ({ children }: { children: React.ReactNode }) => <span>{children}</span>,
}))

vi.mock('@/components/ui/switch/switch', () => ({
  Switch: ({ checked, onCheckedChange, disabled }: { checked?: boolean; onCheckedChange?: (checked: boolean) => void; disabled?: boolean }) => (
    <button type="button" disabled={disabled} onClick={() => onCheckedChange?.(!checked)}>
      {checked ? 'on' : 'off'}
    </button>
  ),
}))

describe('Nav delete behavior', () => {
  beforeEach(() => {
    navTestState.navigate.mockReset()
    navTestState.toast.mockReset()
    navTestState.deleteProjectApi.mockReset()
    navTestState.archiveProjectApi.mockReset()
    navTestState.createProjectApi.mockReset()
    navTestState.updateProjectApi.mockReset()
    navTestState.favoriteProjectApi.mockReset()
    navTestState.unfavoriteProjectApi.mockReset()
    navTestState.location.pathname = '/app/inbox'
    navTestState.deleteProjectApi.mockResolvedValue(undefined)
    navTestState.createProjectApi.mockResolvedValue('project-created-1')
  })

  it('does not show a success toast after deleting a project', async () => {
    const setProjects = vi.fn()

    render(
      <Nav
        projects={[
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
        ]}
        setProjects={setProjects}
        refetchProjects={vi.fn()}
      />,
    )

    fireEvent.click(screen.getByRole('button', { name: /删除项目/ }))
    fireEvent.click(screen.getByRole('button', { name: '删除' }))

    await waitFor(() => expect(navTestState.deleteProjectApi).toHaveBeenCalledWith('project-1'))
    expect(navTestState.toast).not.toHaveBeenCalled()
  })

  it('does not render a zero note-count badge for projects', () => {
    render(
      <Nav
        projects={[
          {
            projectId: 'inbox-1',
            name: 'Inbox',
            color: '#000000',
            noteCount: 12,
            isFavorite: 0,
            inboxProject: true,
          },
          {
            projectId: 'project-0',
            name: 'Project Zero',
            color: '#111111',
            noteCount: 0,
            isFavorite: 0,
            inboxProject: false,
          },
        ]}
        setProjects={vi.fn()}
        refetchProjects={vi.fn()}
      />,
    )

    expect(screen.getByText('Project Zero')).toBeInTheDocument()
    expect(screen.queryByText(/^0$/)).not.toBeInTheDocument()
  })

  it('creates a project through the shared project dialog', async () => {
    const setProjects = vi.fn()

    render(
      <Nav
        projects={[
          {
            projectId: 'inbox-1',
            name: 'Inbox',
            color: '#000000',
            noteCount: 12,
            isFavorite: 0,
            inboxProject: true,
          },
        ]}
        setProjects={setProjects}
        refetchProjects={vi.fn()}
      />,
    )

    fireEvent.click(screen.getByRole('button', { name: 'New project' }))
    fireEvent.change(screen.getByRole('textbox', { name: '名称' }), { target: { value: 'Shared Dialog Project' } })
    fireEvent.click(screen.getByRole('button', { name: '创建项目' }))

    await waitFor(() => {
      expect(navTestState.createProjectApi).toHaveBeenCalledWith({
        name: 'Shared Dialog Project',
        color: 'berry_red',
        isFavorite: 0,
        noteCount: 0,
      })
    })

    expect(setProjects).toHaveBeenCalled()
  })
})
