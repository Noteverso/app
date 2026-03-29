import type React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ProjectsManage } from './projects-manage'

const projectsManageTestState = vi.hoisted(() => ({
  toast: vi.fn(),
  createProjectApi: vi.fn(),
  outletContext: {
    projects: [] as Array<{
      projectId: string;
      name: string;
      color: string;
      noteCount: number;
      isFavorite: 0 | 1;
      inboxProject: boolean;
    }>,
    inboxProject: {
      projectId: 'inbox-1',
      name: 'Inbox',
      color: 'grey',
      noteCount: 4,
      isFavorite: 0 as const,
      inboxProject: true,
    },
    refetchProjects: vi.fn(),
    upsertProject: vi.fn(),
  },
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    Link: ({
      children,
      to,
      ...props
    }: React.ComponentProps<'a'> & { to: string }) => (
      <a href={to} {...props}>
        {children}
      </a>
    ),
    useOutletContext: () => projectsManageTestState.outletContext,
  }
})

vi.mock('@/api/project/project', () => ({
  createProjectApi: projectsManageTestState.createProjectApi,
}))

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: projectsManageTestState.toast,
    dismiss: vi.fn(),
    toasts: [],
  }),
}))

vi.mock('@/components/ui/button/button', () => ({
  Button: ({ children, ...props }: React.ComponentProps<'button'>) => <button {...props}>{children}</button>,
}))

vi.mock('@/components/ui/card/card', () => ({
  Card: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  CardContent: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  CardDescription: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  CardHeader: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  CardTitle: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
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
  SelectTrigger: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  SelectValue: ({ placeholder }: { placeholder?: string }) => <span>{placeholder}</span>,
}))

vi.mock('@/components/ui/switch/switch', () => ({
  Switch: ({ checked, onCheckedChange, disabled }: { checked?: boolean; onCheckedChange?: (checked: boolean) => void; disabled?: boolean }) => (
    <button type="button" disabled={disabled} onClick={() => onCheckedChange?.(!checked)}>
      {checked ? 'on' : 'off'}
    </button>
  ),
}))

describe('ProjectsManage', () => {
  beforeEach(() => {
    projectsManageTestState.toast.mockReset()
    projectsManageTestState.createProjectApi.mockReset()
    projectsManageTestState.createProjectApi.mockResolvedValue('project-3')
    projectsManageTestState.outletContext.refetchProjects.mockReset()
    projectsManageTestState.outletContext.upsertProject.mockReset()
    projectsManageTestState.outletContext.projects = [
      {
        projectId: 'project-1',
        name: 'Project One',
        color: 'blue',
        noteCount: 3,
        isFavorite: 1,
        inboxProject: false,
      },
      {
        projectId: 'project-2',
        name: 'Project Two',
        color: 'green',
        noteCount: 0,
        isFavorite: 0,
        inboxProject: false,
      },
    ]
  })

  it('renders the grid view by default', () => {
    render(<ProjectsManage />)

    expect(screen.getAllByTestId('project-card')).toHaveLength(2)
    expect(screen.queryByTestId('project-list-item')).not.toBeInTheDocument()
  })

  it('switches to the list view', () => {
    render(<ProjectsManage />)

    fireEvent.click(screen.getByRole('button', { name: /列表/ }))

    expect(screen.getAllByTestId('project-list-item')).toHaveLength(2)
    expect(screen.queryByTestId('project-card')).not.toBeInTheDocument()
  })

  it('filters to favorited projects and revalidates data', () => {
    render(<ProjectsManage />)

    fireEvent.click(screen.getByTestId('projects-filter-favorited'))

    expect(screen.getAllByTestId('project-card')).toHaveLength(1)
    expect(screen.getByText('Project One')).toBeInTheDocument()
    expect(screen.queryByText('Project Two')).not.toBeInTheDocument()
    expect(projectsManageTestState.outletContext.refetchProjects).toHaveBeenCalledTimes(1)
  })

  it('restores all projects after returning to the all filter', () => {
    render(<ProjectsManage />)

    fireEvent.click(screen.getByTestId('projects-filter-favorited'))
    fireEvent.click(screen.getByTestId('projects-filter-all'))

    expect(screen.getAllByTestId('project-card')).toHaveLength(2)
    expect(projectsManageTestState.outletContext.refetchProjects).toHaveBeenCalledTimes(2)
  })

  it('shows the archived empty state and revalidates data', () => {
    render(<ProjectsManage />)

    fireEvent.click(screen.getByTestId('projects-filter-archived'))

    expect(screen.getByText('暂无归档项目')).toBeInTheDocument()
    expect(projectsManageTestState.outletContext.refetchProjects).toHaveBeenCalledTimes(1)
  })

  it('creates a project from the page header dialog', async () => {
    render(<ProjectsManage />)

    fireEvent.click(screen.getByTestId('projects-page-create-project-btn'))
    fireEvent.change(screen.getByRole('textbox', { name: '名称' }), { target: { value: 'Page Project' } })
    fireEvent.click(screen.getByRole('button', { name: '创建项目' }))

    await waitFor(() => {
      expect(projectsManageTestState.outletContext.upsertProject).toHaveBeenCalledWith(expect.objectContaining({
        name: 'Page Project',
        color: 'berry_red',
        isFavorite: 0,
        noteCount: 0,
        inboxProject: false,
      }))
    })

    expect(projectsManageTestState.createProjectApi).toHaveBeenCalledWith({
      name: 'Page Project',
      color: 'berry_red',
      isFavorite: 0,
      noteCount: 0,
    })

    await waitFor(() => {
      expect(projectsManageTestState.outletContext.refetchProjects).toHaveBeenCalledTimes(1)
    })
  })
})
