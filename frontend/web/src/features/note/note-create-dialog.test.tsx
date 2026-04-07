import type { ComponentProps } from 'react'
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { NoteCreateDialog } from './note-create-dialog'

const PLAIN_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Plain note body' },
      ],
    },
  ],
}

const CROSS_PROJECT_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Cross project note ' },
        {
          type: 'quickActionToken',
          attrs: {
            tokenId: 'project-token',
            tokenType: 'project',
            entityId: 'project-2',
            label: 'Project Two',
          },
        },
        {
          type: 'quickActionToken',
          attrs: {
            tokenId: 'label-token',
            tokenType: 'label',
            entityId: 'label-1',
            label: 'Label One',
          },
        },
      ],
    },
  ],
}

const SANITIZED_CROSS_PROJECT_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Cross project note ' },
      ],
    },
  ],
}

const dialogTestState = vi.hoisted(() => ({
  toast: vi.fn(),
  dismissToast: vi.fn(),
  updateToast: vi.fn(),
  navigate: vi.fn(),
  addNote: vi.fn(),
  getLabelSelectItemsApi: vi.fn(),
  editor: {
    reset: vi.fn(),
    setContentJson: vi.fn(),
    consumeQuickActionToken: vi.fn(),
    insertQuickActionToken: vi.fn(),
    replaceQuickActionToken: vi.fn(),
  },
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    useNavigate: () => dialogTestState.navigate,
  }
})

vi.mock('@/features/editor', async () => {
  const React = await import('react')
  const MockTextEditor = React.forwardRef((props: any, ref) => {
    React.useImperativeHandle(ref, () => ({
      reset: dialogTestState.editor.reset,
      setContentJson: dialogTestState.editor.setContentJson,
      consumeQuickActionToken: dialogTestState.editor.consumeQuickActionToken,
      insertQuickActionToken: dialogTestState.editor.insertQuickActionToken,
      replaceQuickActionToken: dialogTestState.editor.replaceQuickActionToken,
      getCursorAnchor: () => ({ left: 24, top: 24, bottom: 48 }),
    }))

    return (
      <div data-testid="text-editor">
        <button type="button" onClick={() => props.onChange?.(PLAIN_CONTENT, true)}>
          Set plain content
        </button>
        <button type="button" onClick={() => props.onChange?.(CROSS_PROJECT_CONTENT, true)}>
          Set cross-project content
        </button>
        <button
          type="button"
          onClick={() => props.onQuickActionQuery?.({
            type: 'project',
            keyword: 'Project',
            token: '#Project',
          })}
        >
          Open project suggestions
        </button>
      </div>
    )
  })

  MockTextEditor.displayName = 'MockTextEditor'

  return { TextEditor: MockTextEditor }
})

vi.mock('@/components/ui/dialog/dialog', () => ({
  Dialog: ({ open, children }: { open?: boolean; children: React.ReactNode }) => (open ? <div>{children}</div> : null),
  DialogContent: ({ children, className }: { children: React.ReactNode; className?: string }) => <div data-testid="dialog-content" className={className}>{children}</div>,
  DialogDescription: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogFooter: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/button/button', () => ({
  Button: ({ children, ...props }: React.ComponentProps<'button'>) => <button {...props}>{children}</button>,
}))

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: dialogTestState.toast.mockImplementation((..._args: unknown[]) => ({
      id: 'toast-1',
      dismiss: dialogTestState.dismissToast,
      update: dialogTestState.updateToast,
    })),
    dismiss: vi.fn(),
    toasts: [],
  }),
}))

vi.mock('@/api/note/note', () => ({
  addNote: dialogTestState.addNote,
}))

vi.mock('@/api/label/label', () => ({
  createLabelApi: vi.fn(),
  getLabelSelectItemsApi: dialogTestState.getLabelSelectItemsApi,
}))

vi.mock('@/api/project/project', () => ({
  createProjectApi: vi.fn(),
}))

function createProjects() {
  return [
    {
      projectId: 'inbox-1',
      name: 'Inbox',
      color: '#000000',
      noteCount: 12,
      isFavorite: 0 as const,
      inboxProject: true,
    },
    {
      projectId: 'project-1',
      name: 'Project One',
      color: '#111111',
      noteCount: 3,
      isFavorite: 0 as const,
      inboxProject: false,
    },
    {
      projectId: 'project-2',
      name: 'Project Two',
      color: '#222222',
      noteCount: 5,
      isFavorite: 0 as const,
      inboxProject: false,
    },
  ]
}

function renderDialog(overrideProps: Partial<ComponentProps<typeof NoteCreateDialog>> = {}) {
  const projects = createProjects()
  const inboxProject = projects[0]
  const props: ComponentProps<typeof NoteCreateDialog> = {
    open: true,
    onOpenChange: vi.fn(),
    projects,
    inboxProject,
    pathname: '/app/inbox',
    refetchProjects: vi.fn(),
    upsertProject: vi.fn(),
    ...overrideProps,
  }

  return {
    ...render(<NoteCreateDialog {...props} />),
    props,
  }
}

