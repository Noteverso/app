import { getProjectListApi } from '@/api/project'

export async function layoutLoader() {
  const projectList = await getProjectListApi()
  return {
    projectList,
  }
}

