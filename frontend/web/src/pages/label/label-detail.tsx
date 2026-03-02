import { useLoaderData } from 'react-router-dom'
import { SharedNotesPage } from '../shared-notes-page/shared-notes-page'
import type { NotePageLoaderData } from '@/types/note'

interface LabelDetailLoaderData {
  notePageData: NotePageLoaderData
  labelName: string
}

export function LabelDetail() {
  const { notePageData, labelName } = useLoaderData() as LabelDetailLoaderData

  return (
    <SharedNotesPage title={labelName} initialNotePage={notePageData} />
  )
}
