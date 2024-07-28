import { useFetcher, useLocation, useParams } from 'react-router-dom'
import { useEffect, useMemo, useState } from 'react'
import { v4 as uuidv4 } from 'uuid'
import { NoteList } from '@/features/note'
import { TextEditor } from '@/features/editor'
import type { NoteListLoaderData } from '@/lib/loaders/shared-note-loader'
import { Button } from '@/components/button/button'
import { ROUTER_PATHS } from '@/routes/path'
import type { FullNote } from '@/types/note'

interface ProjectPageProps {
  title?: string
  loaderData: NoteListLoaderData
}

export function SharedNotesPage({ title, loaderData }: ProjectPageProps) {
  const noteList = loaderData.noteList
  const fetcher = useFetcher()
  const location = useLocation()
  const params = useParams()
  const projectId = params.projectId
  const isInbox = location.pathname.includes(ROUTER_PATHS.INBOX.path)
  const actionPath = isInbox ? ROUTER_PATHS.INBOX.path : `${ROUTER_PATHS.PROJECTS.path}/${projectId}`
  const [editorContent, setEditorContent] = useState('')
  const [hasEditorContent, setHasEditorContent] = useState(false)

  useEffect(() => {
    if (fetcher.state === 'idle' && fetcher.data) {
      console.warn('fetcher', fetcher.data)
    }
  }, [fetcher])

  // 计算优化更新后的笔记列表
  const optimisticNotes = useMemo(() => {
    if (fetcher.formData) {
      const content = fetcher.formData.get('content') as string
      const optimisticNote: FullNote = {
        noteId: uuidv4(),
        content,
        timeStamp: new Date().toISOString(),
        labels: [],
        project: { projectId: '', name: '' },
        fileNumber: 0,
        linkedNoteNumber: 0,
        linkingNoteNumber: 0,
      }
      return [optimisticNote, ...noteList]
    }

    return noteList
  }, [fetcher.formData, noteList])

  function handleContentChange(content: string, hasContent: boolean) {
    setEditorContent(content)
    setHasEditorContent(hasContent)
  }

  return (
    <div className="flex flex-col">
      <h1 className="text-2xl mb-4">{title}</h1>
      <TextEditor className="mb-4" onChange={handleContentChange} />
      <fetcher.Form method="post" action={actionPath} className="mb-4">
        <input type="hidden" name="content" value={editorContent} />
        <div className="flex">
          <div className="text-right ml-auto">
            <Button
              type="submit"
              name="note-save"
              value="save"
              disabled={fetcher.state === 'submitting' || (!hasEditorContent && fetcher.state === 'idle')}>
              保存
            </Button>
          </div>
        </div>
      </fetcher.Form>

      <NoteList noteList={optimisticNotes} />
    </div>
  )
}

