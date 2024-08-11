import { json } from 'react-router-dom'
import { getProjectsApi } from '@/api/project/project'

export async function projectLoader() {
  const response = await getProjectsApi()
  if (!response.ok) {
    throw json(response.data, { status: response.status })
  }

  return response.data
}

