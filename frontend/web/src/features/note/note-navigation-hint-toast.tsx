import { useCallback, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { ToastAction } from '@/components/ui/toast/toast'
import { type useToast } from '@/components/ui/toast/use-toast'
import type { NoteNavigationHint } from '@/types/note'

type ToastInvoker = ReturnType<typeof useToast>['toast']

export function getCreatedNoteNavigationToastTitle(projectName: string) {
  return `笔记已创建在 ${projectName}`
}

export const CREATED_NOTE_NAVIGATION_TOAST_DURATION = 10000
export const CREATED_NOTE_NAVIGATION_TOAST_CLASS_NAME = 'w-auto max-w-[24rem] items-center gap-2 border-slate-900 bg-slate-900 p-3 pr-9 text-white shadow-lg'
export const CREATED_NOTE_NAVIGATION_TOAST_CONTENT_CLASS_NAME = 'min-w-0 flex-1'
export const CREATED_NOTE_NAVIGATION_TOAST_TITLE_CLASS_NAME = 'truncate whitespace-nowrap text-xs font-medium leading-none text-white'
export const CREATED_NOTE_NAVIGATION_TOAST_ACTION_CLASS_NAME = 'h-7 shrink-0 border-white/15 px-2 text-xs text-white hover:bg-white/10 hover:text-white focus:ring-white/30 focus:ring-offset-slate-900'
export const CREATED_NOTE_NAVIGATION_TOAST_CLOSE_CLASS_NAME = 'text-white/60 hover:text-white focus:text-white focus:ring-white/30 focus:ring-offset-slate-900'

export function useCreatedNoteNavigationHintToast(pathname: string, toast: ToastInvoker) {
  const navigate = useNavigate()
  const activeNavigationToastDismissRef = useRef<(() => void) | null>(null)

  const dismissNavigationHintToast = useCallback(() => {
    activeNavigationToastDismissRef.current?.()
    activeNavigationToastDismissRef.current = null
  }, [])

  const showNavigationHintToast = useCallback((hint: NoteNavigationHint | null) => {
    dismissNavigationHintToast()
    if (!hint) {
      return
    }

    let dismissToast: (() => void) | null = null
    const toastHandle = toast({
      duration: CREATED_NOTE_NAVIGATION_TOAST_DURATION,
      className: CREATED_NOTE_NAVIGATION_TOAST_CLASS_NAME,
      contentClassName: CREATED_NOTE_NAVIGATION_TOAST_CONTENT_CLASS_NAME,
      titleClassName: CREATED_NOTE_NAVIGATION_TOAST_TITLE_CLASS_NAME,
      closeClassName: CREATED_NOTE_NAVIGATION_TOAST_CLOSE_CLASS_NAME,
      title: getCreatedNoteNavigationToastTitle(hint.projectName),
      action: (
        <ToastAction
          className={CREATED_NOTE_NAVIGATION_TOAST_ACTION_CLASS_NAME}
          altText={`Open ${hint.projectName}`}
          onClick={() => {
            dismissToast?.()
            navigate(hint.routePath)
          }}
        >
          打开项目
        </ToastAction>
      ),
    })

    dismissToast = toastHandle.dismiss
    activeNavigationToastDismissRef.current = toastHandle.dismiss
  }, [dismissNavigationHintToast, navigate, toast])

  useEffect(() => {
    dismissNavigationHintToast()
  }, [dismissNavigationHintToast, pathname])

  useEffect(() => dismissNavigationHintToast, [dismissNavigationHintToast])

  return {
    dismissNavigationHintToast,
    showNavigationHintToast,
  }
}
