import { NavLink } from 'react-router-dom'
import { ROUTER_PATHS } from '@/routes/path'
export type SidebarProprs = {
  projectIds: string[];
}

export default function Sidebar({ projectIds }: SidebarProprs) {
  return <div>
    <ul>
      <li>
        <NavLink to={ROUTER_PATHS.INBOX_PATH}>inbox</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.LABELS_PATH}>labels</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.ATTACHMENTS_PATH}>attachments</NavLink>
      </li>
      {
        projectIds.map(projectId => <li key={projectId}>
          <NavLink to={`${ROUTER_PATHS.PROJECTS_PATH}/${projectId}`}>{projectId}</NavLink>
        </li>)
      }
    </ul>
  </div>
}
