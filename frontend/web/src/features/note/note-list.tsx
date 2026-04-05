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
    <ul className="flex flex-col gap-x-4">
      {notes.map((note, index) => (
        <li
          key={note.noteId}
          className="pt-8 pb-8 border-b first:border-t hover:cursor-pointer"
          ref={index === notes.length - 1 ? refFunc : null}
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
        </li>
      ))}
    </ul>
  )
}
