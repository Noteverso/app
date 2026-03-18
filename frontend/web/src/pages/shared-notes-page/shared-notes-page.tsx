import { json, useFetcher, useLocation, useOutletContext, useParams } from 'react-router-dom'
import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { AtSign, HashIcon, PanelLeft, Plus } from 'lucide-react'
import { v4 as uuidv4 } from 'uuid'
import { NoteList } from '@/features/note'
import { TextEditor } from '@/features/editor'
import { Button } from '@/components/ui/button/button'
import { ROUTER_PATHS } from '@/constants'
import type { FullNote, NotePageLoaderData } from '@/types/note'
import { getNotesApi } from '@/api/note/note'
import type { ProjectOutletContext } from '@/types/project'
import type { EditorMethods, QuickActionQuery, QuickActionTokenClickPayload } from '@/features/editor/text-editor'
import { getLabelSelectItemsApi, createLabelApi } from '@/api/label/label'
import type { SelectItem as LabelSelectItem } from '@/types/label'
import { createProjectApi } from '@/api/project/project'
import { PROJECT_COLORS } from '@/constants/project-constants'
import { useToast } from '@/components/ui/toast/use-toast'
import type { QuickActionTokenAttrs } from '@/features/editor/quick-action-token'
import { extractQuickTokenBinding } from './quick-action-binding'

interface ProjectPageProps {
  title?: string;
  initialNotePage: NotePageLoaderData;
}

type SharedNotesOutletContext = ProjectOutletContext & {
  isSidebarVisible: boolean;
  onToggleSidebar: () => void;
}

type QuickSuggestion = {
  id: string;
  type: 'existing' | 'create';
  actionType: 'project' | 'label';
  name: string;
  color: string;
}

type QuickActionAnchor = {
  left: number;
  top: number;
  bottom: number;
}

type OptimisticSaveContext = {
  tempNoteId: string;
  contentJson: object;
}

export function shouldInsertOptimisticNote(
  previousState: string,
  currentState: string,
  hasFormData: boolean,
  hasActiveOptimisticSave: boolean,
) {
  return previousState !== 'submitting'
    && currentState === 'submitting'
    && hasFormData
    && !hasActiveOptimisticSave
}

export function shouldFinalizeOptimisticNote(previousState: string, currentState: string) {
  return previousState === 'submitting' && currentState === 'idle'
}

export function reconcileOptimisticNote(
  notes: FullNote[],
  activeSave: OptimisticSaveContext,
  result?: { ok: boolean; note: string },
) {
  if (result?.ok) {
    return notes.map(note => (
      note.noteId === activeSave.tempNoteId
        ? { ...note, noteId: result.note }
        : note
    ))
  }

  return notes.filter(note => note.noteId !== activeSave.tempNoteId)
}

export function nextEditorInstanceKeyOnSave(currentKey: number, saveResult?: { ok: boolean; note: string }) {
  return saveResult?.ok ? currentKey + 1 : currentKey
}

function getEmptyEditorContent() {
  return {}
}

