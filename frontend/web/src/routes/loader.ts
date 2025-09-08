import type { LoaderFunctionArgs } from 'react-router-dom'
import { json, redirect } from 'react-router-dom'
import { getProjectsApi } from '@/api/project/project'
import { authProvider } from '@/lib/auth'
import { ROUTER_PATHS } from '@/constants'

export async function projectLoader() {
  const response = await getProjectsApi()
  if (!response.ok) {
    throw json(response.data, { status: response.status })
  }

  return response.data
}

export function protectedLoader<T>(loader: (arg: LoaderFunctionArgs) => Promise<T>) {
  return async (arg: LoaderFunctionArgs): Promise<T> => {
    const user = authProvider.user()
    if (!user) {
      const params = new URLSearchParams()
      params.set('from', new URL(arg.request.url).pathname)

      return redirect(`${ROUTER_PATHS.LOGIN.path}?${params.toString()}`) as any
    }

    return loader(arg)
  }
}
