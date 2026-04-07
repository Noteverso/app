import { ROUTER_PATHS } from '@/constants'
import type { NoteNavigationHint } from '@/types/note'

export type SharedNotesRouteKind = 'inbox' | 'project' | 'other'

type RouteProjectReference = {
  projectId: string;
  name: string;
  inboxProject: boolean;
}

export function getProjectIdFromPathname(pathname: string) {
  const prefix = `${ROUTER_PATHS.PROJECTS.path}/`
  if (!pathname.startsWith(prefix)) {
    return undefined
  }

  const [projectId] = pathname.slice(prefix.length).split('/')
  return projectId || undefined
}

export function resolveSharedNotesRouteContext(
  pathname: string,
  projectId: string | undefined,
  projects: RouteProjectReference[],
  inboxProject: { projectId: string } | null | undefined,
  title?: string,
) {
  if (pathname === ROUTER_PATHS.INBOX.path || pathname.startsWith(`${ROUTER_PATHS.INBOX.path}/`)) {
    return {
      routeKind: 'inbox' as const,
      actionPath: ROUTER_PATHS.INBOX.path,
      projectName: ROUTER_PATHS.INBOX.name,
      currentProjectId: inboxProject?.projectId || '',
      defaultSelectedProjectId: inboxProject?.projectId || '',
      supportsScopedCreateBehavior: true,
    }
  }

  if (projectId) {
    return {
      routeKind: 'project' as const,
      actionPath: `${ROUTER_PATHS.PROJECTS.path}/${projectId}`,
      projectName: projects.find(project => project.projectId === projectId)?.name || '',
      currentProjectId: projectId,
      defaultSelectedProjectId: projectId,
      supportsScopedCreateBehavior: true,
    }
  }

  return {
    routeKind: 'other' as const,
    actionPath: pathname,
    projectName: title || '',
    currentProjectId: '',
    defaultSelectedProjectId: inboxProject?.projectId || '',
    supportsScopedCreateBehavior: false,
  }
}

export function shouldShowCreatedNoteNavigationHint(currentProjectId: string, selectedProjectId: string) {
  return Boolean(currentProjectId) && currentProjectId !== selectedProjectId
}

export function buildNoteNavigationHint(
  targetProjectId: string,
  projects: RouteProjectReference[],
  inboxProject: RouteProjectReference | null | undefined,
  fallbackProjectName = '',
): NoteNavigationHint {
  const targetProject = projects.find(project => project.projectId === targetProjectId)
  const targetIsInbox = targetProjectId === inboxProject?.projectId || Boolean(targetProject?.inboxProject)

  return {
    projectId: targetProjectId,
    projectName: targetIsInbox
      ? ROUTER_PATHS.INBOX.name
      : targetProject?.name || fallbackProjectName || targetProjectId,
    routePath: targetIsInbox
      ? ROUTER_PATHS.INBOX.path
      : `${ROUTER_PATHS.PROJECTS.path}/${targetProjectId}`,
  }
}

export function buildCreatedNoteNavigationHint(
  currentProjectId: string,
  targetProjectId: string,
  projects: RouteProjectReference[],
  inboxProject: RouteProjectReference | null | undefined,
  fallbackProjectName = '',
): NoteNavigationHint | null {
  if (!shouldShowCreatedNoteNavigationHint(currentProjectId, targetProjectId)) {
    return null
  }

  return buildNoteNavigationHint(targetProjectId, projects, inboxProject, fallbackProjectName)
}

export function getApiErrorMessage(
  response: { data?: { error?: { message?: string; payload?: unknown } } } | undefined,
  fallbackMessage: string,
) {
  const payload = response?.data?.error?.payload
  if (payload && typeof payload === 'object') {
    if ('error' in payload) {
      const payloadError = (payload as { error?: unknown }).error
      if (payloadError && typeof payloadError === 'object' && 'message' in payloadError && typeof payloadError.message === 'string') {
        return payloadError.message
      }
    }

    if ('message' in payload && typeof payload.message === 'string') {
      return payload.message
    }
  }

  return response?.data?.error?.message || fallbackMessage
}
