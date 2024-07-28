import { NoteCard } from './note-card'
import type { NoteListLoaderData } from '@/lib/loaders/shared-note-loader'

export function NoteList({ noteList }: NoteListLoaderData) {
  return (
    <ul className="flex flex-col gap-x-4">
      {noteList.map(note => (
        <li key={note.noteId} className="last:pt-0 pt-8 pb-8 border-b">
          <NoteCard
            labels={note.labels}
            content={note.content}
            timeStamp={note.timeStamp}
            project={note.project}
            fileNumber={note.fileNumber}
            linkedNoteNumber={note.linkedNoteNumber}
            linkingNoteNumber={note.linkingNoteNumber}
          />
        </li>
      ))}
    </ul>
  )
}
