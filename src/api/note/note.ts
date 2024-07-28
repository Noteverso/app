import { request } from '@/lib/http'
import type { FullNote } from '@/types/note'

export function getNotesApi() {
  return request<FullNote[]>({
    url: '/api/v1/notes',
    method: 'get',
  })
}
