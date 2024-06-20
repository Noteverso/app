import { Outlet } from 'react-router-dom'

export function Main() {
  return (
    <div className="app-main">
      <div className="app-main__content">
        <Outlet />
      </div>
    </div>
  )
}
