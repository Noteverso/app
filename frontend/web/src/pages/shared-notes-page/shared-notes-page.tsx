import { json, useFetcher, useLocation, useOutletContext, useParams } from 'react-router-dom'
import { type FormEvent, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { AtSign, HashIcon, PanelLeft, Plus } from 'lucide-react'
import { v4 as uuidv4 } from 'uuid'
import { extractQuickTokenBinding } from './quick-action-binding'
import { NoteList } from '@/features/note'
import { TextEditor } from '@/features/editor'
import { Button } from '@/components/ui/button/button'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog/alert-dialog'
import { ROUTER_PATHS } from '@/constants'
import type { FullNote, NoteListItem, NoteNavigationHint, NotePageLoaderData } from '@/types/note'
import { getNotesApi, moveNoteToTrashApi, updateNoteApi } from '@/api/note/note'
import type { ProjectOutletContext } from '@/types/project'
import type { EditorMethods, QuickActionQuery, QuickActionTokenClickPayload } from '@/features/editor/text-editor'
import { createLabelApi, getLabelSelectItemsApi } from '@/api/label/label'
import type { SelectItem as LabelSelectItem } from '@/types/label'
import { createProjectApi } from '@/api/project/project'
import { PROJECT_COLORS } from '@/constants/project-constants'
import { useToast } from '@/components/ui/toast/use-toast'
import type { QuickActionTokenAttrs } from '@/features/editor/quick-action-token'
import {
  buildCreatedNoteNavigationHint,
  getApiErrorMessage,
  resolveSharedNotesRouteContext,
  shouldShowCreatedNoteNavigationHint,
} from '@/features/note/note-create-utils'
import {
  useCreatedNoteNavigationHintToast,
  getCreatedNoteNavigationToastTitle,
} from '@/features/note/note-navigation-hint-toast'

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
  rawContentJson: object;
  sanitizedContentJson: object;
  hasEditorContent: boolean;
  labels: FullNote['labels'];
  project: FullNote['project'];
  navigationHint: NoteNavigationHint | null;
  shouldInsertOptimisticNote: boolean;
}

const LABEL_COLORS = ['#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16', '#22c55e', '#10b981', '#14b8a6', '#06b6d4', '#0ea5e9', '#3b82f6', '#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899']

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
  return currentState === 'idle'
    && (previousState === 'submitting' || previousState === 'loading')
}

