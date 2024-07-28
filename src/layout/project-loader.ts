import { getProjectsApi } from '@/api/project/project'

export async function projectLoader() {
  const projectList = await getProjectsApi()
  // for (let i = 0; i < 50; i++) {
  //   projectList.push({
  //     name: `Project ${i + 1}`,
  //     projectId: `${i + 1}`,
  //     noteCount: 0,
  //     isFavorite: 0,
  //     color: 'blue',
  //   })
  // }
  return {
    projectList,
  }
}

