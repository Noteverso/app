import type { ActionFunctionArgs } from 'react-router-dom'
import { redirect } from 'react-router-dom'
import { ROUTER_PATHS } from '@/routes/path'
import { authProvider } from '@/lib/auth'

export function protectedAction<T>(action: (arg: ActionFunctionArgs) => Promise<T>) {
  return async (arg: ActionFunctionArgs): Promise<T> => {
    const user = authProvider.user()
    // that allows login to redirect back to this page upon successful authentication
    // if the user is not logged in, redirect them to the login page with a `from` parameter
    if (!user) {
      const params = new URLSearchParams()
      params.set('from', new URL(arg.request.url).pathname)

      return redirect(`${ROUTER_PATHS.LOGIN.path}?${params.toString()}`) as any
    }

    return action(arg)
  }
}