describe('NoteCreateDialog', () => {
  beforeEach(() => {
    dialogTestState.toast.mockReset()
    dialogTestState.dismissToast.mockReset()
    dialogTestState.updateToast.mockReset()
    dialogTestState.navigate.mockReset()
    dialogTestState.addNote.mockReset()
    dialogTestState.getLabelSelectItemsApi.mockReset()
    dialogTestState.editor.reset.mockReset()
    dialogTestState.editor.setContentJson.mockReset()
    dialogTestState.editor.consumeQuickActionToken.mockReset()
    dialogTestState.editor.insertQuickActionToken.mockReset()
    dialogTestState.editor.replaceQuickActionToken.mockReset()

    dialogTestState.addNote.mockResolvedValue({ ok: true, status: 200, data: 'new-note-1' })
    dialogTestState.getLabelSelectItemsApi.mockResolvedValue({ ok: true, status: 200, data: [] })
  })

  it('keeps save disabled while the editor is empty', async () => {
    renderDialog()

    await waitFor(() => expect(dialogTestState.getLabelSelectItemsApi).toHaveBeenCalledTimes(1))
    expect(screen.getByTestId('dialog-content')).toHaveClass('top-[4vh]', 'translate-y-0', 'overflow-visible')
    expect(screen.getByTestId('note-create-dialog-scroll-area')).toHaveClass('max-h-[84vh]', 'overflow-y-auto')
    expect(screen.getByRole('button', { name: '保存' })).toBeDisabled()
  })

  it('renders the quick-action menu as a fixed-height overlay anchored to the cursor', async () => {
    renderDialog()

    fireEvent.click(screen.getByRole('button', { name: 'Open project suggestions' }))

    const quickActionMenu = await screen.findByTestId('note-create-quick-action-menu')
    expect(quickActionMenu).toHaveStyle({
      left: '24px',
      top: '56px',
    })
    expect(quickActionMenu).toHaveStyle({
      height: '256px',
      width: '320px',
    })
    expect(quickActionMenu).toHaveClass('overflow-hidden')
    expect(screen.getByTestId('note-create-quick-action-menu-list')).toHaveClass('overflow-y-auto')
  })

  it('shows the navigation-hint toast for unscoped routes', async () => {
    const refetchProjects = vi.fn()
    const onOpenChange = vi.fn()

    renderDialog({
      pathname: '/app/search',
      refetchProjects,
      onOpenChange,
    })

    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: '保存' }))

    await waitFor(() => {
      expect(dialogTestState.addNote).toHaveBeenCalledWith({
        contentJson: PLAIN_CONTENT,
        projectId: 'inbox-1',
        labels: [],
      })
    })

    expect(refetchProjects).toHaveBeenCalledTimes(1)
    expect(onOpenChange).toHaveBeenCalledWith(false)
    expect(dialogTestState.editor.reset).toHaveBeenCalledTimes(1)
    expect(dialogTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      title: '笔记已创建在 收件箱',
    }))

    const toastArgs = dialogTestState.toast.mock.calls.at(-1)?.[0] as {
      action: { props: { onClick: () => void } };
    } | undefined

    await act(async () => {
      toastArgs?.action.props.onClick()
    })

    expect(dialogTestState.dismissToast).toHaveBeenCalledTimes(1)
    expect(dialogTestState.navigate).toHaveBeenCalledWith('/app/inbox')
  })

  it('does not show a success toast for same-route inbox creates', async () => {
    const refetchProjects = vi.fn()

    renderDialog({
      pathname: '/app/inbox',
      refetchProjects,
    })

    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: '保存' }))

    await waitFor(() => {
      expect(dialogTestState.addNote).toHaveBeenCalledWith({
        contentJson: PLAIN_CONTENT,
        projectId: 'inbox-1',
        labels: [],
      })
    })

    expect(refetchProjects).toHaveBeenCalledTimes(1)
    expect(dialogTestState.toast).not.toHaveBeenCalled()
  })

  it('shows the navigation-hint toast for cross-project creates from inbox', async () => {
    renderDialog()

    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))
    fireEvent.click(screen.getByRole('button', { name: '保存' }))

    await waitFor(() => {
      expect(dialogTestState.addNote).toHaveBeenCalledWith({
        contentJson: SANITIZED_CROSS_PROJECT_CONTENT,
        projectId: 'project-2',
        labels: ['label-1'],
      })
    })

    expect(dialogTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      title: '笔记已创建在 Project Two',
    }))

    const toastArgs = dialogTestState.toast.mock.calls.at(-1)?.[0] as {
      action: { props: { onClick: () => void } };
      title: string;
    } | undefined
    expect(toastArgs?.title).toBe('笔记已创建在 Project Two')

    await act(async () => {
      toastArgs?.action.props.onClick()
    })

    expect(dialogTestState.dismissToast).toHaveBeenCalledTimes(1)
    expect(dialogTestState.navigate).toHaveBeenCalledWith('/app/projects/project-2')
  })

  it('allows quick-action suggestions to be selected by mouse inside the modal', async () => {
    renderDialog()

    fireEvent.click(screen.getByRole('button', { name: 'Open project suggestions' }))
    await screen.findByTestId('note-create-quick-action-menu')

    fireEvent.click(screen.getByRole('button', { name: /Project One/ }))

    await waitFor(() => {
      expect(dialogTestState.editor.consumeQuickActionToken).toHaveBeenCalledTimes(1)
    })

    expect(dialogTestState.editor.insertQuickActionToken).toHaveBeenCalledWith(expect.objectContaining({
      tokenType: 'project',
      entityId: 'project-1',
      label: 'Project One',
    }))
  })

  it('shows an error toast and keeps the modal open when note creation fails', async () => {
    const onOpenChange = vi.fn()
    const refetchProjects = vi.fn()
    dialogTestState.addNote.mockResolvedValue({
      ok: false,
      status: 400,
      data: {
        error: {
          message: 'Request failed',
          payload: {
            message: 'Server said no',
          },
        },
      },
    })

    renderDialog({
      pathname: '/app/search',
      refetchProjects,
      onOpenChange,
    })

    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: '保存' }))

    await waitFor(() => {
      expect(dialogTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
        title: 'Server said no',
        variant: 'destructive',
      }))
    })

    expect(screen.getByText('新增笔记')).toBeInTheDocument()
    expect(onOpenChange).not.toHaveBeenCalled()
    expect(refetchProjects).not.toHaveBeenCalled()
  })
})
