import { Outlet } from 'react-router-dom'

export function Main() {
  return (
    <div className="app-main-wrapper">
      <div className="app-main-content">
        <Outlet />
      </div>
    </div>
  )
}
