import { request } from '@/lib/http'
import type { NotePageLoaderData, NotePageRequestParams } from '@/types/note'

// 获取项目笔记列表
export function getNotesApi(params: NotePageRequestParams) {
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
