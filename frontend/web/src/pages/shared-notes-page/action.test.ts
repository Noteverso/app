import { beforeEach, describe, expect, it, vi } from 'vitest'
import type { Mock } from 'vitest'
import { sharedNotesAction } from './action'
import { addNote } from '@/api/note/note'

vi.mock('@/api/note/note', () => ({
  addNote: vi.fn(),
}))

describe('sharedNotesAction', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('submits labels parsed from form data', async () => {
    ;(addNote as Mock).mockResolvedValue({
      ok: true,
      data: 'new-note-id',
    })

    const formData = new FormData()
    formData.set('contentJson', JSON.stringify({ type: 'doc', content: [] }))
    formData.set('projectId', 'project-1')
    formData.set('labels', JSON.stringify(['label-1', 'label-2']))
    const request = new Request('http://localhost/app/inbox', { method: 'POST', body: formData })

    const result = await sharedNotesAction({ request } as never)

    expect(addNote).toHaveBeenCalledWith({
      contentJson: { type: 'doc', content: [] },
      labels: ['label-1', 'label-2'],
      projectId: 'project-1',
    })

    expect(result).not.toBeNull()
    expect(result).toBeInstanceOf(Response)
    const data = await (result as Response).json()
    expect(data).toEqual({ ok: true, note: 'new-note-id' })
  })

  it('defaults labels to empty array when missing', async () => {
    ;(addNote as Mock).mockResolvedValue({
      ok: true,
      data: 'new-note-id',
    })

    const formData = new FormData()
    formData.set('contentJson', JSON.stringify({ type: 'doc', content: [] }))
    formData.set('projectId', 'project-1')
    const request = new Request('http://localhost/app/inbox', { method: 'POST', body: formData })

    await sharedNotesAction({ request } as never)

    expect(addNote).toHaveBeenCalledWith({
      contentJson: { type: 'doc', content: [] },
      labels: [],
      projectId: 'project-1',
    })
  })
})
