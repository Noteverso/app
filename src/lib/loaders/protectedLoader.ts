import { LoaderFunction, LoaderFunctionArgs, redirect } from 'react-router-dom'
import { getUserStorageItem } from '@/lib/auth'
import { ROUTER_PATHS } from '@/routes/path'

export function protectedLoader(loader: LoaderFunction) {
  return (arg: LoaderFunctionArgs) => {
    const user = getUserStorageItem()
    // that allows login to redirect back to this page upon successful authentication
    // if the user is not logged in, redirect them to the login page with a `from` parameter
    if (!user) {
      const params = new URLSearchParams()
      params.set('from', new URL(arg.request.url).pathname)

      return redirect(`${ROUTER_PATHS.LOGIN.path}?${params.toString()}`)
    }

    return loader(arg)
  }
}
