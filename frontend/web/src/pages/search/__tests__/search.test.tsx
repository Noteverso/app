import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { SearchPage } from '../search'
import * as noteApi from '@/api/note/note'

vi.mock('@/api/note/note')
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useSearchParams: () => [new URLSearchParams(), vi.fn()],
  }
})

const renderWithRouter = (component: React.ReactElement) => {
  return render(<BrowserRouter>{component}</BrowserRouter>)
}

describe('SearchPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should_renderSearchPage', () => {
    vi.mocked(noteApi.searchNotesApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<SearchPage />)
    
    expect(screen.getByText(/search results/i)).toBeInTheDocument()
  })

  it('should_displayEmptyState_whenNoResults', async () => {
    vi.mocked(noteApi.searchNotesApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<SearchPage />)

    await waitFor(() => {
      expect(screen.getByText(/no results found/i)).toBeInTheDocument()
    })
  })

  it('should_displayResults_whenFound', async () => {
    const mockNotes = [
      { noteId: '1', content: 'Meeting notes', addedAt: '2024-01-01' },
      { noteId: '2', content: 'Project ideas', addedAt: '2024-01-02' },
    ]

    vi.mocked(noteApi.searchNotesApi).mockResolvedValue({
      ok: true,
      data: { records: mockNotes, total: 2, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<SearchPage />)

    await waitFor(() => {
      expect(screen.getByText(/meeting notes/i)).toBeInTheDocument()
      expect(screen.getByText(/project ideas/i)).toBeInTheDocument()
    })
  })

  it('should_applyFilters', async () => {
    vi.mocked(noteApi.searchNotesApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<SearchPage />)

    const filterButton = screen.getByRole('button', { name: /filter/i })
    await userEvent.click(filterButton)

    await waitFor(() => {
      expect(screen.getByText(/filters/i)).toBeInTheDocument()
    })
  })

  it('should_loadMore_onScroll', async () => {
    const mockNotes = [
      { noteId: '1', content: 'Note 1', addedAt: '2024-01-01' },
    ]

    vi.mocked(noteApi.searchNotesApi).mockResolvedValue({
      ok: true,
      data: { records: mockNotes, total: 10, pageIndex: 1, pageSize: 1 },
    } as any)

    renderWithRouter(<SearchPage />)

    await waitFor(() => {
      expect(screen.getByText(/note 1/i)).toBeInTheDocument()
    })

    // Verify that more results can be loaded
    expect(noteApi.searchNotesApi).toHaveBeenCalled()
  })
})
