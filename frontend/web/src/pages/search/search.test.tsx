import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { SearchPage } from './search'

const searchTestState = vi.hoisted(() => ({
  searchParams: new URLSearchParams(),
  setSearchParams: vi.fn(),
  searchNotesApi: vi.fn(),
  toast: vi.fn(),
}))

let intersectionCallback: IntersectionObserverCallback | undefined

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    useSearchParams: () => [searchTestState.searchParams, searchTestState.setSearchParams] as const,
  }
})

vi.mock('@/api/note/note', () => ({
  searchNotesApi: searchTestState.searchNotesApi,
}))

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: searchTestState.toast,
    dismiss: vi.fn(),
    toasts: [],
  }),
}))

vi.mock('@/components/search-bar/search-bar', () => ({
  SearchBar: ({ onSearch }: { onSearch: (params: any) => void }) => (
    <button
      type="button"
      onClick={() => onSearch({ keyword: 'refined', sortBy: 'addedAt', sortOrder: 'desc' })}
    >
      Trigger Search
    </button>
  ),
}))

vi.mock('@/features/note', () => ({
  NoteList: ({ notes, refFunc }: { notes: Array<{ noteId: string }>; refFunc: (node: HTMLElement | null) => void }) => (
    <div data-testid="note-list">
      {notes.map((note) => (
        <div key={note.noteId}>{note.noteId}</div>
      ))}
      {notes.length > 0 ? <div ref={refFunc} data-testid="sentinel" /> : null}
    </div>
  ),
}))

function createNote(noteId: string) {
  return {
    noteId,
    labels: [],
    contentJson: {},
    addedAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z',
    isArchived: 0 as const,
    isDeleted: 0 as const,
    isPinned: 0 as const,
    attachmentCount: 0,
    referencedCount: 0,
    referencingCount: 0,
    creator: 'tester',
    project: {
      projectId: 'project-1',
      name: 'Project One',
    },
  }
}

function createSearchResponse(pageIndex: number, total: number, noteIds: string[]) {
  return {
    ok: true,
    data: {
      pageIndex,
      pageSize: 20,
      total,
      records: noteIds.map(createNote),
    },
  }
}

function createDeferred<T>() {
  let resolve!: (value: T) => void
  const promise = new Promise<T>((nextResolve) => {
    resolve = nextResolve
  })

  return { promise, resolve }
}

async function triggerIntersection() {
  await act(async () => {
    intersectionCallback?.([{ isIntersecting: true } as IntersectionObserverEntry], {} as IntersectionObserver)
  })
}

