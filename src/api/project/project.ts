import { request } from '@/lib/http'

export type Project = {
  name: string;
  projectId: string;
  noteCount: number;
  isFavorite: 0 | 1;
  color: string;
}

export function getProjectListApi() {
  return request<Project[]>({
    url: '/api/v1/projects',
    method: 'get',
  })
}
