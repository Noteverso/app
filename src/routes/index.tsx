import { createBrowserRouter } from 'react-router-dom'
import { Layout } from '@/components/layout'
import Home from '@/pages/home'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        path: 'home',
        element: <Home />,
      },
    ],
  },
])