describe('SearchPage', () => {
  beforeEach(() => {
    searchTestState.searchParams = new URLSearchParams()
    searchTestState.setSearchParams.mockReset()
    searchTestState.setSearchParams.mockImplementation((nextSearchParams: URLSearchParams) => {
      searchTestState.searchParams = new URLSearchParams(nextSearchParams)
    })
    searchTestState.searchNotesApi.mockReset()
    searchTestState.toast.mockReset()
    intersectionCallback = undefined

    class MockIntersectionObserver implements IntersectionObserver {
      readonly root = null
      readonly rootMargin = ''
      readonly thresholds = []

      constructor(callback: IntersectionObserverCallback) {
        intersectionCallback = callback
      }

      disconnect() {}
      observe() {}
      takeRecords() {
        return []
      }
      unobserve() {}
    }

    vi.stubGlobal('IntersectionObserver', MockIntersectionObserver)
  })

  it('loads the initial query from the URL and renders a sticky toolbar', async () => {
    searchTestState.searchParams = new URLSearchParams('keyword=roadmap')
    searchTestState.searchNotesApi.mockResolvedValue(createSearchResponse(1, 1, ['note-1']))

    render(<SearchPage />)

    await waitFor(() => {
      expect(searchTestState.searchNotesApi).toHaveBeenCalledWith({
        keyword: 'roadmap',
        labelIds: undefined,
        status: undefined,
        sortBy: 'addedAt',
        sortOrder: 'desc',
        pageIndex: 1,
        pageSize: 20,
      })
    })

    expect(screen.getByText('note-1')).toBeInTheDocument()
    expect(screen.getByTestId('search-toolbar').className).toContain('sticky')
    expect(screen.getByTestId('search-toolbar').className).toContain('top-0')
  })

  it('appends page 2 results instead of replacing the current list', async () => {
    const nextPageResponse = createDeferred<ReturnType<typeof createSearchResponse>>()

    searchTestState.searchNotesApi
      .mockResolvedValueOnce(createSearchResponse(1, 40, ['note-1', 'note-2']))
      .mockImplementationOnce(() => nextPageResponse.promise)

    render(<SearchPage />)

    await waitFor(() => expect(screen.getByText('note-1')).toBeInTheDocument())
    await triggerIntersection()

    await waitFor(() => expect(searchTestState.searchNotesApi).toHaveBeenCalledTimes(2))

    nextPageResponse.resolve(createSearchResponse(2, 40, ['note-3', 'note-4']))

    await waitFor(() => {
      expect(screen.getByText('note-1')).toBeInTheDocument()
      expect(screen.getByText('note-2')).toBeInTheDocument()
      expect(screen.getByText('note-3')).toBeInTheDocument()
      expect(screen.getByText('note-4')).toBeInTheDocument()
    })
  })

  it('does not issue duplicate next-page requests while pagination is already loading', async () => {
    const nextPageResponse = createDeferred<ReturnType<typeof createSearchResponse>>()

    searchTestState.searchNotesApi
      .mockResolvedValueOnce(createSearchResponse(1, 40, ['note-1', 'note-2']))
      .mockImplementationOnce(() => nextPageResponse.promise)

    render(<SearchPage />)

    await waitFor(() => expect(screen.getByText('note-1')).toBeInTheDocument())

    await act(async () => {
      intersectionCallback?.([{ isIntersecting: true } as IntersectionObserverEntry], {} as IntersectionObserver)
      intersectionCallback?.([{ isIntersecting: true } as IntersectionObserverEntry], {} as IntersectionObserver)
    })

    await waitFor(() => expect(searchTestState.searchNotesApi).toHaveBeenCalledTimes(2))

    nextPageResponse.resolve(createSearchResponse(2, 40, ['note-3', 'note-4']))

    await waitFor(() => expect(screen.getByText('note-4')).toBeInTheDocument())
    expect(searchTestState.searchNotesApi).toHaveBeenCalledTimes(2)
  })

  it('resets to page 1 for an explicit search and preserves scroll-reset prevention on URL updates', async () => {
    searchTestState.searchNotesApi
      .mockResolvedValueOnce(createSearchResponse(1, 40, ['note-1', 'note-2']))
      .mockResolvedValueOnce(createSearchResponse(2, 40, ['note-3', 'note-4']))
      .mockResolvedValueOnce(createSearchResponse(1, 1, ['note-10']))

    render(<SearchPage />)

    await waitFor(() => expect(screen.getByText('note-1')).toBeInTheDocument())
    await triggerIntersection()
    await waitFor(() => expect(screen.getByText('note-4')).toBeInTheDocument())

    fireEvent.click(screen.getByRole('button', { name: 'Trigger Search' }))

    await waitFor(() => {
      expect(searchTestState.searchNotesApi).toHaveBeenLastCalledWith({
        keyword: 'refined',
        labelIds: undefined,
        status: undefined,
        sortBy: 'addedAt',
        sortOrder: 'desc',
        pageIndex: 1,
        pageSize: 20,
      })
    })

    await waitFor(() => {
      expect(screen.queryByText('note-1')).not.toBeInTheDocument()
      expect(screen.getByText('note-10')).toBeInTheDocument()
    })

    const lastSetSearchParamsCall = searchTestState.setSearchParams.mock.lastCall

    expect(lastSetSearchParamsCall).toBeDefined()

    const [nextSearchParams, nextOptions] = lastSetSearchParamsCall!
    expect(nextSearchParams.toString()).toBe('keyword=refined&sortBy=addedAt&sortOrder=desc')
    expect(nextOptions).toEqual({ preventScrollReset: true })
  })
})
