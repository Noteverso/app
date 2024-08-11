import { type LoaderFunctionArgs, json } from 'react-router-dom'
import type { NotePageLoaderData } from '@/types/note'
import { getNotesApi } from '@/api/note/note'

// const content = '<p> Note the :contactId URL segment. The colon (:) has special meaning, turning it into a "dynamic segment". Dynamic segments will match dynamic (changing) values in that position of the URL, like the contact ID. We call these values in the URL "URL Params", or just "params" for short. </p>'
// const noteList = []
// for (let i = 0; i < 50; i++) {
//   noteList.push({
//     noteId: `${i + 1}`,
//     content,
//     timeStamp: '2024-10-02 10:23:02',
//     labels: [{ labelId: '1', name: 'tag1' }, { labelId: '2', name: 'tag2' }],
//     project: { projectId: `${i + 1}`, name: `project${i + 1}` },
//     fileNumber: 1,
//     linkedNoteNumber: 1,
//     linkingNoteNumber: 1,
//   })
// }

export async function sharedNotesLoader({ params }: LoaderFunctionArgs): Promise<NotePageLoaderData> {
  const { projectId } = params

  const response = await getNotesApi({
    objectId: projectId || '',
    pageSize: 10,
    pageIndex: 1,
  })

  if (!response.ok) {
    throw json(response.data, { status: response.status })
  }

  const notePageData = response.data

  if (projectId) {
    return {
      ...notePageData,
      records: notePageData.records.filter(note => note.project.projectId === params.projectId),

    }
  }

  return notePageData
}

