import { request } from '@/lib/http'
import type { FullProject, NewProject } from '@/types/project'

/**
 * Get all projects for the current user
 * @returns List of projects with note counts
 */
export function getProjectsApi() {
  return request<FullProject[]>({
    url: '/api/v1/projects',
    method: 'get',
    params: {
      showNoteCount: true,
    },
  })
}

/**
 * Create a new project
 * @param data - Project data (name, color, isFavorite)
 * @returns The created project ID
 */
export async function createProjectApi(data: NewProject) {
  const response = await request<string>({
    url: '/api/v1/projects',
    method: 'post',
    data: {
      name: data.name,
      color: data.color,
      isFavorite: data.isFavorite || 0,
    },
  })
  
  if (!response.ok) {
    throw new Error('Failed to create project')
  }
  
  return response.data
}

/**
 * Update an existing project
 * @param id - Project ID
 * @param data - Partial project data to update
 */
export async function updateProjectApi(id: string, data: Partial<NewProject>) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}`,
    method: 'patch',
    data: {
      name: data.name,
      color: data.color,
      isFavorite: data.isFavorite,
    },
  })
  
  if (!response.ok) {
    throw new Error('Failed to update project')
  }
}

/**
 * Delete a project
 * @param id - Project ID
 */
export async function deleteProjectApi(id: string) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}`,
    method: 'delete',
  })
  
  if (!response.ok) {
    throw new Error('Failed to delete project')
  }
}

/**
 * Archive a project
 * @param id - Project ID
 */
export async function archiveProjectApi(id: string) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}/archive`,
    method: 'patch',
  })
  
  if (!response.ok) {
    throw new Error('Failed to archive project')
  }
}

/**
 * Unarchive a project
 * @param id - Project ID
 */
export async function unarchiveProjectApi(id: string) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}/unarchive`,
    method: 'patch',
  })
  
  if (!response.ok) {
    throw new Error('Failed to unarchive project')
  }
}

/**
 * Mark a project as favorite
 * @param id - Project ID
 */
export async function favoriteProjectApi(id: string) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}/favorite`,
    method: 'patch',
  })
  
  if (!response.ok) {
    throw new Error('Failed to favorite project')
  }
}

/**
 * Remove favorite status from a project
 * @param id - Project ID
 */
export async function unfavoriteProjectApi(id: string) {
  const response = await request<void>({
    url: `/api/v1/projects/${id}/unfavorite`,
    method: 'patch',
  })
  
  if (!response.ok) {
    throw new Error('Failed to unfavorite project')
  }
}
