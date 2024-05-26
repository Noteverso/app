import { Navigate, useLocation } from 'react-router-dom'
import { ReactNode } from 'react'
import { useAuth } from '@/hooks/useAuth'
import { ROUTER_PATHS } from '@/routes/path'

export function RequireAuth({ children }: { children: ReactNode }) {
  const auth = useAuth()
  const location = useLocation()

  if (!auth?.user) {
    return <Navigate to={ROUTER_PATHS.LOGIN.path} state={{ from: location }} replace />
  }

  return children
}
