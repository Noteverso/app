import { Navigate, createBrowserRouter, redirect } from 'react-router-dom'
import { ROUTER_PATHS } from './path'
import { Layout } from '@/components/layout'
import { AuthLayout, LoginPage } from '@/pages/auth'
import { ErrorPage } from '@/pages/error'
import { Project } from '@/pages/project'
import { Inbox } from '@/pages/inbox'
import { Labels } from '@/pages/labels'
import { Attachments } from '@/pages/attachments'
import { Home } from '@/pages/home'
import { layoutLoader, protectedLoader } from '@/lib/loaders'
import { authProvider } from '@/lib/auth'

export const router = createBrowserRouter([
  {
    id: 'root',
    path: '/',
    element: <Home />,
    async loader() {
      const user = authProvider.user()
      return user
    },
  },
  {
    id: 'auth',
    path: '/auth',
    element: <AuthLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        path: 'logout',
        action: async () => {
          authProvider.logout()
          return redirect(ROUTER_PATHS.HOME.path)
        },
      },
    ],
  },
  {
    id: 'app',
    path: '/app',
    element: <Layout />,
    errorElement: <ErrorPage />,
    loader: protectedLoader(layoutLoader),
    children: [
      {
        index: true,
        element: <Navigate to="/app/inbox" replace />,
      },
      {
        path: 'inbox',
        element: <Inbox />,
      },
      {
        path: 'labels',
        element: <Labels />,
      },
      {
        path: 'attachments',
        element: <Attachments />,
      },
      {
        path: 'projects/:projectId',
        element: <Project />,
      },
    ],
  },
])
