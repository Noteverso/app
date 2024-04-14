import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { ROUTER_PATHS } from '@/routes/path'

export default function AppMain() {
  const auth = useAuth()
  const location = useLocation()
  if (!auth?.isLogin) {
    return <Navigate to={ROUTER_PATHS.LOGIN_PATH} state={{ from: location }} replace />
  }

  return (
    <div>
      <Outlet />
    </div>
  )
}
