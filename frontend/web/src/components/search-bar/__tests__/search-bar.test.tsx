import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { SearchBar } from '../search-bar'
import * as labelApi from '@/api/label/label'
import { BrowserRouter } from 'react-router-dom'

vi.mock('@/api/label/label')

const renderWithRouter = (component: React.ReactElement) => {
  return render(<BrowserRouter>{component}</BrowserRouter>)
}

describe('SearchBar', () => {
  const mockOnSearch = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(labelApi.getLabelSelectItemsApi).mockResolvedValue({
      ok: true,
      data: [],
    } as any)
  })

  it('should_renderSearchBar', () => {
    renderWithRouter(<SearchBar onSearch={mockOnSearch} />)
    
    expect(screen.getByPlaceholderText(/search/i)).toBeInTheDocument()
  })

  it('should_updateInput_onType', async () => {
    renderWithRouter(<SearchBar onSearch={mockOnSearch} />)
    
    const input = screen.getByPlaceholderText(/search/i)
    await userEvent.type(input, 'test query')

    expect(input).toHaveValue('test query')
  })

  it('should_submitSearch_onEnter', async () => {
    renderWithRouter(<SearchBar onSearch={mockOnSearch} />)
    
    const input = screen.getByPlaceholderText(/search/i)
    await userEvent.type(input, 'test{Enter}')

    expect(mockOnSearch).toHaveBeenCalledWith(
      expect.objectContaining({
        keyword: 'test',
      })
    )
  })

  it('should_clearSearch', async () => {
    renderWithRouter(<SearchBar onSearch={mockOnSearch} />)
    
    const input = screen.getByPlaceholderText(/search/i)
    await userEvent.type(input, 'test query')
    
    expect(input).toHaveValue('test query')
    
    const clearButton = screen.getByRole('button', { name: /clear/i })
    await userEvent.click(clearButton)

    expect(input).toHaveValue('')
  })

  it('should_openFilters', async () => {
    renderWithRouter(<SearchBar onSearch={mockOnSearch} />)
    
    const filterButton = screen.getByRole('button', { name: /filter/i })
    await userEvent.click(filterButton)

    expect(screen.getByText(/sort by/i)).toBeInTheDocument()
  })
})
