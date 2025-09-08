import { useLoaderData } from 'react-router-dom'
import { SharedNotesPage } from '../shared-notes-page/shared-notes-page'
import type { NotePageLoaderData } from '@/types/note'

export function Project() {
  const notePageData = useLoaderData() as NotePageLoaderData

  return (
    <SharedNotesPage initialNotePage={notePageData} />
  )
}
