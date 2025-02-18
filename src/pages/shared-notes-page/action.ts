import type { ActionFunctionArgs } from 'react-router-dom'
import { json } from 'react-router-dom'
import type { NewNote } from '@/types/note'
import { addNote } from '@/api/note/note'

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
  const savedNote = await addNote(newNote)
  if (savedNote.ok) {
    return json({ ok: true, note: savedNote.data })
  }
}
