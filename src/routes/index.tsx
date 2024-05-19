import { Navigate, createBrowserRouter } from 'react-router-dom'
import { Layout, layoutLoader } from '@/components/layout'
import { LoginPage } from '@/pages/auth'
import { ErrorPage } from '@/pages/error'
import { Project } from '@/pages/project'
import { Inbox } from '@/pages/inbox'
import { Labels } from '@/pages/labels'
import { Attachments } from '@/pages/attachments'

export const router = createBrowserRouter([
  {
    id: 'root',
    path: '/',
    element: <Layout />,
    errorElement: <ErrorPage />,
    loader: layoutLoader,
    children: [
      {
        errorElement: <ErrorPage />,
        children: [
          {
            index: true,
            element: <Navigate to="/app" replace />,
          },
          {
            // loading
            path: 'app',
            element: <Navigate to="/app/inbox" replace />,
          },
          {
            path: 'app/inbox',
            element: <Inbox />,
          },
          {
            path: 'app/labels',
            element: <Labels />,
          },
          {
            path: 'app/attachments',
            element: <Attachments />,
          },
          {
            path: 'app/projects/:projectId',
            element: <Project />,
          },
        ],
      },
    ],
  },
  {
    path: '/auth',
    children: [
      {
        path: 'login',
        element: <LoginPage />,
      },
    ],
  },
])
