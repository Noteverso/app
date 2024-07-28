import { Navigate, createBrowserRouter, redirect } from 'react-router-dom'
import { ROUTER_PATHS } from './path'
import { Layout } from '@/layout/layout'
import { AuthLayout, LoginPage } from '@/pages/auth'
import { ErrorPage } from '@/pages/error/error'
import { Project } from '@/pages/project/project'
import { Inbox } from '@/pages/inbox/inbox'
import { Label } from '@/pages/label/label'
import { Attachment } from '@/pages/attachment/attachment'
import { Home } from '@/pages/home/home'
import { protectedLoader, sharedProjectLoader } from '@/lib/loaders'
import { projectLoader } from '@/layout/project-loader'
import { authProvider } from '@/lib/auth'
import { protectedAction, sharedNoteAction } from '@/lib/actions'
import { loginAction } from '@/pages/auth/auth-action'

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
        action: loginAction,
      },
      {
        path: 'logout',
        action: async () => {
          authProvider.logout()
          return redirect(ROUTER_PATHS.LOGIN.path)
        },
      },
    ],
  },
  {
    id: 'app',
    path: '/app',
    element: <Layout />,
    errorElement: <ErrorPage />,
    loader: protectedLoader(projectLoader),
    children: [
      {
        errorElement: <ErrorPage />,
        children: [
          {
            index: true,
            element: <Navigate to="/app/inbox" replace />,
          },
          {
            path: 'inbox',
            element: <Inbox />,
            loader: protectedLoader(sharedProjectLoader),
            action: protectedAction(sharedNoteAction),
          },
          {
            path: 'labels',
            element: <Label />,
          },
          {
            path: 'attachments',
            element: <Attachment />,
          },
          {
            path: 'projects/:projectId',
            element: <Project />,
            loader: protectedLoader(sharedProjectLoader),
            action: protectedAction(sharedNoteAction),
          },
        ],
      },
    ],
  },
])
