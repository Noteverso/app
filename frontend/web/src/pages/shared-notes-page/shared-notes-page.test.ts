import { describe, expect, it } from 'vitest'
import {
  nextEditorInstanceKeyOnSave,
  reconcileOptimisticNote,
  shouldFinalizeOptimisticNote,
  shouldInsertOptimisticNote,
} from './shared-notes-page'

function createNote(noteId: string) {
  return {
    noteId,
    contentJson: {},
    labels: [],
    project: { projectId: 'p1', name: 'Inbox' },
    addedAt: '2026-01-01T00:00:00.000Z',
    updatedAt: '2026-01-01T00:00:00.000Z',
    isArchived: 0 as const,
    isDeleted: 0 as const,
    isPinned: 0 as const,
    attachmentCount: null,
    referencedCount: null,
    referencingCount: null,
    creator: 'u1',
  }
}

describe('shared notes optimistic save guards', () => {
  it('inserts optimistic note only once for the same submitting phase', () => {
    expect(shouldInsertOptimisticNote('idle', 'submitting', true, false)).toBe(true)
    expect(shouldInsertOptimisticNote('submitting', 'submitting', true, true)).toBe(false)
    expect(shouldInsertOptimisticNote('submitting', 'submitting', true, false)).toBe(false)
  })

  it('does not insert when form data is missing', () => {
    expect(shouldInsertOptimisticNote('idle', 'submitting', false, false)).toBe(false)
  })

  it('finalizes optimistic note only when submit transitions to idle', () => {
    expect(shouldFinalizeOptimisticNote('submitting', 'idle')).toBe(true)
    expect(shouldFinalizeOptimisticNote('idle', 'idle')).toBe(false)
    expect(shouldFinalizeOptimisticNote('submitting', 'submitting')).toBe(false)
  })

  it('allows another optimistic insert on next independent save cycle', () => {
    expect(shouldInsertOptimisticNote('idle', 'submitting', true, false)).toBe(true)
    expect(shouldFinalizeOptimisticNote('submitting', 'idle')).toBe(true)
    expect(shouldInsertOptimisticNote('idle', 'submitting', true, false)).toBe(true)
  })

  it('replaces only target temp note on success', () => {
    const notes = [createNote('temp-1'), createNote('temp-2'), createNote('real-1')]
    const updated = reconcileOptimisticNote(
      notes,
      { tempNoteId: 'temp-2', contentJson: {} },
      { ok: true, note: 'real-2' },
    )
    expect(updated.map(note => note.noteId)).toEqual(['temp-1', 'real-2', 'real-1'])
  })

  it('removes target temp note on failure rollback', () => {
    const notes = [createNote('temp-1'), createNote('real-1')]
    const updated = reconcileOptimisticNote(notes, { tempNoteId: 'temp-1', contentJson: {} })
    expect(updated.map(note => note.noteId)).toEqual(['real-1'])
  })

  it('increments editor instance key on successful save', () => {
    expect(nextEditorInstanceKeyOnSave(0, { ok: true, note: 'real-1' })).toBe(1)
    expect(nextEditorInstanceKeyOnSave(10, { ok: true, note: 'real-2' })).toBe(11)
  })

  it('keeps editor instance key unchanged on failed or missing save result', () => {
    expect(nextEditorInstanceKeyOnSave(3, undefined)).toBe(3)
    expect(nextEditorInstanceKeyOnSave(3, { ok: false, note: 'ignored' })).toBe(3)
  })
})
