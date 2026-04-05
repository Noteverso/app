import type { ComponentProps } from 'react'
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { buildEditorContentWithQuickBindings, SharedNotesPage } from './shared-notes-page'

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

const INBOX_TARGET_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Back to inbox note ' },
        {
          type: 'quickActionToken',
          attrs: {
            tokenId: 'inbox-token',
            tokenType: 'project',
            entityId: 'inbox-1',
            label: 'Inbox',
          },
        },
      ],
    },
  ],
}

const LOWERCASE_PROJECT_ATTR_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Live editor project ' },
        {
          type: 'quickActionToken',
          attrs: {
            tokenid: 'project-token-lowercase',
            tokentype: 'project',
            entityid: 'project-lowercase-1',
            label: 'Lowercase Project',
          },
        },
      ],
    },
  ],
}

const NUMERIC_PROJECT_ATTR_CONTENT = {
  type: 'doc',
  content: [
    {
      type: 'paragraph',
      content: [
        { type: 'text', text: 'Numeric runtime project ' },
        {
          type: 'quickActionToken',
          attrs: {
            tokenId: 'project-token-numeric',
            tokenType: 'project',
            entityId: 315011398767874050,
            label: 'Numeric Runtime Project',
          },
        },
      ],
    },
  ],
}

const sharedTestState = vi.hoisted(() => {
  const initialOutletProjects = [
    {
      projectId: 'project-1',
      name: 'Project One',
      color: '#111111',
      noteCount: 3,
      isFavorite: 0,
      inboxProject: false,
    },
    {
      projectId: 'project-2',
      name: 'Project Two',
      color: '#222222',
      noteCount: 5,
      isFavorite: 0,
      inboxProject: false,
    },
  ]
  const initialInboxProject = {
    projectId: 'inbox-1',
    name: 'Inbox',
    color: '#000000',
    noteCount: 12,
    isFavorite: 0,
    inboxProject: true,
  }
  const state = {
    fetcher: {
      state: 'idle',
      data: undefined as unknown,
      formData: undefined as FormData | undefined,
    },
    location: {
      pathname: '/app/inbox',
    },
    params: {
      projectId: undefined as string | undefined,
    },
    navigate: vi.fn(),
    refetchProjects: vi.fn(),
    onToggleSidebar: vi.fn(),
    dismissToast: vi.fn(),
    updateToast: vi.fn(),
    getLabelSelectItemsApi: vi.fn(),
    getNotesApi: vi.fn(),
    createProjectApi: vi.fn(),
    updateNoteApi: vi.fn(),
    moveNoteToTrashApi: vi.fn(),
    editor: {
      reset: vi.fn(),
      setContentJson: vi.fn(),
      consumeQuickActionToken: vi.fn(),
      insertQuickActionToken: vi.fn(),
      replaceQuickActionToken: vi.fn(),
    },
    outletContext: {
      projects: initialOutletProjects.map(project => ({ ...project })),
      inboxProject: { ...initialInboxProject },
      refetchProjects: vi.fn(),
      upsertProject: vi.fn(),
      isSidebarVisible: true,
      onToggleSidebar: vi.fn(),
    },
  }

  state.outletContext.upsertProject = vi.fn((project) => {
    state.outletContext.projects = [
      ...state.outletContext.projects.filter(existingProject => existingProject.projectId !== project.projectId),
      project,
    ]
  })

  return {
    ...state,
    initialOutletProjects,
    initialInboxProject,
    upsertProject: state.outletContext.upsertProject,
    toast: vi.fn((..._args: unknown[]) => ({
      id: 'toast-1',
      dismiss: state.dismissToast,
      update: state.updateToast,
    })),
  }
})

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    useFetcher: () => ({
      ...sharedTestState.fetcher,
      Form: ({ children, ...props }: ComponentProps<'form'>) => <form {...props}>{children}</form>,
    }),
    useLocation: () => sharedTestState.location,
    useNavigate: () => sharedTestState.navigate,
    useOutletContext: () => sharedTestState.outletContext,
    useParams: () => sharedTestState.params,
  }
})