export function reconcileOptimisticNote(
  notes: NoteListItem[],
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

export function shouldKeepOptimisticNoteInCurrentList(currentProjectId: string, selectedProjectId: string) {
  return !currentProjectId || currentProjectId === selectedProjectId
}

export function isCreateSubmitDisabled(fetcherState: string, hasEditorContent: boolean) {
  return fetcherState !== 'idle' || !hasEditorContent
}

export function mergeSameRouteNoteRecords(
  existingNotes: NoteListItem[],
  incomingRecords: NoteListItem[],
) {
  const incomingIds = new Set(incomingRecords.map(note => note.noteId))
  const preservedExistingNotes = existingNotes.filter(note => !incomingIds.has(note.noteId))
  return [...incomingRecords, ...preservedExistingNotes]
}

export function createSavedNoteListItem(
  activeSave: OptimisticSaveContext,
  noteId: string,
  addedAt = new Date().toISOString(),
): NoteListItem {
  return {
    noteId,
    contentJson: activeSave.sanitizedContentJson,
    addedAt,
    labels: activeSave.labels,
    project: activeSave.project,
    attachmentCount: null,
    referencedCount: null,
    referencingCount: null,
    updatedAt: addedAt,
    isDeleted: 0,
    isPinned: 0,
    isArchived: 0,
    creator: '',
  }
}

function createQuickActionTokenNode(tokenType: 'project' | 'label', entityId: string, label: string) {
  return {
    type: 'quickActionToken',
    attrs: {
      tokenId: `${tokenType}-${entityId}`,
      tokenType,
      entityId,
      label,
    },
  }
}

export function buildEditorContentWithQuickBindings(note: Pick<NoteListItem, 'contentJson' | 'project' | 'labels'>) {
  const { sanitizedContentJson, projectId, labelIds } = extractQuickTokenBinding(note.contentJson || {})
  const doc = sanitizedContentJson as { type?: string; content?: Array<{ type?: string; content?: unknown[] }> }
  const nextContent = Array.isArray(doc.content) ? [...doc.content] : []
  const firstParagraph = nextContent[0]?.type === 'paragraph' && Array.isArray(nextContent[0].content)
    ? { ...nextContent[0], content: [...(nextContent[0].content as unknown[])] }
    : { type: 'paragraph', content: [] as unknown[] }

  const tokenNodes: unknown[] = []
  const noteProject = note.project ?? { projectId: '', name: '' }
  const noteLabels = note.labels ?? []
  const effectiveProjectId = projectId || noteProject.projectId
  if (effectiveProjectId) {
    const effectiveProjectName = noteProject.name || ''
    tokenNodes.push(createQuickActionTokenNode('project', effectiveProjectId, effectiveProjectName))
  }

  for (const label of noteLabels) {
    if (!labelIds.includes(label.labelId)) {
      tokenNodes.push(createQuickActionTokenNode('label', label.labelId, label.name))
    }
  }

  if (tokenNodes.length === 0) {
    return note.contentJson || {}
  }

  firstParagraph.content = [...tokenNodes, ...firstParagraph.content]

  if (nextContent.length === 0) {
    nextContent.push(firstParagraph)
  } else {
    nextContent[0] = firstParagraph
  }

  return {
    ...doc,
    type: doc.type || 'doc',
    content: nextContent,
  }
}

function getEmptyEditorContent() {
  return {}
}

export { getCreatedNoteNavigationToastTitle }
export { shouldShowCreatedNoteNavigationHint }

export function SharedNotesPage({ title, initialNotePage }: ProjectPageProps) {
  const { projects, inboxProject, refetchProjects, upsertProject, isSidebarVisible, onToggleSidebar } = useOutletContext() as SharedNotesOutletContext
  const { toast } = useToast()
  const fetcher = useFetcher()
  const location = useLocation()
  const { projectId } = useParams() as { projectId: string }
  const routeContext = useMemo(
    () => resolveSharedNotesRouteContext(location.pathname, projectId, projects, inboxProject, title),
    [inboxProject, location.pathname, projectId, projects, title],
  )
  const {
    actionPath,
    currentProjectId: curProjectId,
    defaultSelectedProjectId,
    projectName,
    routeKind,
    supportsScopedCreateBehavior,
  } = routeContext
  const isInbox = routeKind === 'inbox'

  const [selectedProjectId, setSelectedProjectId] = useState(defaultSelectedProjectId)
  const [quickActionQuery, setQuickActionQuery] = useState<QuickActionQuery | null>(null)
  const [quickActionSource, setQuickActionSource] = useState<'typed' | 'icon' | 'token' | null>(null)
  const [quickActionIndex, setQuickActionIndex] = useState(0)
  const [quickActionAnchor, setQuickActionAnchor] = useState<QuickActionAnchor | null>(null)
  const [targetTokenId, setTargetTokenId] = useState<string | null>(null)
  const [labelOptions, setLabelOptions] = useState<LabelSelectItem[]>([])

  const [editorContentJson, setEditorContentJson] = useState<object>({})
  const [hasEditorContent, setHasEditorContent] = useState(false)
  const [editorInstanceKey, setEditorInstanceKey] = useState(0)

  const [notes, setNotes] = useState<NoteListItem[]>(initialNotePage.records)
  const [editingNote, setEditingNote] = useState<NoteListItem | null>(null)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [pendingDeleteNote, setPendingDeleteNote] = useState<NoteListItem | null>(null)
  const [isUpdatingNote, setIsUpdatingNote] = useState(false)
  const [isDeletingNote, setIsDeletingNote] = useState(false)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const editorRef = useRef<EditorMethods>(null)
  const previousFetcherStateRef = useRef(fetcher.state)
  const activeOptimisticSaveRef = useRef<OptimisticSaveContext | null>(null)
  const previousRoutePathRef = useRef(location.pathname)
  const { dismissNavigationHintToast, showNavigationHintToast } = useCreatedNoteNavigationHintToast(location.pathname, toast)

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

  const restoreComposerState = useCallback((contentJson: object, nextHasEditorContent: boolean) => {
    setEditorContentJson(contentJson)
    setHasEditorContent(nextHasEditorContent)

    const bindings = extractQuickTokenBinding(contentJson)
    setSelectedProjectId(bindings.projectId || defaultSelectedProjectId)
    setQuickActionQuery(null)
    setQuickActionSource(null)
    setQuickActionAnchor(null)
    setTargetTokenId(null)
  }, [defaultSelectedProjectId])

  useEffect(() => {
    const routeChanged = previousRoutePathRef.current !== location.pathname
    let nextNotes = initialNotePage.records

    if (routeChanged) {
      setNotes(initialNotePage.records)
      setPage(1)
      setHasMore(initialNotePage.total > initialNotePage.records.length)
      previousRoutePathRef.current = location.pathname
      return
    }

    setNotes((prevNotes) => {
      nextNotes = mergeSameRouteNoteRecords(prevNotes, initialNotePage.records)
      return nextNotes
    })
    setHasMore(initialNotePage.total > nextNotes.length)
    previousRoutePathRef.current = location.pathname
  }, [initialNotePage, location.pathname])

  const availableProjects = useMemo(() => (
    inboxProject
      ? [inboxProject, ...projects.filter(project => project.projectId !== inboxProject.projectId)]
      : projects
  ), [projects, inboxProject])

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
    setSelectedProjectId(defaultSelectedProjectId)
    setQuickActionQuery(null)
    setQuickActionSource(null)
    setQuickActionAnchor(null)
    setTargetTokenId(null)
  }, [defaultSelectedProjectId])

  const exitEditMode = useCallback(() => {
    setEditingNote(null)
    resetComposerState()
    editorRef.current?.reset()
  }, [resetComposerState])

  const handleEditNote = useCallback((note: NoteListItem) => {
    const editContentJson = buildEditorContentWithQuickBindings(note)
    setEditingNote(note)
    restoreComposerState(editContentJson, Boolean(editContentJson && JSON.stringify(editContentJson) !== '{}'))
    editorRef.current?.setContentJson(editContentJson)
  }, [restoreComposerState])

  const handleRequestDeleteNote = useCallback((note: NoteListItem) => {
    setPendingDeleteNote(note)
    setDeleteDialogOpen(true)
  }, [])

  const handleCreateFormSubmit = useCallback((event: FormEvent<HTMLFormElement>) => {
    if (fetcher.state !== 'idle') {
      event.preventDefault()
    }
  }, [fetcher.state])

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
      const sanitizedContentJson = JSON.parse(fetcher.formData.get('contentJson') as string)
      const targetProjectId = (fetcher.formData.get('projectId') as string) || defaultSelectedProjectId
      const targetLabelIds = JSON.parse((fetcher.formData.get('labels') as string) || '[]') as string[]
      const targetProject = availableProjects.find(project => project.projectId === targetProjectId)
      const projectLabel = targetProject?.inboxProject
        ? ROUTER_PATHS.INBOX.name
        : targetProject?.name || projectName || title || ROUTER_PATHS.INBOX.name
      const shouldInsertNote = supportsScopedCreateBehavior
        ? shouldKeepOptimisticNoteInCurrentList(curProjectId, targetProjectId)
        : true
      const nextActiveSave: OptimisticSaveContext = {
        tempNoteId,
        rawContentJson: editorContentJson,
        sanitizedContentJson,
        hasEditorContent,
        labels: labelOptions
          .filter(label => targetLabelIds.includes(label.value))
          .map(label => ({ labelId: label.value, name: label.name })),
        project: {
          projectId: targetProjectId,
          name: targetProject?.name || projectName || title || ROUTER_PATHS.INBOX.name,
        },
        navigationHint: supportsScopedCreateBehavior
          ? buildCreatedNoteNavigationHint(curProjectId, targetProjectId, availableProjects, inboxProject, projectLabel)
          : null,
        shouldInsertOptimisticNote: shouldInsertNote,
      }
      activeOptimisticSaveRef.current = nextActiveSave
      dismissNavigationHintToast()
      resetComposerState()
      editorRef.current?.reset()

      if (shouldInsertNote) {
        setNotes(prevNotes => [createSavedNoteListItem(nextActiveSave, tempNoteId), ...prevNotes])
      }
    } else if (isSubmitEnd) {
      const activeSave = activeOptimisticSaveRef.current
      if (!activeSave) {
        previousFetcherStateRef.current = currentState
        return
      }

      const res = fetcher.data as { ok: boolean, note: string } | undefined
      if (res?.ok) {
        if (activeSave.shouldInsertOptimisticNote) {
          setNotes(prevNotes => reconcileOptimisticNote(prevNotes, activeSave, res))
          dismissNavigationHintToast()
        } else {
          showNavigationHintToast(activeSave.navigationHint)
        }
        refetchProjects()
        setEditorInstanceKey(prev => nextEditorInstanceKeyOnSave(prev, res))
      } else {
        if (activeSave.shouldInsertOptimisticNote) {
          setNotes(prevNotes => reconcileOptimisticNote(prevNotes, activeSave))
        }
        restoreComposerState(activeSave.rawContentJson || {}, activeSave.hasEditorContent)
        editorRef.current?.setContentJson(activeSave.rawContentJson)
      }

      activeOptimisticSaveRef.current = null
    }
    previousFetcherStateRef.current = currentState
  }, [
    availableProjects,
    curProjectId,
    defaultSelectedProjectId,
    editorContentJson,
    fetcher.data,
    fetcher.formData,
    fetcher.state,
    hasEditorContent,
    labelOptions,
    projectName,
    dismissNavigationHintToast,
    refetchProjects,
    resetComposerState,
    restoreComposerState,
    showNavigationHintToast,
    supportsScopedCreateBehavior,
    title,
  ])

  function handleContentChange(contentJson: object, hasContent: boolean) {
    setEditorContentJson(contentJson)
    setHasEditorContent(hasContent)

    const bindings = extractQuickTokenBinding(contentJson)
    if (bindings.projectId) {
      setSelectedProjectId(bindings.projectId)
    } else {
      setSelectedProjectId(editingNote?.project.projectId || defaultSelectedProjectId)
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
  }, [quickActionQuery?.type, quickActionQuery?.keyword, quickSuggestions.length])

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

        upsertProject({
          projectId: response,
          name: suggestion.name,
          color: PROJECT_COLORS[0].value,
          noteCount: 0,
          isFavorite: 0,
          inboxProject: false,
        })
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
  }, [quickSuggestions, toast, quickActionSource, targetTokenId, upsertProject])

  const triggerQuickSuggestionSelection = useCallback((index: number) => {
    applyQuickSuggestion(index).catch(() => undefined)
  }, [applyQuickSuggestion])

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
      triggerQuickSuggestionSelection(quickActionIndex)
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
  }, [quickActionQuery, quickSuggestions, quickActionIndex, triggerQuickSuggestionSelection])

  const effectiveProjectId = selectedProjectId || editingNote?.project.projectId || defaultSelectedProjectId || ''
  const quickTokenBinding = useMemo(() => extractQuickTokenBinding(editorContentJson), [editorContentJson])
  const sanitizedEditorContentJson = quickTokenBinding.sanitizedContentJson

  const handleUpdateNote = useCallback(async () => {
    if (!editingNote || isUpdatingNote || !hasEditorContent) {
      return
    }

    const targetProjectId = quickTokenBinding.projectId || selectedProjectId || editingNote.project.projectId || defaultSelectedProjectId || undefined
    const resolvedProjectId = targetProjectId || editingNote.project.projectId
    const targetProject = resolvedProjectId
      ? availableProjects.find(project => project.projectId === resolvedProjectId)
      : undefined
    const targetProjectName = targetProject?.name || editingNote.project.name
    const targetLabels = labelOptions
      .filter(label => quickTokenBinding.labelIds.includes(label.value))
      .map(label => ({ labelId: label.value, name: label.name }))

    setIsUpdatingNote(true)
    const response = await updateNoteApi(editingNote.noteId, {
      contentJson: sanitizedEditorContentJson,
      labels: quickTokenBinding.labelIds,
      ...(targetProjectId ? { projectId: targetProjectId } : {}),
      linkedNotes: [],
      files: [],
    })
    setIsUpdatingNote(false)

    if (!response.ok) {
      toast({ title: getApiErrorMessage(response, 'Failed to update note'), variant: 'destructive' })
      return
    }

    const shouldRemoveFromCurrentList = supportsScopedCreateBehavior
      && Boolean(curProjectId && resolvedProjectId)
      && curProjectId !== resolvedProjectId

    if (shouldRemoveFromCurrentList) {
      setNotes(prevNotes => prevNotes.filter(note => note.noteId !== editingNote.noteId))
      toast({ title: `Note moved to ${targetProjectName}` })
    } else {
      const updatedAt = new Date().toISOString()
      setNotes(prevNotes => prevNotes.map(note => (
        note.noteId === editingNote.noteId
          ? {
              ...note,
              contentJson: sanitizedEditorContentJson,
              updatedAt,
              labels: targetLabels,
              project: {
                projectId: resolvedProjectId,
                name: targetProjectName,
              },
            }
          : note
      )))
      toast({ title: 'Note updated' })
    }

    exitEditMode()
  }, [
    availableProjects,
    curProjectId,
    defaultSelectedProjectId,
    editingNote,
    exitEditMode,
    hasEditorContent,
    isUpdatingNote,
    labelOptions,
    quickTokenBinding.labelIds,
    quickTokenBinding.projectId,
    sanitizedEditorContentJson,
    selectedProjectId,
    supportsScopedCreateBehavior,
    toast,
  ])

  const handleConfirmDelete = useCallback(async () => {
    if (!pendingDeleteNote || isDeletingNote) {
      return
    }

    setIsDeletingNote(true)
    const response = await moveNoteToTrashApi(pendingDeleteNote.noteId)
    setIsDeletingNote(false)

    if (!response.ok) {
      toast({ title: 'Failed to delete note', variant: 'destructive' })
      return
    }

    setNotes(prevNotes => prevNotes.filter(note => note.noteId !== pendingDeleteNote.noteId))
    if (editingNote?.noteId === pendingDeleteNote.noteId) {
      exitEditMode()
    }
    setDeleteDialogOpen(false)
    setPendingDeleteNote(null)
    toast({ title: 'Note moved to trash' })
  }, [editingNote?.noteId, exitEditMode, isDeletingNote, pendingDeleteNote, toast])
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
    const estimatedHeight = Math.min(280, Math.max(120, 40 + quickSuggestions.length * 40))
    const availableBelow = viewportHeight - quickActionAnchor.bottom - 12
    const availableAbove = quickActionAnchor.top - 12
    const left = Math.max(12, Math.min(quickActionAnchor.left, viewportWidth - panelWidth - 12))
    const preferTop = availableBelow < 120 && availableAbove > availableBelow
    const top = preferTop
      ? Math.max(12, quickActionAnchor.top - Math.min(estimatedHeight, availableAbove) - 8)
      : Math.max(12, quickActionAnchor.bottom + 8)
    const maxHeight = preferTop
      ? Math.max(96, availableAbove - 8)
      : Math.max(96, availableBelow - 8)

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
      <fetcher.Form method="post" action={actionPath} className="relative mb-5" onSubmit={handleCreateFormSubmit}>
        {editingNote && (
          <div className="mb-3 flex items-center justify-between rounded-md border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">
            <span>Editing note</span>
            <Button type="button" variant="ghost" size="sm" onClick={exitEditMode}>Cancel</Button>
          </div>
        )}
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
              {editingNote && (
                <Button type="button" variant="ghost" onClick={exitEditMode} disabled={isUpdatingNote}>
                  Cancel
                </Button>
              )}
              <Button
                type={editingNote ? 'button' : 'submit'}
                name="note-save"
                value="save"
                className="bg-slate-900 text-white hover:bg-slate-800"
                onClick={editingNote ? () => { void handleUpdateNote() } : undefined}
                disabled={editingNote ? (isUpdatingNote || !hasEditorContent) : isCreateSubmitDisabled(fetcher.state, hasEditorContent)}>
                {editingNote ? '更新' : '保存'}
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
                    triggerQuickSuggestionSelection(index)
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

      <NoteList notes={notes} refFunc={lastNoteElementRef} onEdit={handleEditNote} onDelete={handleRequestDeleteNote} />

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>删除这条笔记？</AlertDialogTitle>
            <AlertDialogDescription>
              删除后笔记将移入回收站，你可以稍后恢复。
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeletingNote}>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={() => { void handleConfirmDelete() }} disabled={isDeletingNote}>Delete</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
