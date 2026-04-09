import { fireEvent, render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { NoteList } from './note-list'
import type { NoteListItem } from '@/types/note'

const noteCardMock = vi.fn()

vi.mock('./note-card', () => ({
  NoteCard: (props: { onEdit?: () => void; onDelete?: () => void }) => {
    noteCardMock(props)

    return (
      <div data-testid="note-card">
        <button type="button" onClick={props.onEdit}>Edit note</button>
        <button type="button" onClick={props.onDelete}>Delete note</button>
      </div>
    )
  },
}))

function createNote(noteId: string): NoteListItem {
  const timestamp = '2026-01-01T00:00:00.000Z'

  return {
    noteId,
    contentJson: {
      type: 'doc',
      content: [{ type: 'paragraph', content: [{ type: 'text', text: noteId }] }],
    },
    addedAt: timestamp,
    updatedAt: timestamp,
    labels: [],
    project: { projectId: 'project-1', name: 'Project One' },
    attachmentCount: null,
    referencedCount: null,
    referencingCount: null,
    isDeleted: 0,
    isPinned: 0,
    isArchived: 0,
    creator: 'tester',
  }
}

describe('NoteList', () => {
  beforeEach(() => {
    noteCardMock.mockReset()
  })

  it('lets the inner row wrapper own padding and borders instead of the li', () => {
    const refFunc = vi.fn()
    const onEdit = vi.fn()
    const onDelete = vi.fn()
    const notes = [createNote('note-1'), createNote('note-2')]
    const { container } = render(
      <NoteList notes={notes} refFunc={refFunc} onEdit={onEdit} onDelete={onDelete} />,
    )

    const listItems = container.querySelectorAll('li')
    const firstRow = screen.getByTestId('note-list-row-note-1')
    const secondRow = screen.getByTestId('note-list-row-note-2')

    expect(listItems).toHaveLength(2)
    expect(listItems[0].className).not.toContain('py-8')
    expect(listItems[0].className).not.toContain('border-b')

    expect(firstRow.className).toContain('w-full')
    expect(firstRow.className).toContain('py-8')
    expect(firstRow.className).toContain('border-b')
    expect(firstRow.className).toContain('border-t')

    expect(secondRow.className).toContain('w-full')
    expect(secondRow.className).toContain('py-8')
    expect(secondRow.className).toContain('border-b')
    expect(secondRow.className).not.toContain('border-t')

    fireEvent.click(screen.getAllByRole('button', { name: 'Edit note' })[0])
    fireEvent.click(screen.getAllByRole('button', { name: 'Delete note' })[1])

    expect(onEdit).toHaveBeenCalledWith(notes[0])
    expect(onDelete).toHaveBeenCalledWith(notes[1])
    expect(refFunc).toHaveBeenLastCalledWith(listItems[1])
  })
})
