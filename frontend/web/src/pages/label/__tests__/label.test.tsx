import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { LabelPage } from '../label'
import * as labelApi from '@/api/label/label'

vi.mock('@/api/label/label')

const renderWithRouter = (component: React.ReactElement) => {
  return render(<BrowserRouter>{component}</BrowserRouter>)
}

describe('LabelPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should_renderLabelPage', () => {
    vi.mocked(labelApi.getLabelsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)

    renderWithRouter(<LabelPage />)
    
    expect(screen.getByText(/labels/i)).toBeInTheDocument()
  })

  it('should_displayEmptyState_whenNoLabels', async () => {
    vi.mocked(labelApi.getLabelsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)

    renderWithRouter(<LabelPage />)

    await waitFor(() => {
      expect(screen.getByText(/no labels/i)).toBeInTheDocument()
    })
  })

  it('should_displayLabels_whenLoaded', async () => {
    const mockLabels = [
      { labelId: '1', name: 'Work', color: 'blue', noteCount: 5 },
      { labelId: '2', name: 'Personal', color: 'green', noteCount: 3 },
    ]

    vi.mocked(labelApi.getLabelsApi).mockResolvedValue({
      ok: true,
      data: mockLabels,
    } as any)

    renderWithRouter(<LabelPage />)

    await waitFor(() => {
      expect(screen.getByText('Work')).toBeInTheDocument()
      expect(screen.getByText('Personal')).toBeInTheDocument()
    })
  })

  it('should_openCreateDialog', async () => {
    vi.mocked(labelApi.getLabelsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)

    renderWithRouter(<LabelPage />)

    const createButton = screen.getByRole('button', { name: /new label/i })
    await userEvent.click(createButton)

    await waitFor(() => {
      expect(screen.getByText(/create label/i)).toBeInTheDocument()
    })
  })

  it('should_deleteLabel', async () => {
    const mockLabels = [
      { labelId: '1', name: 'Work', color: 'blue', noteCount: 0 },
    ]

    vi.mocked(labelApi.getLabelsApi).mockResolvedValue({
      ok: true,
      data: mockLabels,
    } as any)

    vi.mocked(labelApi.deleteLabelApi).mockResolvedValue({
      ok: true,
    } as any)

    renderWithRouter(<LabelPage />)

    await waitFor(() => {
      expect(screen.getByText('Work')).toBeInTheDocument()
    })

    const deleteButton = screen.getByRole('button', { name: /delete/i })
    await userEvent.click(deleteButton)

    expect(labelApi.deleteLabelApi).toHaveBeenCalledWith('1')
  })
})
