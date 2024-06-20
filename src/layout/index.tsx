import { useLoaderData } from 'react-router-dom'
import { Main } from './main/main'
import { Nav } from './nav/nav'
import { Project } from '@/api/project'

export function Layout() {
  const layoutLoaderData = useLoaderData() as { projectList: Project[] }

  return (
    <div className="app-layout">
      <Nav projectList={layoutLoaderData.projectList} />
      <Main />
    </div>
  )
}
