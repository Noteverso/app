import { request } from '@/lib/http'
import type { NewNote, NotePageLoaderData, NotePageRequestParams, UpdateNotePayload } from '@/types/note'

export function getNotesApi(params: NotePageRequestParams, isInbox = false) {
  if (isInbox) {
    return getInboxNotesApi(params)
  }

  return getProjectNotesApi(params)
}

// 获取项目笔记列表
export function getProjectNotesApi(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: `/api/v1/projects/${params.objectId}/notes`,
    method: 'get',
    params,
  })
}

// 获取收件箱笔记列表
export function getInboxNotesApi(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: '/api/v1/projects/inbox/notes',
    method: 'get',
    params,
  })
}

// 新增笔记
export function addNote(newNote: NewNote) {
  return request<string>({
    url: '/api/v1/notes',
    method: 'post',
    data: newNote,
  })
}

// 更新笔记
export function updateNoteApi(noteId: string, note: UpdateNotePayload) {
  return request<void>({
    url: `/api/v1/notes/${noteId}`,
    method: 'patch',
    data: note,
  })
}

// 软删除笔记（移入回收站）
export function moveNoteToTrashApi(noteId: string) {
  return request<void>({
    url: `/api/v1/notes/${noteId}`,
    method: 'delete',
  })
}

// 搜索笔记
export interface SearchNotesParams {
  keyword?: string
  labelIds?: string[]
  status?: number
  startDate?: string
  endDate?: string
  sortBy?: string
  sortOrder?: string
  pageIndex?: number
  pageSize?: number
}

export function searchNotesApi(params: SearchNotesParams) {
  return request<NotePageLoaderData>({
    url: '/api/v1/notes/search',
    method: 'get',
    params,
  })
}
