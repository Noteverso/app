import { Navigate, useLoaderData, useLocation } from 'react-router-dom'
import { AppMain } from './main'
import { Sidebar } from './siderbar'
import { useAuth } from '@/hooks/useAuth'
import { ROUTER_PATHS } from '@/routes/path'

export function layoutLoader() {
  return {
    projectIds: ['1', '2', '3'],
  }
}

export function Layout() {
  const { projectIds } = useLoaderData() as { projectIds: string[] }
  const auth = useAuth()
  const location = useLocation()
  if (!auth?.isLogin) {
    return <Navigate to={ROUTER_PATHS.LOGIN_PATH} state={{ from: location }} replace />
  }

  return (
    <div>
      <Sidebar projectIds={projectIds} />
      <AppMain />
    </div>
  )
}
