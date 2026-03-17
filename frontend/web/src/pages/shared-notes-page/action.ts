import type { ActionFunctionArgs } from 'react-router-dom'
import { json } from 'react-router-dom'
import type { NewNote } from '@/types/note'
import { addNote } from '@/api/note/note'

export async function sharedNotesAction({ request }: ActionFunctionArgs): Promise<any> {
  const formData = await request.formData()

  const noteContent = JSON.parse(formData.get('contentJson') as string)
  const projectId = formData.get('projectId') as string

  // 创建新笔记对象
  const newNote: NewNote = {
    contentJson: noteContent,
    labels: [],
    projectId,
  }

  // 发送到 API
  const savedNote = await addNote(newNote)
  if (savedNote.ok) {
    return json({ ok: true, note: savedNote.data })
  }
}
