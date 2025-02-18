import { request } from '@/lib/http'
import type { FullProject } from '@/types/project'

export function getProjectsApi() {
  return request<FullProject[]>({
    url: '/api/v1/projects',
    method: 'get',
    params: {
      showNoteCount: true,
    },
  })
}
