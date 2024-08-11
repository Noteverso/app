import { NoteCard } from './note-card'
import type { FullNote } from '@/types/note'

export function NoteList({ notes, refFunc }: { notes: FullNote[], refFunc: (node: HTMLElement | null) => void }) {
  return (
    <ul className="flex flex-col gap-x-4">
      {notes.map((note, index) => (
        <li
          key={note.noteId}
          className="pt-8 pb-8 border-b first:border-t"
          ref={index === notes.length - 1 ? refFunc : null}
        >
          <NoteCard
            labels={note.labels}
            content={note.content}
            addedAt={note.addedAt}
            project={note.project}
            attachmentCount={note.attachmentCount}
            referencedCount={note.referencedCount}
            referencingCount={note.referencingCount}
          />
        </li>
      ))}
    </ul>
  )
}