vi.mock('@/features/editor', async () => {
  const React = await import('react')
  const MockTextEditor = React.forwardRef((props: any, ref) => {
    React.useImperativeHandle(ref, () => ({
      reset: sharedTestState.editor.reset,
      setContentJson: sharedTestState.editor.setContentJson,
      consumeQuickActionToken: sharedTestState.editor.consumeQuickActionToken,
      insertQuickActionToken: sharedTestState.editor.insertQuickActionToken,
      replaceQuickActionToken: sharedTestState.editor.replaceQuickActionToken,
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
        <button type="button" onClick={() => props.onChange?.(INBOX_TARGET_CONTENT, true)}>
          Set inbox-target content
        </button>
        <button type="button" onClick={() => props.onChange?.(LOWERCASE_PROJECT_ATTR_CONTENT, true)}>
          Set lowercase project token content
        </button>
        <button type="button" onClick={() => props.onChange?.(NUMERIC_PROJECT_ATTR_CONTENT, true)}>
          Set numeric project token content
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
        <button
          type="button"
          onClick={() => props.onQuickActionQuery?.({
            type: 'project',
            keyword: 'Inline Created Project',
            token: '#Inline Created Project',
          })}
        >
          Open inline project create
        </button>
        {props.footer}
      </div>
    )
  })

  MockTextEditor.displayName = 'MockTextEditor'

  return { TextEditor: MockTextEditor }
})

vi.mock('@/features/note', () => ({
  NoteList: ({
    notes,
    onEdit,
    onDelete,
  }: {
    notes: Array<{ noteId: string }>
    onEdit?: (note: { noteId: string }) => void
    onDelete?: (note: { noteId: string }) => void
  }) => (
    <div data-testid="note-list">
      {notes.map(note => (
        <div key={note.noteId}>
          <span>{note.noteId}</span>
          <button type="button" onClick={() => onEdit?.(note)} aria-label={`edit-${note.noteId}`}>Edit {note.noteId}</button>
          <button type="button" onClick={() => onDelete?.(note)} aria-label={`delete-${note.noteId}`}>Delete {note.noteId}</button>
        </div>
      ))}
    </div>
  ),
}))

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: sharedTestState.toast,
    dismiss: sharedTestState.dismissToast,
    toasts: [],
  }),
}))

vi.mock('@/api/label/label', () => ({
  createLabelApi: vi.fn(),
  getLabelSelectItemsApi: sharedTestState.getLabelSelectItemsApi,
}))

vi.mock('@/api/project/project', () => ({
  createProjectApi: sharedTestState.createProjectApi,
}))

vi.mock('@/api/note/note', () => ({
  getNotesApi: sharedTestState.getNotesApi,
  updateNoteApi: sharedTestState.updateNoteApi,
  moveNoteToTrashApi: sharedTestState.moveNoteToTrashApi,
}))

function createInitialNotePage() {
  return {
    pageIndex: 1,
    pageSize: 10,
    total: 1,
    records: [
      {
        noteId: 'existing-note',
        contentJson: PLAIN_CONTENT,
        labels: [],
        project: { projectId: 'inbox-1', name: 'Inbox' },
        addedAt: '2026-01-01T00:00:00.000Z',
        updatedAt: '2026-01-01T00:00:00.000Z',
        isArchived: 0 as const,
        isDeleted: 0 as const,
        isPinned: 0 as const,
        attachmentCount: null,
        referencedCount: null,
        referencingCount: null,
        creator: 'u1',
      },
    ],
  }
}

function createEditBoundNotePage() {
  return {
    pageIndex: 1,
    pageSize: 10,
    total: 1,
    records: [
      {
        noteId: 'existing-note',
        contentJson: PLAIN_CONTENT,
        labels: [
          { labelId: 'label-1', name: 'Label One' },
        ],
        project: { projectId: 'project-2', name: 'Project Two' },
        addedAt: '2026-01-01T00:00:00.000Z',
        updatedAt: '2026-01-01T00:00:00.000Z',
        isArchived: 0 as const,
        isDeleted: 0 as const,
        isPinned: 0 as const,
        attachmentCount: null,
        referencedCount: null,
        referencingCount: null,
        creator: 'u1',
      },
    ],
  }
}

