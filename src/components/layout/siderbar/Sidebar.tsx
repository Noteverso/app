import { NavLink } from 'react-router-dom'
export type SidebarProprs = {
  projectIds: string[];
}

export default function Sidebar({ projectIds }: SidebarProprs) {
  return <div>
    <ul>
      <li>
        <NavLink to="/app/inbox">inbox</NavLink>
      </li>
      <li>
        <NavLink to="/app/labels">labels</NavLink>
      </li>
      <li>
        <NavLink to="/app/attachments">attachments</NavLink>
      </li>
      {
        projectIds.map(projectId => <li key={projectId}>
          <NavLink to={`/app/projects/${projectId}`}>{projectId}</NavLink>
        </li>)
      }
    </ul>
  </div>
}
