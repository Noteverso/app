import { fireEvent, render, screen } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { NoteCard } from './note-card'

const navigateMock = vi.fn()

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')
  return {
    ...actual,
    useNavigate: () => navigateMock,
  }
})

describe('NoteCard', () => {
  beforeEach(() => {
    navigateMock.mockReset()
  })

  it('does not render global navigation toast copy inside the note card', () => {
    render(
      <NoteCard
        contentJson={{ type: 'doc', content: [{ type: 'paragraph', content: [{ type: 'text', text: 'Cross project' }] }] }}
        addedAt="2026-01-01T00:00:00.000Z"
        labels={[]}
        project={{ projectId: 'p2', name: 'Project Two' }}
      />,
    )

    expect(screen.queryByText('Note created in Project Two')).not.toBeInTheDocument()
    expect(screen.queryByText('Open project')).not.toBeInTheDocument()
    expect(navigateMock).not.toHaveBeenCalled()
  })

  it('still navigates to label detail from note metadata', () => {
    render(
      <NoteCard
        contentJson={{ type: 'doc', content: [{ type: 'paragraph', content: [{ type: 'text', text: 'Same project' }] }] }}
        addedAt="2026-01-01T00:00:00.000Z"
        labels={[{ labelId: 'label-1', name: 'Work' }]}
        project={{ projectId: 'p1', name: 'Project One' }}
      />,
    )

    fireEvent.click(screen.getByText('Work'))
    expect(navigateMock).toHaveBeenCalledWith('/app/labels/label-1')
  })
})
