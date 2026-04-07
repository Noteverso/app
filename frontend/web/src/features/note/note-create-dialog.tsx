import { type FormEvent, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { AtSign, HashIcon, Plus } from 'lucide-react'
import { v4 as uuidv4 } from 'uuid'
import { TextEditor } from '@/features/editor'
import type { EditorMethods, QuickActionQuery, QuickActionTokenClickPayload } from '@/features/editor/text-editor'
import type { QuickActionTokenAttrs } from '@/features/editor/quick-action-token'
import { extractQuickTokenBinding } from '@/pages/shared-notes-page/quick-action-binding'
import { sanitizeQuickActionContentJson } from '@/pages/shared-notes-page/quick-action-sanitize'
import type { FullProject } from '@/types/project'
import type { SelectItem as LabelSelectItem } from '@/types/label'
import { addNote } from '@/api/note/note'
import { createLabelApi, getLabelSelectItemsApi } from '@/api/label/label'
import { createProjectApi } from '@/api/project/project'
import { PROJECT_COLORS } from '@/constants/project-constants'
import { ROUTER_PATHS } from '@/constants'
import { Button } from '@/components/ui/button/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog/dialog'
import { useToast } from '@/components/ui/toast/use-toast'
import {
  buildNoteNavigationHint,
  buildCreatedNoteNavigationHint,
  getApiErrorMessage,
  getProjectIdFromPathname,
  resolveSharedNotesRouteContext,
} from './note-create-utils'
import { useCreatedNoteNavigationHintToast } from './note-navigation-hint-toast'

type NoteCreateDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  projects: FullProject[];
  inboxProject: FullProject | null | undefined;
  pathname: string;
  refetchProjects: () => void;
  upsertProject: (project: FullProject) => void;
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

const QUICK_ACTION_MENU_WIDTH = 320
const QUICK_ACTION_MENU_HEIGHT = 256
const QUICK_ACTION_MENU_VIEWPORT_PADDING = 12
const QUICK_ACTION_MENU_OFFSET = 8

const LABEL_COLORS = ['#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16', '#22c55e', '#10b981', '#14b8a6', '#06b6d4', '#0ea5e9', '#3b82f6', '#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899']

function getEmptyEditorContent() {
  return {}
}

function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max)
}

