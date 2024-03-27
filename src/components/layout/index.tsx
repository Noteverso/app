import { AppMain } from './app-main'
import { Sidebar } from './siderbar'

export const Layout = function Layout() {
  return (
    <div>
      <Sidebar />
      <AppMain />
    </div>
  )
}
