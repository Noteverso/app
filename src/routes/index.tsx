import { Navigate, Route, createBrowserRouter, createRoutesFromElements, redirect } from 'react-router-dom'
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
import { clearStorage } from '@/lib/auth'

export const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route
        id="root"
        path="/"
        element={<Home />}
        errorElement={<ErrorPage />}
      // loader={rootLoader}
      />
      <Route path="/auth" element={<AuthLayout />}>
        <Route path="login" element={<LoginPage />} />
        <Route
          path="logout"
          action={async ({ params, request }) => {
            console.warn(params, request)
            clearStorage()
            return redirect(ROUTER_PATHS.HOME.path)
          }}
        />
      </Route>
      <Route
        path="/app"
        element={<Layout />}
        errorElement={<ErrorPage />}
        loader={protectedLoader(layoutLoader)}
      >
        <Route index element={<Navigate to="/app/inbox" replace />} />
        <Route path="inbox" element={<Inbox />} />
        <Route path="labels" element={<Labels />} />
        <Route path="attachments" element={<Attachments />} />
        <Route path="projects/:projectId" element={<Project />} />
      </Route>
    </>,
  ),
)

// export const router1 = createBrowserRouter([
//   {
//     id: 'root',
//     path: '/',
//     element: <Home />,
//     errorElement: <ErrorPage />,
//     // loader: rootLoader,
//   },
//   {
//     id: 'auth',
//     path: '/auth',
//     children: [
//       {
//         path: 'login',
//         element: <LoginPage />,
//       },
//       {
//         path: 'logout',
//       },
//     ],
//   },
//   {
//     id: 'app',
//     path: '/app',
//     element: <Layout />,
//     errorElement: <ErrorPage />,
//     loader: layoutLoader,
//     children: [
//       {
//         index: true,
//         element: <Navigate to="/app/inbox" replace />,
//       },
//       {
//         path: 'inbox',
//         element: <Inbox />,
//       },
//       {
//         path: 'labels',
//         element: <Labels />,
//       },
//       {
//         path: 'attachments',
//         element: <Attachments />,
//       },
//       {
//         path: 'projects/:projectId',
//         element: <Project />,
//       },
//     ],
//   },
// ])
