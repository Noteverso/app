import { Outlet } from 'react-router-dom'
import { Toaster } from '@/components/ui/toast/toaster'

export function AuthLayout() {
  return (
    <>
      <Outlet />
      <Toaster />
    </>
  )
}
