import { NavLink, useFetcher } from 'react-router-dom'
import { ROUTER_PATHS } from '@/routes/path'
import type { Project } from '@/api/project'

export type SidebarProprs = {
  projectList: Project[];
}

export function Sidebar({ projectList }: SidebarProprs) {
  const fetcher = useFetcher()

  return <div>
    <ul className="flex gap-x-2">
      <li>
        <NavLink to={ROUTER_PATHS.INBOX.path}>{ROUTER_PATHS.INBOX.name}</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.LABELS.path}>{ROUTER_PATHS.LABELS.name}</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.ATTACHMENTS.path}>{ROUTER_PATHS.ATTACHMENTS.name}</NavLink>
      </li>
      <li>
        <fetcher.Form method="post" action={ROUTER_PATHS.LOGOUT.path}>
          <button>{ROUTER_PATHS.LOGOUT.name}</button>
        </fetcher.Form>
      </li>
      {
        projectList.map(project => <li key={project.projectId}>
          <NavLink to={`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`}>{project.name}</NavLink>
        </li>)
      }
    </ul>
  </div>
}
