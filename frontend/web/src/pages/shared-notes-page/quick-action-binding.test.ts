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

  it('extracts project and labels from lowercase live-editor attrs', () => {
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Live editor ' },
            { type: 'quickActionToken', attrs: { tokenid: 'p1', tokentype: 'project', entityid: 'project-live-1', label: 'Live Project' } },
            { type: 'quickActionToken', attrs: { tokenid: 'l1', tokentype: 'label', entityid: 'label-live-1', label: 'Live Label' } },
          ],
        },
      ],
    }

    const result = extractQuickTokenBinding(contentJson)
    expect(result.projectId).toBe('project-live-1')
    expect(result.labelIds).toEqual(['label-live-1'])
    expect(result.sanitizedContentJson).toEqual({
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Live editor ' },
          ],
        },
      ],
    })
  })

  it('normalizes numeric entity ids from the live editor to strings', () => {
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Numeric project ' },
            { type: 'quickActionToken', attrs: { tokenId: 'p1', tokenType: 'project', entityId: 315011398767874050, label: 'Numeric Project' } },
            { type: 'quickActionToken', attrs: { tokenId: 'l1', tokenType: 'label', entityId: 42, label: 'Numeric Label' } },
          ],
        },
      ],
    }

    const result = extractQuickTokenBinding(contentJson)
    expect(result.projectId).toBe('315011398767874050')
    expect(result.labelIds).toEqual(['42'])
    expect(result.sanitizedContentJson).toEqual({
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            { type: 'text', text: 'Numeric project ' },
          ],
        },
      ],
    })
  })
})
