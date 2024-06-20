// import { getProjectListApi } from '@/api/project'

export async function layoutLoader() {
  // const projectList = await getProjectListApi()
  const projectList = [
    { name: 'Project 1', projectId: '1', noteCount: 0, isFavorite: 0, color: 'red' },
    { name: 'Project 2', projectId: '2', noteCount: 0, isFavorite: 0, color: 'green' },
    { name: 'Project 3', projectId: '3', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 4', projectId: '4', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 5', projectId: '5', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 6', projectId: '6', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 7', projectId: '7', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 8', projectId: '8', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 9', projectId: '9', noteCount: 0, isFavorite: 0, color: 'blue' },
    { name: 'Project 10', projectId: '10', noteCount: 0, isFavorite: 0, color: 'blue' },
  ]
  return {
    projectList,
  }
}

