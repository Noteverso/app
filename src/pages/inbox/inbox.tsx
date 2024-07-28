import { useLoaderData } from 'react-router-dom'
import { SharedNotesPage } from '../shared-notes-page/shared-notes-page'
import type { NoteListLoaderData } from '@/lib/loaders/shared-note-loader'

export function Inbox() {
  const inboxLoaderData = useLoaderData() as NoteListLoaderData

  return (
    <SharedNotesPage title="收件箱" loaderData={inboxLoaderData} />
  )
}