function createNote(noteId: string, projectId = 'inbox-1', projectName = 'Inbox') {
  return {
    noteId,
    contentJson: PLAIN_CONTENT,
    labels: [],
    project: { projectId, name: projectName },
    addedAt: '2026-01-01T00:00:00.000Z',
    updatedAt: '2026-01-01T00:00:00.000Z',
    isArchived: 0 as const,
    isDeleted: 0 as const,
    isPinned: 0 as const,
    attachmentCount: null,
    referencedCount: null,
    referencingCount: null,
    creator: 'u1',
  }
}

function getHiddenInput(container: HTMLElement, name: string) {
  return container.querySelector(`input[name="${name}"]`) as HTMLInputElement
}

function buildSubmitFormData(container: HTMLElement) {
  const formData = new FormData()
  formData.set('contentJson', getHiddenInput(container, 'contentJson').value)
  formData.set('labels', getHiddenInput(container, 'labels').value)
  formData.set('projectId', getHiddenInput(container, 'projectId').value)
  return formData
}

function setFetcherSubmitting(view: ReturnType<typeof render>, submitFormData: FormData, title = 'Inbox') {
  sharedTestState.fetcher.state = 'submitting'
  sharedTestState.fetcher.formData = submitFormData
  view.rerender(<SharedNotesPage title={title} initialNotePage={createInitialNotePage()} />)
}

function setFetcherLoading(view: ReturnType<typeof render>, title = 'Inbox') {
  sharedTestState.fetcher.state = 'loading'
  view.rerender(<SharedNotesPage title={title} initialNotePage={createInitialNotePage()} />)
}

function setFetcherIdleSuccess(
  view: ReturnType<typeof render>,
  noteId = 'new-note-1',
  title = 'Inbox',
  initialNotePage = createInitialNotePage(),
) {
  sharedTestState.fetcher.state = 'idle'
  sharedTestState.fetcher.data = { ok: true, note: noteId }
  view.rerender(<SharedNotesPage title={title} initialNotePage={initialNotePage} />)
}

