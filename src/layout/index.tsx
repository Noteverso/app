import { useLoaderData } from 'react-router-dom'
import { Main } from './main/main'
import { Sidebar } from './siderbar/sidebar'
import { Project } from '@/api/project'

export function Layout() {
  const layoutLoaderData = useLoaderData() as { projectList: Project[] }

  return (
    <div>
      <Sidebar projectList={layoutLoaderData.projectList} />
      <Main />
    </div>
  )
}
