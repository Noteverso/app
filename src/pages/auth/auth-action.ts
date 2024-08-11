import { type ActionFunctionArgs, json } from 'react-router-dom'
import type { BaseUser } from '@/types/user'
import { loginApi } from '@/api/user/user'
import { setIsLoginStorageItem, setUserStorageItem } from '@/lib/storage'

export async function loginAction({ request }: ActionFunctionArgs) {
  const formData = await request.formData()

  const user: BaseUser = {
    username: formData.get('username') as string,
    password: formData.get('password') as string,
  }

  const response = await loginApi(user)
  if (!response.ok) {
    throw json(response.data, { status: response.status })
  }

  setUserStorageItem(response.data)
  setIsLoginStorageItem(true)
  return response.data
}
