import { NavLink } from 'react-router-dom'

export default function Sidebar() {
  return <div>
    <ul>
      <li>
        <NavLink to="/home">Home</NavLink>
      </li>
    </ul>
  </div>
}
