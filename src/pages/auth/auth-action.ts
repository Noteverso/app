import { type ActionFunctionArgs, json, redirect } from 'react-router-dom'
import type { BaseUser, UserResponse } from '@/types/user'
import { authProvider } from '@/lib/auth'
import { ROUTER_PATHS } from '@/routes/path'

export async function loginAction({ request }: ActionFunctionArgs) {
  const formData = await request.formData()

  const user: BaseUser = {
    username: formData.get('username') as string,
    password: formData.get('password') as string,
  }

  const userResponse: UserResponse = await authProvider.login(user)
  if (userResponse && userResponse.token) {
    return redirect(ROUTER_PATHS.INBOX.path)
  }

  return json({
    ok: false,
    error: 'df',
  })
}
