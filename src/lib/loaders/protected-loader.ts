import type { LoaderFunctionArgs } from 'react-router-dom'
import { redirect } from 'react-router-dom'
import { ROUTER_PATHS } from '@/constants'
import { authProvider } from '@/lib/auth'

export function protectedLoader<T>(loader: (arg: LoaderFunctionArgs) => Promise<T>) {
  return async (arg: LoaderFunctionArgs): Promise<T> => {
    const user = authProvider.user()
    // that allows login to redirect back to this page upon successful authentication
    // if the user is not logged in, redirect them to the login page with a `from` parameter
    if (!user) {
      const params = new URLSearchParams()
      params.set('from', new URL(arg.request.url).pathname)

      return redirect(`${ROUTER_PATHS.LOGIN.path}?${params.toString()}`) as any
    }

    return loader(arg)
  }
}
