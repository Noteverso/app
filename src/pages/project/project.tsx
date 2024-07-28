import { useLoaderData, useParams } from 'react-router-dom'
import { SharedNotesPage } from '../shared-notes-page/shared-notes-page'
import type { NoteListLoaderData } from '@/lib/loaders/shared-note-loader'

export function Project() {
  const params = useParams()
  const paramsId = params.projectId

  const projectLoaderData = useLoaderData() as NoteListLoaderData

  return (
    <SharedNotesPage title={paramsId} loaderData={projectLoaderData} />
  )
}
