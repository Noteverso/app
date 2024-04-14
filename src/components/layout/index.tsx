import { useLoaderData } from 'react-router-dom'
import { AppMain } from './main'
import { Sidebar } from './siderbar'
import { AuthProvider } from '@/contexts/AuthContext'

export function layoutLoader() {
  return {
    projectIds: ['1', '2', '3'],
  }
}

export function Layout() {
  const { projectIds } = useLoaderData() as { projectIds: string[] }

  return (
    <AuthProvider>
      <div>
        <Sidebar projectIds={projectIds} />
        <AppMain />
      </div>
    </AuthProvider>
  )
}
