import { useLoaderData } from 'react-router-dom'
import { SharedNotesPage } from '../shared-notes-page/shared-notes-page'
import type { NotePageLoaderData } from '@/types/note'

export function Inbox() {
  const notePageData = useLoaderData() as NotePageLoaderData

  return (
    <SharedNotesPage title="收件箱" initialNotePage={notePageData} />
  )
}