export function SharedNotesPage({ title, initialNotePage }: ProjectPageProps) {
  const { projects, inboxProject, isSidebarVisible, onToggleSidebar } = useOutletContext() as SharedNotesOutletContext
  const { toast } = useToast()
  const fetcher = useFetcher()
  const location = useLocation()
  const { projectId } = useParams() as { projectId: string }
  const isInbox = location.pathname.includes(ROUTER_PATHS.INBOX.path)

  let curProjectId = ''
  let actionPath = ''
  let projectName = ''
  if (isInbox) {
    actionPath = ROUTER_PATHS.INBOX.path
    projectName = ROUTER_PATHS.INBOX.name
    curProjectId = inboxProject?.projectId || ''
  } else {
    actionPath = `${ROUTER_PATHS.PROJECTS.path}/${projectId}`
    projectName = projects.find(project => project.projectId === projectId)?.name || ''
    curProjectId = projectId
  }

  const [selectedProjectId, setSelectedProjectId] = useState(curProjectId)
  const [quickActionQuery, setQuickActionQuery] = useState<QuickActionQuery | null>(null)
  const [quickActionSource, setQuickActionSource] = useState<'typed' | 'icon' | 'token' | null>(null)
  const [quickActionIndex, setQuickActionIndex] = useState(0)
  const [quickActionAnchor, setQuickActionAnchor] = useState<QuickActionAnchor | null>(null)
  const [targetTokenId, setTargetTokenId] = useState<string | null>(null)
  const [availableProjects, setAvailableProjects] = useState<{
    projectId: string;
    name: string;
    color: string;
    noteCount: number;
    isFavorite: 0 | 1;
    inboxProject: boolean;
  }[]>([])
  const [labelOptions, setLabelOptions] = useState<LabelSelectItem[]>([])

  const [editorContentJson, setEditorContentJson] = useState<object>({})
  const [hasEditorContent, setHasEditorContent] = useState(false)
  const [editorInstanceKey, setEditorInstanceKey] = useState(0)

  const [notes, setNotes] = useState(initialNotePage.records)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const editorRef = useRef<EditorMethods>(null)
  const previousFetcherStateRef = useRef(fetcher.state)
  const activeOptimisticSaveRef = useRef<OptimisticSaveContext | null>(null)
  const LABEL_COLORS = ['#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16', '#22c55e', '#10b981', '#14b8a6', '#06b6d4', '#0ea5e9', '#3b82f6', '#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899']

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

  useEffect(() => {
    const incoming = inboxProject ? [inboxProject, ...projects.filter(project => project.projectId !== inboxProject.projectId)] : projects
    setAvailableProjects(prev => {
      const createdOnly = prev.filter(project => !incoming.some(incomingProject => incomingProject.projectId === project.projectId))
      return [...incoming, ...createdOnly]
    })
  }, [projects, inboxProject])

  useEffect(() => {
    setSelectedProjectId(curProjectId)
  }, [curProjectId])

  useEffect(() => {
    getLabelSelectItemsApi().then((response) => {
      if (response.ok) {
        setLabelOptions(response.data)
      }
    })
  }, [])

  const resetComposerState = useCallback(() => {
    setEditorContentJson(getEmptyEditorContent())
    setHasEditorContent(false)
    setSelectedProjectId(curProjectId)
    setQuickActionQuery(null)
    setQuickActionSource(null)
    setQuickActionAnchor(null)
    setTargetTokenId(null)
  }, [curProjectId])

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

  // 提交状态机：开始时插入一次乐观笔记，结束时替换临时 ID 或回滚
  useEffect(() => {
    const previousState = previousFetcherStateRef.current
    const currentState = fetcher.state
    const isSubmitStart = shouldInsertOptimisticNote(
      previousState,
      currentState,
      Boolean(fetcher.formData),
      Boolean(activeOptimisticSaveRef.current),
    )
    const isSubmitEnd = shouldFinalizeOptimisticNote(previousState, currentState)

    if (isSubmitStart && fetcher.formData) {
      const tempNoteId = uuidv4()
      const contentJson = JSON.parse(fetcher.formData.get('contentJson') as string)
      const optimisticProjectId = (fetcher.formData.get('projectId') as string) || curProjectId
      const optimisticLabelIds = JSON.parse((fetcher.formData.get('labels') as string) || '[]') as string[]
      const optimisticNote: FullNote = {
        noteId: tempNoteId,
        contentJson,
        addedAt: new Date().toISOString(),
        labels: labelOptions
          .filter(label => optimisticLabelIds.includes(label.value))
          .map(label => ({ labelId: label.value, name: label.name })),
        project: {
          projectId: optimisticProjectId,
          name: availableProjects.find(project => project.projectId === optimisticProjectId)?.name || projectName,
        },
        attachmentCount: null,
        referencedCount: null,
        referencingCount: null,
        updatedAt: new Date().toISOString(),
        isDeleted: 0,
        isPinned: 0,
        isArchived: 0,
        creator: '',
      }

      activeOptimisticSaveRef.current = {
        tempNoteId,
        contentJson,
      }
      setNotes(prevNotes => [optimisticNote, ...prevNotes])
    } else if (isSubmitEnd) {
      const activeSave = activeOptimisticSaveRef.current
      if (!activeSave) {
        previousFetcherStateRef.current = currentState
        return
      }

      const res = fetcher.data as { ok: boolean, note: string } | undefined
      if (res?.ok) {
        setNotes(prevNotes => reconcileOptimisticNote(prevNotes, activeSave, res))
        resetComposerState()
        editorRef.current?.reset()
        setEditorInstanceKey(prev => nextEditorInstanceKeyOnSave(prev, res))
      } else {
        handleContentChange(activeSave.contentJson || {}, true)
        editorRef.current?.setContentJson(activeSave.contentJson)
        setNotes(prevNotes => reconcileOptimisticNote(prevNotes, activeSave))
      }

      activeOptimisticSaveRef.current = null
    }
    previousFetcherStateRef.current = currentState
  }, [availableProjects, curProjectId, fetcher.data, fetcher.formData, fetcher.state, labelOptions, projectName, resetComposerState])

  function handleContentChange(contentJson: object, hasContent: boolean) {
    setEditorContentJson(contentJson)
    setHasEditorContent(hasContent)

    const bindings = extractQuickTokenBinding(contentJson)
    if (bindings.projectId) {
      setSelectedProjectId(bindings.projectId)
    } else {
      setSelectedProjectId(curProjectId)
    }
  }

  const quickSuggestions = useMemo<QuickSuggestion[]>(() => {
    if (!quickActionQuery) {
      return []
    }

    const keyword = quickActionQuery.keyword.trim().toLowerCase()
    if (quickActionQuery.type === 'project') {
      const matches = availableProjects.filter(project =>
        project.name.toLowerCase().includes(keyword),
      )

      const suggestions: QuickSuggestion[] = matches.map(project => ({
        id: project.projectId,
        type: 'existing' as const,
        actionType: 'project' as const,
        name: project.name,
        color: project.color,
      }))

      const hasExact = matches.some(project => project.name.toLowerCase() === keyword)
      if (keyword && !hasExact) {
        suggestions.unshift({
          id: `create-project-${keyword}`,
          type: 'create' as const,
          actionType: 'project' as const,
          name: quickActionQuery.keyword.trim(),
          color: PROJECT_COLORS[0].value,
        })
      }

      return suggestions
    }

    const matches = labelOptions.filter(label =>
      label.name.toLowerCase().includes(keyword),
    )

    const suggestions: QuickSuggestion[] = matches.map(label => ({
      id: label.value,
      type: 'existing' as const,
      actionType: 'label' as const,
      name: label.name,
      color: label.color || LABEL_COLORS[0],
    }))

    const hasExact = matches.some(label => label.name.toLowerCase() === keyword)
    if (keyword && !hasExact) {
      suggestions.unshift({
        id: `create-label-${keyword}`,
        type: 'create' as const,
        actionType: 'label' as const,
        name: quickActionQuery.keyword.trim(),
        color: LABEL_COLORS[0],
      })
    }

    return suggestions
  }, [quickActionQuery, availableProjects, labelOptions])

  useEffect(() => {
    setQuickActionIndex(0)
  }, [quickActionQuery?.type, quickActionQuery?.keyword])

  const applyQuickSuggestion = useCallback(async (index: number) => {
    const suggestion = quickSuggestions[index]
    if (!suggestion) {
      return
    }
    let resolvedEntityId = ''

    if (suggestion.actionType === 'project') {
      if (suggestion.type === 'existing') {
        setSelectedProjectId(suggestion.id)
        resolvedEntityId = suggestion.id
      } else {
        const response = await createProjectApi({
          name: suggestion.name,
          color: PROJECT_COLORS[0].value,
          noteCount: 0,
          isFavorite: 0,
        }).catch(() => '')

        if (!response) {
          toast({ title: 'Failed to create project', variant: 'destructive' })
          return
        }

        setAvailableProjects(prev => [...prev, {
          projectId: response,
          name: suggestion.name,
          color: PROJECT_COLORS[0].value,
          noteCount: 0,
          isFavorite: 0,
          inboxProject: false,
        }])
        setSelectedProjectId(response)
        resolvedEntityId = response
      }
    } else if (suggestion.type === 'existing') {
      resolvedEntityId = suggestion.id
    } else {
      const response = await createLabelApi({
        name: suggestion.name,
        color: LABEL_COLORS[0],
      })

      if (!response.ok) {
        toast({ title: 'Failed to create label', variant: 'destructive' })
        return
      }

      setLabelOptions(prev => [...prev, {
        value: response.data,
        name: suggestion.name,
        color: LABEL_COLORS[0],
      }])
      resolvedEntityId = response.data
    }

    const tokenPayload: QuickActionTokenAttrs = {
      tokenId: targetTokenId ?? uuidv4(),
      tokenType: suggestion.actionType,
      entityId: resolvedEntityId,
      label: suggestion.name,
    }

    if (quickActionSource === 'typed') {
      editorRef.current?.consumeQuickActionToken()
      editorRef.current?.insertQuickActionToken(tokenPayload)
    } else if (quickActionSource === 'token' && targetTokenId) {
      editorRef.current?.replaceQuickActionToken(targetTokenId, tokenPayload)
    } else {
      editorRef.current?.insertQuickActionToken(tokenPayload)
    }

    setQuickActionQuery(null)
    setQuickActionAnchor(null)
    setTargetTokenId(null)
  }, [quickSuggestions, toast, quickActionSource, targetTokenId])

  const handleQuickActionKeyDown = useCallback((event: globalThis.KeyboardEvent) => {
    if (!quickActionQuery || quickSuggestions.length === 0) {
      return false
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault()
      setQuickActionIndex(prev => (prev + 1) % quickSuggestions.length)
      return true
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      setQuickActionIndex(prev => (prev - 1 + quickSuggestions.length) % quickSuggestions.length)
      return true
    }

    if (event.key === 'Enter') {
      event.preventDefault()
      void applyQuickSuggestion(quickActionIndex)
      return true
    }

    if (event.key === 'Escape') {
      event.preventDefault()
      setQuickActionQuery(null)
      setQuickActionAnchor(null)
      setTargetTokenId(null)
      return true
    }

    return false
  }, [quickActionQuery, quickSuggestions, quickActionIndex, applyQuickSuggestion])

  const effectiveProjectId = selectedProjectId || curProjectId || ''
  const quickTokenBinding = useMemo(() => extractQuickTokenBinding(editorContentJson), [editorContentJson])
  const sanitizedEditorContentJson = quickTokenBinding.sanitizedContentJson
  const handleEditorQuickActionQuery = useCallback((query: QuickActionQuery | null) => {
    setQuickActionSource(query ? 'typed' : null)
    setQuickActionQuery(query)
    if (!query) {
      setQuickActionAnchor(null)
      setTargetTokenId(null)
      return
    }

    const cursorAnchor = editorRef.current?.getCursorAnchor() || null
    setQuickActionAnchor(cursorAnchor)
    setTargetTokenId(null)
  }, [])

  const handleQuickActionIconClick = useCallback((type: 'project' | 'label') => {
    setQuickActionSource('icon')
    setQuickActionIndex(0)
    const cursorAnchor = editorRef.current?.getCursorAnchor() || null
    setQuickActionAnchor(cursorAnchor)
    setTargetTokenId(null)
    setQuickActionQuery({
      type,
      keyword: '',
      token: type === 'project' ? '#' : '@',
    })
  }, [])

  const handleQuickActionTokenClick = useCallback((payload: QuickActionTokenClickPayload) => {
    setQuickActionSource('token')
    setQuickActionIndex(0)
    setQuickActionAnchor({
      left: payload.rect.left,
      top: payload.rect.top,
      bottom: payload.rect.bottom,
    })
    setTargetTokenId(payload.tokenId)
    setQuickActionQuery({
      type: payload.tokenType,
      keyword: payload.label,
      token: payload.tokenType === 'project' ? `#${payload.label}` : `@${payload.label}`,
    })
  }, [])

  const dropdownStyle = useMemo(() => {
    if (!quickActionAnchor || !quickActionQuery || quickSuggestions.length === 0) {
      return null
    }

    const viewportHeight = window.innerHeight
    const viewportWidth = window.innerWidth
    const panelWidth = 320
    const estimatedHeight = 280
    const left = Math.max(12, Math.min(quickActionAnchor.left, viewportWidth - panelWidth - 12))
    const preferTop = quickActionAnchor.bottom + estimatedHeight > viewportHeight - 12
    const top = preferTop
      ? Math.max(12, quickActionAnchor.top - estimatedHeight - 8)
      : Math.min(viewportHeight - estimatedHeight - 12, quickActionAnchor.bottom + 8)
    const maxHeight = preferTop
      ? Math.max(120, quickActionAnchor.top - 24)
      : Math.max(120, viewportHeight - quickActionAnchor.bottom - 24)

    return {
      left,
      top,
      maxHeight,
    }
  }, [quickActionAnchor, quickActionQuery, quickSuggestions.length])

  return (
    <div className="flex flex-col">
      <div className="mt-2 mb-5 flex items-center gap-3">
        {!isSidebarVisible && (
          <Button
            type="button"
            variant="ghost"
            size="icon"
            className="hidden h-8 w-8 md:inline-flex"
            onClick={onToggleSidebar}
            aria-label="Toggle navigation menu"
          >
            <PanelLeft className="h-4 w-4" />
          </Button>
        )}
        <h1 className="text-2xl">{projectName ?? title}</h1>
      </div>
      <fetcher.Form method="post" action={actionPath} className="relative mb-5">
        <input type="hidden" name="contentJson" value={JSON.stringify(sanitizedEditorContentJson)} />
        <input type="hidden" name="labels" value={JSON.stringify(quickTokenBinding.labelIds)} />
        <input type="hidden" name="projectId" value={quickTokenBinding.projectId || effectiveProjectId} />
        <TextEditor
          key={editorInstanceKey}
          ref={editorRef}
          className="mb-3"
          onChange={handleContentChange}
          onQuickActionQuery={handleEditorQuickActionQuery}
          onQuickActionKeyDown={handleQuickActionKeyDown}
          onQuickActionIconClick={handleQuickActionIconClick}
          onQuickActionTokenClick={handleQuickActionTokenClick}
          footer={(
            <div className="flex items-center justify-end">
              <Button
                type="submit"
                name="note-save"
                value="save"
                className="bg-slate-900 text-white hover:bg-slate-800"
                disabled={fetcher.state === 'submitting' || (!hasEditorContent && fetcher.state === 'idle')}>
                保存
              </Button>
            </div>
          )}
        />
        {dropdownStyle && quickActionQuery && quickSuggestions.length > 0 && (
          <div
            className="fixed z-50 w-80 overflow-auto rounded-lg border border-slate-200 bg-white p-2 shadow-xl"
            style={{ left: dropdownStyle.left, top: dropdownStyle.top, maxHeight: dropdownStyle.maxHeight }}
          >
            <div className="mb-2 px-2 text-xs font-medium text-slate-500">
              {quickActionQuery.type === 'project' ? 'Projects' : 'Labels'}
            </div>
            <div className="space-y-1">
              {quickSuggestions.map((suggestion, index) => (
                <button
                  key={suggestion.id}
                  type="button"
                  className={`flex w-full items-center justify-between rounded-md px-2 py-2 text-left text-sm transition-colors ${
                    index === quickActionIndex ? 'bg-slate-100 text-slate-900' : 'text-slate-700 hover:bg-slate-50'
                  }`}
                  onMouseDown={(event) => {
                    event.preventDefault()
                    void applyQuickSuggestion(index)
                  }}
                >
                  <span className="flex items-center gap-2">
                    {suggestion.actionType === 'project'
                      ? <HashIcon className="h-4 w-4 text-slate-500" />
                      : <AtSign className="h-4 w-4 text-slate-500" />
                    }
                    {suggestion.name}
                  </span>
                  {suggestion.type === 'create' && (
                    <span className="flex items-center gap-1 text-xs text-slate-500">
                      <Plus className="h-3 w-3" /> Create
                    </span>
                  )}
                </button>
              ))}
            </div>
          </div>
        )}
      </fetcher.Form>

      <NoteList notes={notes} refFunc={lastNoteElementRef} />
    </div>
  )
}
