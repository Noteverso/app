import { describe, expect, it } from 'vitest'
import { sanitizeQuickActionContentJson, wrapQuickActionTokenForEditor } from './quick-action-sanitize'

describe('quick-action-sanitize', () => {
  it('removes only wrapped quick-action tokens and keeps manual hashtags/mentions', () => {
    const wrappedProject = wrapQuickActionTokenForEditor('#Project Alpha')
    const wrappedLabel = wrapQuickActionTokenForEditor('@P1')
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            {
              type: 'text',
              text: `manual #keep ${wrappedProject} and @keep ${wrappedLabel}`,
            },
          ],
        },
      ],
    }

    const sanitized = sanitizeQuickActionContentJson(contentJson) as { content: Array<{ content: Array<{ text: string }> }> }
    expect(sanitized.content[0].content[0].text).toBe('manual #keep and @keep')
  })

  it('keeps punctuation and multiline layout after wrapped token cleanup', () => {
    const wrappedProject = wrapQuickActionTokenForEditor('#MySQL')
    const wrappedLabel = wrapQuickActionTokenForEditor('@urgent')
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            {
              type: 'text',
              text: `Start ${wrappedProject}, continue.\nNext line ${wrappedLabel} done!`,
            },
          ],
        },
      ],
    }

    const sanitized = sanitizeQuickActionContentJson(contentJson) as { content: Array<{ content: Array<{ text: string }> }> }
    expect(sanitized.content[0].content[0].text).toBe('Start, continue.\nNext line done!')
  })

  it('handles wrapped tokens with spaces and special characters', () => {
    const wrappedProject = wrapQuickActionTokenForEditor('#Roadmap V2.1')
    const wrappedLabel = wrapQuickActionTokenForEditor('@api-team/core')
    const contentJson = {
      type: 'doc',
      content: [
        {
          type: 'paragraph',
          content: [
            {
              type: 'text',
              text: `${wrappedProject} release ${wrappedLabel} now`,
            },
          ],
        },
      ],
    }

    const sanitized = sanitizeQuickActionContentJson(contentJson) as { content: Array<{ content: Array<{ text: string }> }> }
    expect(sanitized.content[0].content[0].text).toBe('release now')
  })
})
