import { NoteCard } from './note-card'
import type { NoteListItem } from '@/types/note'

interface NoteListProps {
  notes: NoteListItem[]
  refFunc: (node: HTMLElement | null) => void
  onEdit?: (note: NoteListItem) => void
  onDelete?: (note: NoteListItem) => void
}

export function NoteList({ notes, refFunc, onEdit, onDelete }: NoteListProps) {
  return (
    <ul className="m-0 w-full list-none p-0">
      {notes.map((note, index) => (
        <li
          key={note.noteId}
          ref={index === notes.length - 1 ? refFunc : null}
        >
          <div
            data-testid={`note-list-row-${note.noteId}`}
            className={`w-full border-b py-8 hover:cursor-pointer ${index === 0 ? 'border-t' : ''}`}
          >
            <NoteCard
              labels={note.labels}
              contentJson={note.contentJson || {}}
              addedAt={note.addedAt}
              project={note.project}
              attachmentCount={note.attachmentCount}
              referencedCount={note.referencedCount}
              referencingCount={note.referencingCount}
              onEdit={onEdit ? () => onEdit(note) : undefined}
              onDelete={onDelete ? () => onDelete(note) : undefined}
            />
          </div>
        </li>
      ))}
    </ul>
  )
}
