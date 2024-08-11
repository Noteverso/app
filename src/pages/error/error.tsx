import { Navigate, isRouteErrorResponse, useLocation, useRouteError } from 'react-router-dom'
import { ROUTER_PATHS } from '@/constants'
import { authProvider } from '@/lib/auth'

export function ErrorPage() {
  const error = useRouteError()
  const location = useLocation()

  if (isRouteErrorResponse(error)) {
    if (error.status === 401) {
      authProvider.logout()
      return <Navigate to={ROUTER_PATHS.LOGIN.path} state={{ from: location }} replace />
    }

    return (
      <div id="error-page" className="error-page flex flex-col h-screen items-center justify-center">
        <h1>Oops!</h1>
        <p>Sorry, an unexpected error has occurred.</p>
        <p>
          <i>{error.statusText || error.data?.message}</i>
        </p>
        <button onClick={() => window.location.reload()} className="underline">Please reload the page to retry</button>
      </div>
    )
  } else {
    return <div id="error-page">An unexpected error has occurred.</div>
  }
}
