import { Outlet } from 'react-router-dom'

export function Main() {
  return (
    <div className="app-main px-4 lg:px-6">
      <div className="app-main__content">
        <Outlet />
      </div>
    </div>
  )
}
