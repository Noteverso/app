import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LabelSelector } from '../label-selector'
import * as labelApi from '@/api/label/label'

vi.mock('@/api/label/label')

describe('LabelSelector', () => {
  const mockOnChange = vi.fn()
  const mockLabels = [
    { value: '1', label: 'Work', color: 'blue' },
    { value: '2', label: 'Personal', color: 'green' },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should_renderLabelSelector', () => {
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)

    render(<LabelSelector selectedLabelIds={[]} onChange={mockOnChange} />)
    
    expect(screen.getByText('Add Label')).toBeInTheDocument()
  })

  it('should_displayLabels_whenLoaded', async () => {
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: mockLabels,
    } as any)

    render(<LabelSelector selectedLabelIds={[]} onChange={mockOnChange} />)
    
    const button = screen.getByText('Add Label')
    await userEvent.click(button)

    await waitFor(() => {
      expect(screen.getByText('Work')).toBeInTheDocument()
      expect(screen.getByText('Personal')).toBeInTheDocument()
    })
  })

  it('should_displayEmptyState_whenNoLabels', async () => {
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)

    render(<LabelSelector selectedLabelIds={[]} onChange={mockOnChange} />)
    
    const button = screen.getByText('Add Label')
    await userEvent.click(button)

    await waitFor(() => {
      const popover = screen.getByRole('dialog')
      expect(popover).toBeInTheDocument()
    })
  })

  it('should_selectLabel_onClick', async () => {
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: mockLabels,
    } as any)

    render(<LabelSelector selectedLabelIds={[]} onChange={mockOnChange} />)
    
    const button = screen.getByText('Add Label')
    await userEvent.click(button)

    await waitFor(() => {
      expect(screen.getByText('Work')).toBeInTheDocument()
    })

    const workLabel = screen.getByText('Work')
    await userEvent.click(workLabel)

    expect(mockOnChange).toHaveBeenCalledWith(['1'])
  })

  it('should_deselectLabel_onClick', async () => {
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: mockLabels,
    } as any)

    render(<LabelSelector selectedLabelIds={['1']} onChange={mockOnChange} />)
    
    const button = screen.getByText('Add Label')
    await userEvent.click(button)

    await waitFor(() => {
      expect(screen.getByText('Work')).toBeInTheDocument()
    })

    const workLabel = screen.getByText('Work')
    await userEvent.click(workLabel)

    expect(mockOnChange).toHaveBeenCalledWith([])
  })
})
