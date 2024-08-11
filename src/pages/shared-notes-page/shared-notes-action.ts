import type { ActionFunctionArgs } from 'react-router-dom'
import { json } from 'react-router-dom'
import type { NewNote } from '@/types/note'

export async function sharedNotesAction({ request }: ActionFunctionArgs): Promise<any> {
  const formData = await request.formData()

  const noteContent = formData.get('content') as string
  const projectId = formData.get('projectId') as string

  // 创建新笔记对象
  const newNote: NewNote = {
    content: noteContent,
    labels: [],
    projectId,
  }

  // 发送到 API
  // const savedNote = await saveNoteToAPI(newNote)
  const wait = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

  await wait(5 * 1000)

  // throw json(
  //   { message: 'Invalid update' },
  //   { status: 404 },
  // )

  return json({ ok: false, errors: 'error request', note: newNote })
}
