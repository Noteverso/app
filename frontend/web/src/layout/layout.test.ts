import { describe, expect, it } from 'vitest'
import { getContentTopControlsClass } from './layout'

describe('layout top controls', () => {
  it('renders mobile-only top controls class', () => {
    expect(getContentTopControlsClass()).toContain('md:hidden')
  })
})