export function NoteCreateDialog({
  open,
  onOpenChange,
  projects,
  inboxProject,
  pathname,
  refetchProjects,
  upsertProject,
}: NoteCreateDialogProps) {
  const { toast } = useToast()
  const editorRef = useRef<EditorMethods>(null)
  const overlayRootRef = useRef<HTMLDivElement>(null)
  const quickSuggestionPointerHandledRef = useRef(false)
  const [editorContentJson, setEditorContentJson] = useState<object>(getEmptyEditorContent())
  const [hasEditorContent, setHasEditorContent] = useState(false)
  const [editorInstanceKey, setEditorInstanceKey] = useState(0)
  const [isSaving, setIsSaving] = useState(false)
  const [quickActionQuery, setQuickActionQuery] = useState<QuickActionQuery | null>(null)
  const [quickActionSource, setQuickActionSource] = useState<'typed' | 'icon' | 'token' | null>(null)
  const [quickActionIndex, setQuickActionIndex] = useState(0)
  const [quickActionAnchor, setQuickActionAnchor] = useState<QuickActionAnchor | null>(null)
  const [targetTokenId, setTargetTokenId] = useState<string | null>(null)
  const [labelOptions, setLabelOptions] = useState<LabelSelectItem[]>([])

  const availableProjects = useMemo(() => (
    inboxProject
      ? [inboxProject, ...projects.filter(project => project.projectId !== inboxProject.projectId)]
      : projects
  ), [inboxProject, projects])
  const routeContext = useMemo(
    () => resolveSharedNotesRouteContext(pathname, getProjectIdFromPathname(pathname), availableProjects, inboxProject),
    [availableProjects, inboxProject, pathname],
  )
  const { showNavigationHintToast } = useCreatedNoteNavigationHintToast(pathname, toast)
  const normalizedEditorContentJson = useMemo(
    () => sanitizeQuickActionContentJson(editorContentJson),
    [editorContentJson],
  )
  const quickTokenBinding = useMemo(
    () => extractQuickTokenBinding(normalizedEditorContentJson),
    [normalizedEditorContentJson],
  )

  useEffect(() => {
    if (!open) {
      return
    }

    getLabelSelectItemsApi().then((response) => {
      if (response.ok) {
        setLabelOptions(response.data)
      }
    })
  }, [open])

  const resetDraft = useCallback(() => {
    setEditorContentJson(getEmptyEditorContent())
    setHasEditorContent(false)
    setIsSaving(false)
    setQuickActionQuery(null)
    setQuickActionSource(null)
    setQuickActionIndex(0)
    setQuickActionAnchor(null)
    setTargetTokenId(null)
    editorRef.current?.reset()
    setEditorInstanceKey(prev => prev + 1)
  }, [])

  const quickSuggestions = useMemo(() => {
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
        name: project.inboxProject ? ROUTER_PATHS.INBOX.name : project.name,
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
  }, [availableProjects, labelOptions, quickActionQuery])

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
  }, [quickActionSource, quickSuggestions, targetTokenId, toast, upsertProject])

  const triggerQuickSuggestionSelection = useCallback((index: number) => {
    applyQuickSuggestion(index).catch(() => undefined)
  }, [applyQuickSuggestion])

  const markQuickSuggestionPointerHandled = useCallback(() => {
    quickSuggestionPointerHandledRef.current = true
    window.setTimeout(() => {
      quickSuggestionPointerHandledRef.current = false
    }, 0)
  }, [])

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
  }, [quickActionIndex, quickActionQuery, quickSuggestions.length, triggerQuickSuggestionSelection])

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
    setQuickActionAnchor(editorRef.current?.getCursorAnchor() || null)
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
    const availableBelow = viewportHeight - quickActionAnchor.bottom - QUICK_ACTION_MENU_VIEWPORT_PADDING
    const availableAbove = quickActionAnchor.top - QUICK_ACTION_MENU_VIEWPORT_PADDING
    const left = clamp(
      quickActionAnchor.left,
      QUICK_ACTION_MENU_VIEWPORT_PADDING,
      viewportWidth - QUICK_ACTION_MENU_WIDTH - QUICK_ACTION_MENU_VIEWPORT_PADDING,
    )
    const preferTop = availableBelow < QUICK_ACTION_MENU_HEIGHT && availableAbove > availableBelow
    const top = preferTop
      ? quickActionAnchor.top - QUICK_ACTION_MENU_HEIGHT - QUICK_ACTION_MENU_OFFSET
      : quickActionAnchor.bottom + QUICK_ACTION_MENU_OFFSET
    const clampedTop = clamp(
      top,
      QUICK_ACTION_MENU_VIEWPORT_PADDING,
      viewportHeight - QUICK_ACTION_MENU_HEIGHT - QUICK_ACTION_MENU_VIEWPORT_PADDING,
    )
    const overlayRootRect = overlayRootRef.current?.getBoundingClientRect()

    return {
      left: overlayRootRect ? left - overlayRootRect.left : left,
      top: overlayRootRect ? clampedTop - overlayRootRect.top : clampedTop,
    }
  }, [quickActionAnchor, quickActionQuery, quickSuggestions.length])

  const handleDialogOpenChange = useCallback((nextOpen: boolean) => {
    if (!nextOpen && isSaving) {
      return
    }

    if (!nextOpen) {
      resetDraft()
    }

    onOpenChange(nextOpen)
  }, [isSaving, onOpenChange, resetDraft])

  const handleSubmit = useCallback(async (event?: FormEvent) => {
    event?.preventDefault()
    if (!hasEditorContent || isSaving) {
      return
    }

    const targetProjectId = quickTokenBinding.projectId || routeContext.defaultSelectedProjectId
    if (!targetProjectId) {
      toast({ title: '笔记创建失败', variant: 'destructive' })
      return
    }

    setIsSaving(true)
    const response = await addNote({
      contentJson: quickTokenBinding.sanitizedContentJson,
      projectId: targetProjectId,
      labels: quickTokenBinding.labelIds,
    }).catch(() => ({
      ok: false as const,
      status: 500,
      data: {
        error: {
          message: '笔记创建失败',
        },
      },
    }))

    if (!response.ok) {
      setIsSaving(false)
      toast({ title: getApiErrorMessage(response, '笔记创建失败'), variant: 'destructive' })
      return
    }
    const navigationHint = routeContext.supportsScopedCreateBehavior
      ? buildCreatedNoteNavigationHint(
          routeContext.currentProjectId,
          targetProjectId,
          availableProjects,
          inboxProject,
        )
      : buildNoteNavigationHint(
          targetProjectId,
          availableProjects,
          inboxProject,
        )

    setIsSaving(false)
    resetDraft()
    onOpenChange(false)
    refetchProjects()
    showNavigationHintToast(navigationHint)
  }, [
    availableProjects,
    hasEditorContent,
    inboxProject,
    isSaving,
    onOpenChange,
    quickTokenBinding.labelIds,
    quickTokenBinding.projectId,
    quickTokenBinding.sanitizedContentJson,
    refetchProjects,
    resetDraft,
    routeContext.currentProjectId,
    routeContext.defaultSelectedProjectId,
    routeContext.supportsScopedCreateBehavior,
    showNavigationHintToast,
    toast,
  ])

  const quickActionMenu = dropdownStyle && quickActionQuery && quickSuggestions.length > 0
    ? (
        <div
          data-testid="note-create-quick-action-menu"
          className="absolute z-20 flex w-80 flex-col overflow-hidden rounded-lg border border-slate-200 bg-white p-2 shadow-xl"
          style={{ left: dropdownStyle.left, top: dropdownStyle.top, height: QUICK_ACTION_MENU_HEIGHT, width: QUICK_ACTION_MENU_WIDTH }}
        >
          <div className="mb-2 shrink-0 px-2 text-xs font-medium text-slate-500">
            {quickActionQuery.type === 'project' ? 'Projects' : 'Labels'}
          </div>
          <div data-testid="note-create-quick-action-menu-list" className="min-h-0 flex-1 space-y-1 overflow-y-auto">
            {quickSuggestions.map((suggestion, index) => (
              <button
                key={suggestion.id}
                type="button"
                className={`flex w-full items-center justify-between rounded-md px-2 py-2 text-left text-sm transition-colors ${
                  index === quickActionIndex ? 'bg-slate-100 text-slate-900' : 'text-slate-700 hover:bg-slate-50'
                }`}
                onPointerDown={(pointerEvent) => {
                  pointerEvent.preventDefault()
                  markQuickSuggestionPointerHandled()
                  triggerQuickSuggestionSelection(index)
                }}
                onClick={() => {
                  if (quickSuggestionPointerHandledRef.current) {
                    return
                  }

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
      )
    : null

  return (
    <Dialog open={open} onOpenChange={handleDialogOpenChange}>
      <DialogContent className="top-[4vh] max-h-[92vh] translate-y-0 overflow-visible sm:max-w-[720px]">
        <div ref={overlayRootRef} className="relative">
          <div data-testid="note-create-dialog-scroll-area" className="max-h-[84vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>新增笔记</DialogTitle>
              <DialogDescription>在当前上下文中快速记录一条笔记。</DialogDescription>
            </DialogHeader>
            <form className="space-y-4" onSubmit={handleSubmit}>
              <TextEditor
                key={editorInstanceKey}
                ref={editorRef}
                onChange={(contentJson, nextHasContent) => {
                  setEditorContentJson(contentJson)
                  setHasEditorContent(nextHasContent)
                }}
                onQuickActionQuery={handleEditorQuickActionQuery}
                onQuickActionKeyDown={handleQuickActionKeyDown}
                onQuickActionIconClick={handleQuickActionIconClick}
                onQuickActionTokenClick={handleQuickActionTokenClick}
              />
              <DialogFooter>
                <Button type="button" variant="secondary" onClick={() => handleDialogOpenChange(false)} disabled={isSaving}>
                  取消
                </Button>
                <Button type="submit" className="bg-slate-900 text-white hover:bg-slate-800" disabled={!hasEditorContent || isSaving}>
                  {isSaving ? '保存中...' : '保存'}
                </Button>
              </DialogFooter>
            </form>
          </div>
          {quickActionMenu}
        </div>
      </DialogContent>
    </Dialog>
  )
}
