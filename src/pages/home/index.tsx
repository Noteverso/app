import { NavLink } from 'react-router-dom'
import { ROUTER_PATHS } from '@/routes/path'
export function Home() {
  return <div>
    <ul className="flex gap-x-2">
      <li>
        <NavLink to={ROUTER_PATHS.SIGNUP.path}>{ROUTER_PATHS.SIGNUP.name}</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.LOGIN.path}>{ROUTER_PATHS.LOGIN.name}</NavLink>
      </li>
      <li>
        <NavLink to={ROUTER_PATHS.INBOX.path}>{ROUTER_PATHS.INBOX.name}</NavLink>
      </li>
    </ul>
    <div>
      <h1>Home</h1>
    </div>
  </div>
}
