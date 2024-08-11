import { request } from '@/lib/http'
import type { NotePageLoaderData, NotePageRequestParams } from '@/types/note'

// 获取笔记列表
export function getNotesApi(params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: '/api/v1/notes/project',
    method: 'get',
    params,
  })
}
