import { describe, expect, it } from 'vitest'
import {
  createSavedNoteListItem,
  getCreatedNoteNavigationToastTitle,
  isCreateSubmitDisabled,
  mergeSameRouteNoteRecords,
  nextEditorInstanceKeyOnSave,
  reconcileOptimisticNote,
  resolveSharedNotesRouteContext,
  shouldFinalizeOptimisticNote,
  shouldInsertOptimisticNote,
  shouldKeepOptimisticNoteInCurrentList,
  shouldShowCreatedNoteNavigationHint,
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

function createActiveSave(overrides: Partial<Parameters<typeof createSavedNoteListItem>[0]> = {}) {
  return {
    tempNoteId: 'temp-1',
    rawContentJson: { type: 'doc', content: [] },
    sanitizedContentJson: { type: 'doc', content: [] },
    hasEditorContent: true,
    labels: [],
    project: { projectId: 'p2', name: 'Project Two' },
    navigationHint: {
      projectId: 'p2',
      projectName: 'Project Two',
      routePath: '/app/projects/p2',
    },
    shouldInsertOptimisticNote: false,
    ...overrides,
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
    expect(shouldFinalizeOptimisticNote('loading', 'idle')).toBe(true)
    expect(shouldFinalizeOptimisticNote('submitting', 'loading')).toBe(false)
    expect(shouldFinalizeOptimisticNote('idle', 'idle')).toBe(false)
    expect(shouldFinalizeOptimisticNote('submitting', 'submitting')).toBe(false)
  })

  it('allows another optimistic insert on next independent save cycle', () => {
    expect(shouldInsertOptimisticNote('idle', 'submitting', true, false)).toBe(true)
    expect(shouldFinalizeOptimisticNote('submitting', 'idle')).toBe(true)
    expect(shouldInsertOptimisticNote('idle', 'submitting', true, false)).toBe(true)
  })

  it('keeps optimistic note only when save target matches current route project', () => {
    expect(shouldKeepOptimisticNoteInCurrentList('project-1', 'project-1')).toBe(true)
    expect(shouldKeepOptimisticNoteInCurrentList('project-1', 'project-2')).toBe(false)
    expect(shouldKeepOptimisticNoteInCurrentList('', 'project-2')).toBe(true)
  })

  it('shows navigation hint only for successful cross-project creates', () => {
    expect(shouldShowCreatedNoteNavigationHint('project-1', 'project-2')).toBe(true)
    expect(shouldShowCreatedNoteNavigationHint('project-1', 'project-1')).toBe(false)
    expect(shouldShowCreatedNoteNavigationHint('', 'project-2')).toBe(false)
  })

  it('disables create while a save is pending or content is empty', () => {
    expect(isCreateSubmitDisabled('idle', true)).toBe(false)
    expect(isCreateSubmitDisabled('submitting', true)).toBe(true)
    expect(isCreateSubmitDisabled('loading', true)).toBe(true)
    expect(isCreateSubmitDisabled('idle', false)).toBe(true)
  })

  it('replaces only target temp note on success', () => {
    const notes = [createNote('temp-1'), createNote('temp-2'), createNote('real-1')]
    const updated = reconcileOptimisticNote(
      notes,
      createActiveSave({ tempNoteId: 'temp-2' }),
      { ok: true, note: 'real-2' },
    )
    expect(updated.map(note => note.noteId)).toEqual(['temp-1', 'real-2', 'real-1'])
  })

  it('removes target temp note on failure rollback', () => {
    const notes = [createNote('temp-1'), createNote('real-1')]
    const updated = reconcileOptimisticNote(notes, createActiveSave({ tempNoteId: 'temp-1' }))
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

  it('creates saved note list items without note-local navigation hint metadata', () => {
    const created = createSavedNoteListItem(createActiveSave(), 'real-2', '2026-01-02T00:00:00.000Z')

    expect(created.noteId).toBe('real-2')
    expect(created.project).toEqual({ projectId: 'p2', name: 'Project Two' })
  })

  it('builds global navigation toast copy for the destination project', () => {
    expect(getCreatedNoteNavigationToastTitle('Project Two')).toBe('Note created in Project Two')
  })

  it('merges same-route refresh data without dropping already loaded notes', () => {
    const merged = mergeSameRouteNoteRecords(
      [createNote('real-1'), createNote('real-2'), createNote('real-3')],
      [createNote('real-4'), createNote('real-1')],
    )

    expect(merged.map(note => note.noteId)).toEqual(['real-4', 'real-1', 'real-2', 'real-3'])
  })

  it('treats non-project routes as unscoped create flows with inbox fallback', () => {
    const routeContext = resolveSharedNotesRouteContext(
      '/app/labels/label-1',
      undefined,
      [{ projectId: 'project-2', name: 'Project Two', inboxProject: false }],
      { projectId: 'inbox-1' },
      'Important',
    )

    expect(routeContext.routeKind).toBe('other')
    expect(routeContext.supportsScopedCreateBehavior).toBe(false)
    expect(routeContext.currentProjectId).toBe('')
    expect(routeContext.defaultSelectedProjectId).toBe('inbox-1')
    expect(routeContext.actionPath).toBe('/app/labels/label-1')
  })
})
