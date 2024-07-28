import type { LoaderFunctionArgs } from 'react-router-dom'
import type { FullNote } from '@/types/note'

const content = '<p> Note the :contactId URL segment. The colon (:) has special meaning, turning it into a "dynamic segment". Dynamic segments will match dynamic (changing) values in that position of the URL, like the contact ID. We call these values in the URL "URL Params", or just "params" for short. </p>'

export interface NoteListLoaderData {
  noteList: FullNote[]
}

export interface ProjectLoaderProps {
  params?: { projectId: string }
}

export async function sharedNoteLoader({ params }: LoaderFunctionArgs): Promise<NoteListLoaderData> {
  // const projectList = await getProjectListApi()

  const noteList = []
  for (let i = 0; i < 50; i++) {
    noteList.push({
      noteId: `${i + 1}`,
      content,
      timeStamp: '2024-10-02 10:23:02',
      labels: [{ labelId: '1', name: 'tag1' }, { labelId: '2', name: 'tag2' }],
      project: { projectId: `${i + 1}`, name: `project${i + 1}` },
      fileNumber: 1,
      linkedNoteNumber: 1,
      linkingNoteNumber: 1,
    })
  }

  if (params?.projectId) {
    return {
      noteList: noteList.filter(note => note.project.projectId === params.projectId),
    }
  }

  return {
    noteList,
  }
}

