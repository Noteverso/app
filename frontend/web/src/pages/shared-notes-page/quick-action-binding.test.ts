import { describe, expect, it } from 'vitest'
import { extractQuickTokenBinding } from './quick-action-binding'

describe('extractQuickTokenBinding', () => {
  it('extracts active project and labels while removing token nodes', () => {
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Start ' },
            { type: 'quickActionToken', attrs: { tokenId: 'p1', tokenType: 'project', entityId: 'project-1', label: 'Project One' } },
            { type: 'text', text: ' body ' },
            { type: 'quickActionToken', attrs: { tokenId: 'l1', tokenType: 'label', entityId: 'label-1', label: 'Label One' } },
            { type: 'quickActionToken', attrs: { tokenId: 'l2', tokenType: 'label', entityId: 'label-2', label: 'Label Two' } },
          ],
        },
      ],
    }

    const result = extractQuickTokenBinding(contentJson)
    expect(result.projectId).toBe('project-1')
    expect(result.labelIds).toEqual(['label-1', 'label-2'])
    expect(result.sanitizedContentJson).toEqual({
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Start ' },
            { type: 'text', text: ' body ' },
          ],
        },
      ],
    })
  })

  it('deduplicates labels and keeps downgraded #project plain text', () => {
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: '#OldProject plain text ' },
            { type: 'quickActionToken', attrs: { tokenId: 'l1', tokenType: 'label', entityId: 'label-1', label: 'Label One' } },
            { type: 'quickActionToken', attrs: { tokenId: 'l1b', tokenType: 'label', entityId: 'label-1', label: 'Label One' } },
          ],
        },
      ],
    }

    const result = extractQuickTokenBinding(contentJson)
    expect(result.projectId).toBeNull()
    expect(result.labelIds).toEqual(['label-1'])
    expect(result.sanitizedContentJson).toEqual({
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: '#OldProject plain text ' },
          ],
        },
      ],
    })
  })
})