describe('SharedNotesPage component regressions', () => {
  beforeEach(() => {
    sharedTestState.fetcher.state = 'idle'
    sharedTestState.fetcher.data = undefined
    sharedTestState.fetcher.formData = undefined
    sharedTestState.location.pathname = '/app/inbox'
    sharedTestState.params.projectId = undefined
    sharedTestState.navigate.mockReset()
    sharedTestState.refetchProjects.mockReset()
    sharedTestState.onToggleSidebar.mockReset()
    sharedTestState.dismissToast.mockReset()
    sharedTestState.updateToast.mockReset()
    sharedTestState.toast.mockClear()
    sharedTestState.editor.reset.mockReset()
    sharedTestState.editor.setContentJson.mockReset()
    sharedTestState.editor.consumeQuickActionToken.mockReset()
    sharedTestState.editor.insertQuickActionToken.mockReset()
    sharedTestState.editor.replaceQuickActionToken.mockReset()
    sharedTestState.upsertProject.mockClear()
    sharedTestState.outletContext.projects = sharedTestState.initialOutletProjects.map(project => ({ ...project }))
    sharedTestState.outletContext.inboxProject = { ...sharedTestState.initialInboxProject }
    sharedTestState.outletContext.refetchProjects = sharedTestState.refetchProjects
    sharedTestState.outletContext.upsertProject = sharedTestState.upsertProject
    sharedTestState.outletContext.onToggleSidebar = sharedTestState.onToggleSidebar
    sharedTestState.outletContext.isSidebarVisible = true
    sharedTestState.getLabelSelectItemsApi.mockResolvedValue({
      ok: true,
      data: [
        { value: 'label-1', name: 'Label One', color: '#ff0000' },
      ],
    })
    sharedTestState.getNotesApi.mockResolvedValue({
      ok: true,
      data: createInitialNotePage(),
    })
    sharedTestState.createProjectApi.mockReset()
    sharedTestState.createProjectApi.mockResolvedValue('project-inline-1')
    sharedTestState.updateNoteApi.mockReset()
    sharedTestState.updateNoteApi.mockResolvedValue({ ok: true })
    sharedTestState.moveNoteToTrashApi.mockReset()
    sharedTestState.moveNoteToTrashApi.mockResolvedValue({ ok: true })
  })

  it('clears the composer immediately when create starts on the current route project', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))

    expect(getHiddenInput(view.container, 'contentJson').value).toContain('Plain note body')
    expect(screen.getByRole('button', { name: '保存' })).toBeEnabled()

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)

    expect(sharedTestState.editor.reset).toHaveBeenCalledTimes(1)
    expect(getHiddenInput(view.container, 'contentJson').value).toBe('{}')
    expect(getHiddenInput(view.container, 'projectId').value).toBe('inbox-1')
    expect(getHiddenInput(view.container, 'labels').value).toBe('[]')
    expect(screen.getByRole('button', { name: '保存' })).toBeDisabled()
  })

  it('blocks repeated submit while the create request is still loading', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))

    sharedTestState.fetcher.state = 'loading'
    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    const saveButton = screen.getByRole('button', { name: '保存' })
    expect(saveButton).toBeDisabled()

    const form = saveButton.closest('form')
    expect(form).not.toBeNull()

    const submitEvent = new Event('submit', { bubbles: true, cancelable: true })
    fireEvent(form as HTMLFormElement, submitEvent)

    expect(submitEvent.defaultPrevented).toBe(true)
  })

  it('uses the current route project as the submitted fallback when no project token is selected', async () => {
    sharedTestState.location.pathname = '/app/projects/project-1'
    sharedTestState.params.projectId = 'project-1'

    const view = render(<SharedNotesPage initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))

    expect(getHiddenInput(view.container, 'projectId').value).toBe('project-1')
    expect(getHiddenInput(view.container, 'contentJson').value).toContain('Plain note body')
  })

  it('uses lowercase quick-action project attrs from the live editor as the submitted project id', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set lowercase project token content' }))

    expect(getHiddenInput(view.container, 'projectId').value).toBe('project-lowercase-1')
    expect(getHiddenInput(view.container, 'contentJson').value).not.toContain('Lowercase Project')
  })

  it('uses numeric quick-action project ids from the live editor as the submitted project id', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set numeric project token content' }))

    expect(getHiddenInput(view.container, 'projectId').value).toBe('315011398767874050')
    expect(getHiddenInput(view.container, 'contentJson').value).not.toContain('Numeric Runtime Project')
  })

  it('restores the exact draft project and labels when create fails after an immediate clear', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const submitFormData = buildSubmitFormData(view.container)
    expect(submitFormData.get('projectId')).toBe('project-2')
    expect(submitFormData.get('labels')).toBe('["label-1"]')

    sharedTestState.fetcher.state = 'submitting'
    sharedTestState.fetcher.formData = submitFormData
    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    expect(sharedTestState.editor.reset).toHaveBeenCalledTimes(1)
    expect(getHiddenInput(view.container, 'projectId').value).toBe('inbox-1')
    expect(getHiddenInput(view.container, 'labels').value).toBe('[]')

    sharedTestState.fetcher.state = 'idle'
    sharedTestState.fetcher.data = undefined
    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    expect(sharedTestState.editor.setContentJson).toHaveBeenCalledWith(CROSS_PROJECT_CONTENT)
    expect(getHiddenInput(view.container, 'projectId').value).toBe('project-2')
    expect(getHiddenInput(view.container, 'labels').value).toBe('["label-1"]')
    expect(screen.getByRole('button', { name: '保存' })).toBeEnabled()
  })

  it('reconciles same-project optimistic notes exactly once and does not show a cross-project toast', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)

    expect(screen.getByTestId('note-list')).toHaveTextContent('existing-note')
    expect(screen.getByTestId('note-list').querySelectorAll('span')).toHaveLength(2)

    setFetcherLoading(view)
    setFetcherIdleSuccess(view, 'saved-note-1')

    const noteIds = Array.from(screen.getByTestId('note-list').querySelectorAll('span')).map(node => node.textContent)
    expect(noteIds).toHaveLength(2)
    expect(noteIds).toEqual(expect.arrayContaining(['saved-note-1', 'existing-note']))
    expect(sharedTestState.toast).not.toHaveBeenCalled()
    expect(sharedTestState.refetchProjects).toHaveBeenCalledTimes(1)
  })

  it('keeps the current list unchanged for cross-project success and only shows the global toast', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)
    setFetcherLoading(view)
    setFetcherIdleSuccess(view, 'saved-note-2')

    expect(screen.getByTestId('note-list').querySelectorAll('span')).toHaveLength(1)
    expect(screen.getByTestId('note-list')).toHaveTextContent('existing-note')
    expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      title: 'Note created in Project Two',
    }))
  })

  it('shows a global toast, navigates, and refetches projects once after a successful cross-project create', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)
    setFetcherLoading(view)
    setFetcherIdleSuccess(view)

    expect(sharedTestState.toast).toHaveBeenCalledTimes(1)
    expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      duration: 10000,
      className: 'w-auto max-w-[24rem] items-center gap-2 border-slate-900 bg-slate-900 p-3 pr-9 text-white shadow-lg',
      contentClassName: 'min-w-0 flex-1',
      titleClassName: 'truncate whitespace-nowrap text-xs font-medium leading-none text-white',
      closeClassName: 'text-white/60 hover:text-white focus:text-white focus:ring-white/30 focus:ring-offset-slate-900',
      title: 'Note created in Project Two',
    }))
    expect(sharedTestState.refetchProjects).toHaveBeenCalledTimes(1)

    const firstToastCall = sharedTestState.toast.mock.calls.at(0)
    const toastArgs = firstToastCall?.[0] as {
      duration: number;
      className: string;
      contentClassName: string;
      titleClassName: string;
      closeClassName: string;
      description?: string;
      action: { props: { className?: string; onClick: () => void } };
    } | undefined
    expect(toastArgs).toBeDefined()
    expect(toastArgs?.duration).toBe(10000)
    expect(toastArgs?.description).toBeUndefined()
    expect(toastArgs?.className).toBe('w-auto max-w-[24rem] items-center gap-2 border-slate-900 bg-slate-900 p-3 pr-9 text-white shadow-lg')
    expect(toastArgs?.contentClassName).toBe('min-w-0 flex-1')
    expect(toastArgs?.titleClassName).toBe('truncate whitespace-nowrap text-xs font-medium leading-none text-white')
    expect(toastArgs?.closeClassName).toBe('text-white/60 hover:text-white focus:text-white focus:ring-white/30 focus:ring-offset-slate-900')
    expect(toastArgs?.action.props.className).toBe('h-7 shrink-0 border-white/15 px-2 text-xs text-white hover:bg-white/10 hover:text-white focus:ring-white/30 focus:ring-offset-slate-900')
    await act(async () => {
      toastArgs?.action.props.onClick()
    })

    expect(sharedTestState.dismissToast).toHaveBeenCalledTimes(1)
    expect(sharedTestState.navigate).toHaveBeenCalledWith('/app/projects/project-2')

    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)
    expect(sharedTestState.refetchProjects).toHaveBeenCalledTimes(1)
  })

  it('keeps the previously loaded list items across same-route loader refresh without duplication', async () => {
    const initialNotePage = {
      ...createInitialNotePage(),
      total: 3,
      records: [
        createNote('existing-note'),
        createNote('loaded-page-two-note'),
      ],
    }
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={initialNotePage} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())

    const refreshedPageOne = {
      ...createInitialNotePage(),
      total: 3,
      records: [
        createNote('refreshed-page-one-note'),
        createNote('existing-note'),
      ],
    }

    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={refreshedPageOne} />)

    const noteIds = Array.from(screen.getByTestId('note-list').querySelectorAll('span')).map(node => node.textContent)
    expect(noteIds).toEqual(['refreshed-page-one-note', 'existing-note', 'loaded-page-two-note'])
  })

  it('dismisses the previous navigation toast when the next submit starts', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const crossProjectFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, crossProjectFormData)
    setFetcherIdleSuccess(view, 'saved-note-3')

    sharedTestState.dismissToast.mockClear()
    sharedTestState.fetcher.data = undefined
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    const sameProjectFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, sameProjectFormData)

    expect(sharedTestState.dismissToast).toHaveBeenCalledTimes(1)
  })

  it('creates to inbox from a project route with a toast action targeting /app/inbox', async () => {
    sharedTestState.location.pathname = '/app/projects/project-1'
    sharedTestState.params.projectId = 'project-1'

    const view = render(<SharedNotesPage initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set inbox-target content' }))

    const submitFormData = buildSubmitFormData(view.container)
    expect(submitFormData.get('projectId')).toBe('inbox-1')

    setFetcherSubmitting(view, submitFormData)
    setFetcherIdleSuccess(view, 'saved-note-4')

    const toastArgs = sharedTestState.toast.mock.calls.at(-1)?.[0] as {
      title: string;
      action: { props: { onClick: () => void } };
    } | undefined

    expect(toastArgs?.title).toBe('Note created in 收件箱')
    await act(async () => {
      toastArgs?.action.props.onClick()
    })

    expect(sharedTestState.navigate).toHaveBeenCalledWith('/app/inbox')
  })

  it('uses the real project id and name when the destination project is created inline', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: 'Open inline project create' }))

    const createSuggestion = await screen.findByRole('button', { name: /Inline Created Project/i })
    fireEvent.mouseDown(createSuggestion)

    await waitFor(() => expect(sharedTestState.createProjectApi).toHaveBeenCalledTimes(1))
    expect(sharedTestState.upsertProject).toHaveBeenCalledWith({
      projectId: 'project-inline-1',
      name: 'Inline Created Project',
      color: expect.any(String),
      noteCount: 0,
      isFavorite: 0,
      inboxProject: false,
    })
    expect(getHiddenInput(view.container, 'projectId').value).toBe('project-inline-1')

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)
    setFetcherIdleSuccess(view, 'saved-note-5')

    expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      title: 'Note created in Inline Created Project',
    }))
  })

  it('removes deleted projects from the quick-action dropdown when outlet projects change', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Open project suggestions' }))

    expect(await screen.findByRole('button', { name: 'Project Two' })).toBeInTheDocument()

    sharedTestState.outletContext.projects = sharedTestState.initialOutletProjects
      .filter(project => project.projectId !== 'project-2')
      .map(project => ({ ...project }))
    view.rerender(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => {
      expect(screen.queryByRole('button', { name: 'Project Two' })).not.toBeInTheDocument()
    })
  })

  it('treats label detail as outside the scoped cross-project toast flow', async () => {
    sharedTestState.location.pathname = '/app/labels/label-1'
    sharedTestState.params.projectId = undefined

    const view = render(<SharedNotesPage title="Important" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const submitFormData = buildSubmitFormData(view.container)
    expect(submitFormData.get('projectId')).toBe('project-2')

    setFetcherSubmitting(view, submitFormData, 'Important')
    setFetcherIdleSuccess(view, 'saved-note-6', 'Important')

    expect(sharedTestState.toast).not.toHaveBeenCalled()
    expect(screen.getByTestId('note-list').querySelectorAll('span')).toHaveLength(2)
  })

  it('dismisses an active navigation toast when the route changes', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))

    const submitFormData = buildSubmitFormData(view.container)
    setFetcherSubmitting(view, submitFormData)
    setFetcherIdleSuccess(view)

    sharedTestState.location.pathname = '/app/projects/project-2'
    sharedTestState.params.projectId = 'project-2'
    view.rerender(<SharedNotesPage initialNotePage={createInitialNotePage()} />)

    expect(sharedTestState.dismissToast).toHaveBeenCalledTimes(1)
  })

  it('loads an existing note into the shared editor when edit is clicked', async () => {
    render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))

    expect(sharedTestState.editor.setContentJson).toHaveBeenCalledWith(expect.objectContaining({ type: 'doc' }))
    expect(screen.getByText('Editing note')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: '更新' })).toBeInTheDocument()
  })

  it('enters edit mode when the note action menu edit callback is triggered', async () => {
    render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))

    expect(screen.getByText('Editing note')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: '更新' })).toBeInTheDocument()
  })

  it('restores the edited note project and labels as quick-action bindings when edit starts', async () => {
    const view = render(<SharedNotesPage title="Inbox" initialNotePage={createEditBoundNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))

    const editContent = sharedTestState.editor.setContentJson.mock.calls.at(-1)?.[0] as {
      type: string
      content: Array<{ type: string; content: Array<{ type: string; text?: string; attrs?: { tokenType?: string; entityId?: string; label?: string } }> }>
    }

    expect(editContent.type).toBe('doc')
    expect(editContent.content[0]).toEqual(expect.objectContaining({
      type: 'paragraph',
    }))
    expect(editContent.content[0].content.slice(0, 3)).toEqual([
      expect.objectContaining({
        type: 'quickActionToken',
        attrs: expect.objectContaining({ tokenType: 'project', entityId: 'project-2', label: 'Project Two' }),
      }),
      expect.objectContaining({
        type: 'quickActionToken',
        attrs: expect.objectContaining({ tokenType: 'label', entityId: 'label-1', label: 'Label One' }),
      }),
      expect.objectContaining({ type: 'text', text: 'Plain note body' }),
    ])
    expect(getHiddenInput(view.container, 'projectId').value).toBe('project-2')
    expect(getHiddenInput(view.container, 'labels').value).toBe('["label-1"]')
    expect(screen.getByText('Editing note')).toBeInTheDocument()
  })

  it('builds edit content safely when note labels are missing', () => {
    const content = buildEditorContentWithQuickBindings({
      contentJson: PLAIN_CONTENT,
      project: { projectId: 'inbox-1', name: 'Inbox' },
      labels: undefined as never,
    })

    expect(content).toEqual(expect.objectContaining({ type: 'doc' }))
  })

  it('removes the edited note from the current scoped list when it is moved to another project', async () => {
    render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))
    fireEvent.click(screen.getByRole('button', { name: 'Set cross-project content' }))
    fireEvent.click(screen.getByRole('button', { name: '更新' }))

    await waitFor(() => expect(sharedTestState.updateNoteApi).toHaveBeenCalledWith('existing-note', expect.objectContaining({
      projectId: 'project-2',
      labels: ['label-1'],
      linkedNotes: [],
      files: [],
    })))
    expect(sharedTestState.editor.reset).toHaveBeenCalled()
    expect(screen.queryByText('Editing note')).not.toBeInTheDocument()
    expect(screen.getByTestId('note-list')).not.toHaveTextContent('existing-note')
    expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({ title: 'Note moved to Project Two' }))
  })

  it('keeps the edited note project when the quick-action project token is removed before save', async () => {
    render(<SharedNotesPage title="Inbox" initialNotePage={createEditBoundNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: '更新' }))

    await waitFor(() => expect(sharedTestState.updateNoteApi).toHaveBeenCalledWith('existing-note', expect.objectContaining({
      projectId: 'project-2',
    })))
  })

  it('shows the backend update error message and stays in edit mode when note update fails', async () => {
    sharedTestState.updateNoteApi.mockResolvedValue({
      ok: false,
      status: 404,
      data: {
        error: {
          message: 'Request failed with status code 404',
          payload: {
            error: {
              message: 'Note not found',
            },
          },
        },
      },
    })

    render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('edit-existing-note'))
    fireEvent.click(screen.getByRole('button', { name: 'Set plain content' }))
    fireEvent.click(screen.getByRole('button', { name: '更新' }))

    await waitFor(() => expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({
      title: 'Note not found',
      variant: 'destructive',
    })))
    expect(screen.getByText('Editing note')).toBeInTheDocument()
    expect(screen.getByTestId('note-list')).toHaveTextContent('existing-note')
    expect(sharedTestState.editor.reset).not.toHaveBeenCalled()
  })

  it('moves a note to trash and removes it from the list after delete confirmation', async () => {
    render(<SharedNotesPage title="Inbox" initialNotePage={createInitialNotePage()} />)

    await waitFor(() => expect(sharedTestState.getLabelSelectItemsApi).toHaveBeenCalled())
    fireEvent.click(screen.getByLabelText('delete-existing-note'))

    expect(screen.getByText('删除这条笔记？')).toBeInTheDocument()
    fireEvent.click(screen.getByRole('button', { name: 'Delete' }))

    await waitFor(() => expect(sharedTestState.moveNoteToTrashApi).toHaveBeenCalledWith('existing-note'))
    expect(screen.getByTestId('note-list')).not.toHaveTextContent('existing-note')
    expect(sharedTestState.toast).toHaveBeenCalledWith(expect.objectContaining({ title: 'Note moved to trash' }))
  })
})
