import { json, useFetcher, useLocation, useOutletContext, useParams } from 'react-router-dom'
import { useCallback, useEffect, useRef, useState } from 'react'
import { HashIcon, Inbox } from 'lucide-react'
import { v4 as uuidv4 } from 'uuid'
import { NoteList } from '@/features/note'
import { TextEditor } from '@/features/editor'
import { Button } from '@/components/button/button'
import { ROUTER_PATHS } from '@/constants'
import type { FullNote, NotePageLoaderData } from '@/types/note'
import { getNotesApi } from '@/api/note/note'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/select/select'
import type { ProjectOutletContext } from '@/types/project'
import type { EditorMethods } from '@/features/editor/text-editor'

interface ProjectPageProps {
  title?: string;
  initialNotePage: NotePageLoaderData;
}

export function SharedNotesPage({ title, initialNotePage }: ProjectPageProps) {
  const { projects, inboxProject } = useOutletContext() as ProjectOutletContext
  const fetcher = useFetcher()
  const location = useLocation()
  const { projectId } = useParams() as { projectId: string }
  const isInbox = location.pathname.includes(ROUTER_PATHS.INBOX.path)

  let curProjectId, actionPath, projectName
  if (isInbox) {
    actionPath = ROUTER_PATHS.INBOX.path
    projectName = ROUTER_PATHS.INBOX.name
    curProjectId = inboxProject?.projectId
  } else {
    actionPath = `${ROUTER_PATHS.PROJECTS.path}/${projectId}`
    projectName = projects.find(project => project.projectId === projectId)?.name as string
    curProjectId = projectId
  }

  const [selectedProjectId, setSelectedProjectId] = useState(curProjectId)
  const [editorContent, setEditorContent] = useState('')
  const [hasEditorContent, setHasEditorContent] = useState(false)

  const [notes, setNotes] = useState(initialNotePage.records)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const [uuid, setUuid] = useState('')
  const editorRef = useRef<EditorMethods>(null)

  // 避免执行多余请求
  const [hasMore, setHasMore] = useState(initialNotePage.total > initialNotePage.records.length)
  const observer = useRef<IntersectionObserver>()

  // Infinite scroll logic using IntersectionObserver
  const lastNoteElementRef = useCallback((node: HTMLElement | null) => {
    if (loading) {
      return
    }

    if (observer.current) {
      observer.current.disconnect()
    }

    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore) {
        setPage(prevPage => prevPage + 1)
      }
    })

    if (node) {
      observer.current.observe(node)
    }
  }, [loading, hasMore])

  // /app/projects/:projectId 路由之间切换更新数据
  useEffect(() => {
    if (projectId) {
      setNotes(initialNotePage.records)
    }
  }, [projectId, initialNotePage])

  // Effect to load more notes when page changes
  useEffect(() => {
    // Skip the initial load handled by the loader
    if (page === 1) {
      return
    }

    setLoading(true)
    getNotesApi({
      objectId: projectId,
      pageSize: 10,
      pageIndex: page,
    }, isInbox).then((response) => {
      if (!response.ok) {
        throw json(response.data, { status: response.status })
      }
      const data = response.data

      setNotes(prevNotes => [...prevNotes, ...data.records])
      // 总数小于当前页数乘以10，说明没有更多数据
      setHasMore(data.total > page * 10)
      setLoading(false)
    })
  }, [isInbox, page, projectId])

  // 清空编辑器内容
  useEffect(() => {
    if (fetcher.formData && fetcher.state === 'submitting') {
      const tempUuid = uuidv4()
      setUuid(tempUuid)
      const content = fetcher.formData.get('content') as string
      const optimisticNote: FullNote = {
        noteId: tempUuid,
        content,
        addedAt: new Date().toISOString(),
        labels: [],
        project: { projectId: curProjectId, name: projectName },
        attachmentCount: null,
        referencedCount: null,
        referencingCount: null,
        updatedAt: new Date().toISOString(),
        isDeleted: 0,
        isPinned: 0,
        isArchived: 0,
        creator: '',
      }

      // 在前端更新乐观 UI（显示临时 noteId）
      setNotes(prevNotes => [optimisticNote, ...prevNotes])
    }

    if (fetcher.state === 'idle' && fetcher.data) {
      const res = fetcher.data as { ok: boolean, note: string }
      if (res.ok) {
        const updatedNotes = notes.map((note) => {
          if (note.noteId === uuid) {
            return { ...note, noteId: res.note }
          }
          return note
        })

        setNotes(updatedNotes)
        handleContentChange('', false)
        editorRef.current?.reset()
      } else {
        // 回滚数据
        const failedNote = notes.find(note => note.noteId === uuid)
        if (failedNote) {
          handleContentChange(failedNote.content, true)
          editorRef.current?.setContent(failedNote.content)

          const filteredNotes = notes.filter(n => n.noteId !== uuid)
          setNotes(filteredNotes)
        }
      }
    }
  }, [fetcher])

  function handleContentChange(content: string, hasContent: boolean) {
    setEditorContent(content)
    setHasEditorContent(hasContent)
  }

  return (
    <div className="flex flex-col">
      <h1 className="text-2xl mb-4">{projectName ?? title}</h1>
      <TextEditor ref={editorRef} className="mb-4" onChange={handleContentChange} />
      <fetcher.Form method="post" action={actionPath} className="mb-4">
        <input type="hidden" name="content" value={editorContent} />
        <div className="flex">
          <Select name="projectId" value={selectedProjectId} onValueChange={setSelectedProjectId}>
            <SelectTrigger className="w-[280px]">
              <SelectValue placeholder="选择一个项目" />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                {/* <SelectLabel>收件箱</SelectLabel> */}
                <SelectItem value={inboxProject?.projectId as string}>
                  <div className="flex items-center gap-3">
                    <Inbox className="h-4 w-4" />
                    <span>{inboxProject?.name}</span>
                  </div>
                </SelectItem>
              </SelectGroup>
              <SelectGroup>
                {/* <SelectLabel>项目</SelectLabel> */}
                {projects.map(project => (
                  <SelectItem
                    value={project.projectId}
                    key={project.projectId}
                    className="hover:bg-gray-400"
                  >
                    <div className="flex items-center gap-3">
                      <HashIcon className="h-4 w-4 group-[.active]:text-blue-500"
                        style={{ color: `var(--named-color-${project.color.replace('_', '-')})` }}
                      />
                      <span>{project.name}</span>
                      {/* <span className="ml-auto bg-transparent text-muted-foreground flex h-6 w-6 shrink-0 items-center justify-center"> */}
                      {/*   {project.noteCount ?? 0} */}
                      {/* </span> */}
                    </div>
                  </SelectItem>
                ))}
              </SelectGroup>
            </SelectContent>
          </Select>
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

      <NoteList notes={notes} refFunc={lastNoteElementRef} />
    </div>
  )
}

