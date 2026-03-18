import { describe, expect, it } from 'vitest'
import { EDITOR_CONTENT_MAX_HEIGHT_CLASS, parseQuickActionFromTextBefore } from './text-editor'

describe('parseQuickActionFromTextBefore', () => {
  it('parses project token', () => {
    expect(parseQuickActionFromTextBefore('hello #alpha')).toEqual({
      type: 'project',
      keyword: 'alpha',
      token: '#alpha',
    })
  })

  it('parses label token', () => {
    expect(parseQuickActionFromTextBefore('todo @urgent')).toEqual({
      type: 'label',
      keyword: 'urgent',
      token: '@urgent',
    })
  })

  it('returns null for unsupported token shapes', () => {
    expect(parseQuickActionFromTextBefore('hello #')).toBeNull()
    expect(parseQuickActionFromTextBefore('hello #alpha,test')).toBeNull()
    expect(parseQuickActionFromTextBefore('hello #alpha beta')).toBeNull()
  })

  it('uses fixed editor max-height class', () => {
    expect(EDITOR_CONTENT_MAX_HEIGHT_CLASS).toBe('max-h-[280px]')
  })
})
