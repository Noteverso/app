import { Navigate, Outlet, createBrowserRouter, redirect } from 'react-router-dom'
import { Suspense } from 'react'
import { projectLoader, protectedLoader } from './loader'
import { protectedAction } from './action'
import { ROUTER_PATHS } from '@/constants'
import { Layout } from '@/layout/layout'
import { AuthLayout, LoginPage, SignupPage } from '@/pages/auth'
import { ErrorPage } from '@/pages/error/error'
import { Project } from '@/pages/project/project'
import { ProjectsManage } from '@/pages/projects-manage/projects-manage'
import { Inbox } from '@/pages/inbox/inbox'
import { Label } from '@/pages/label/label'
import { LabelDetail } from '@/pages/label/label-detail'
import { labelDetailLoader } from '@/pages/label/loader'
import { Attachment } from '@/pages/attachment/attachment'
import { SearchPage } from '@/pages/search/search'
import { Home } from '@/pages/home/home'
import { sharedNotesLoader } from '@/pages/shared-notes-page/loader'
import { sharedNotesAction } from '@/pages/shared-notes-page/action'
import { authProvider } from '@/lib/auth'
import { loginAction } from '@/pages/auth/auth-action'

const Root = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <Outlet />
  </Suspense>
)

export const router = createBrowserRouter([
  {
    id: 'root',
    path: '/',
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      {
        index: true,
        element: <Home />,
        async loader() {
          const user = authProvider.user()
          return user
        },
      },
      {
        id: 'auth',
        path: 'auth',
        element: <AuthLayout />,
        children: [
          {
            path: 'login',
            element: <LoginPage />,
            action: loginAction,
          },
          {
            path: 'signup',
            element: <SignupPage />,
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
        path: 'app',
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
                loader: protectedLoader(sharedNotesLoader),
                action: protectedAction(sharedNotesAction),
              },
              {
                path: 'labels',
                element: <Label />,
              },
              {
                path: 'labels/:labelId',
                element: <LabelDetail />,
                loader: protectedLoader(labelDetailLoader),
                action: protectedAction(sharedNotesAction),
              },
              {
                path: 'attachments',
                element: <Attachment />,
              },
              {
                path: 'search',
                element: <SearchPage />,
              },
              {
                path: 'projects',
                element: <ProjectsManage />,
              },
              {
                path: 'projects/:projectId',
                element: <Project />,
                loader: protectedLoader(sharedNotesLoader),
                action: protectedAction(sharedNotesAction),
              },
            ],
          },
        ],
      },
    ],
  },
])
