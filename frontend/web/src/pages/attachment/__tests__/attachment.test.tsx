import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { AttachmentPage } from '../attachment'
import * as attachmentApi from '@/api/attachment/attachment'

vi.mock('@/api/attachment/attachment')

const renderWithRouter = (component: React.ReactElement) => {
  return render(<BrowserRouter>{component}</BrowserRouter>)
}

describe('AttachmentPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should_renderAttachmentPage', () => {
    vi.mocked(attachmentApi.getUserAttachmentsApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<AttachmentPage />)
    
    expect(screen.getByText(/attachments/i)).toBeInTheDocument()
  })

  it('should_displayEmptyState_whenNoAttachments', async () => {
    vi.mocked(attachmentApi.getUserAttachmentsApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<AttachmentPage />)

    await waitFor(() => {
      expect(screen.getByText(/no attachments/i)).toBeInTheDocument()
    })
  })

  it('should_displayAttachments_whenLoaded', async () => {
    const mockAttachments = [
      { attachmentId: '1', name: 'document.pdf', size: 1024, type: 'application/pdf' },
      { attachmentId: '2', name: 'image.png', size: 2048, type: 'image/png' },
    ]

    vi.mocked(attachmentApi.getUserAttachmentsApi).mockResolvedValue({
      ok: true,
      data: { records: mockAttachments, total: 2, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<AttachmentPage />)

    await waitFor(() => {
      expect(screen.getByText('document.pdf')).toBeInTheDocument()
      expect(screen.getByText('image.png')).toBeInTheDocument()
    })
  })

  it('should_uploadFile', async () => {
    vi.mocked(attachmentApi.getUserAttachmentsApi).mockResolvedValue({
      ok: true,
      data: { records: [], total: 0, pageIndex: 1, pageSize: 20 },
    } as any)

    renderWithRouter(<AttachmentPage />)

    const uploadButton = screen.getByRole('button', { name: /upload/i })
    expect(uploadButton).toBeInTheDocument()
  })

  it('should_deleteAttachment', async () => {
    const mockAttachments = [
      { attachmentId: '1', name: 'document.pdf', size: 1024, type: 'application/pdf' },
    ]

    vi.mocked(attachmentApi.getUserAttachmentsApi).mockResolvedValue({
      ok: true,
      data: { records: mockAttachments, total: 1, pageIndex: 1, pageSize: 20 },
    } as any)

    vi.mocked(attachmentApi.deleteAttachmentApi).mockResolvedValue({
      ok: true,
    } as any)

    renderWithRouter(<AttachmentPage />)

    await waitFor(() => {
      expect(screen.getByText('document.pdf')).toBeInTheDocument()
    })

    const deleteButton = screen.getByRole('button', { name: /delete/i })
    await userEvent.click(deleteButton)

    expect(attachmentApi.deleteAttachmentApi).toHaveBeenCalledWith('1')
  })
})
