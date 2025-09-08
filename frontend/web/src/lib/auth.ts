import { json, redirect } from 'react-router-dom'
import {
  clearStorage,
  getIsLoginStorageItem,
  getUserStorageItem,
  setIsLoginStorageItem,
  setUserStorageItem,
} from './storage'
import type { ApiResponse } from './http'
import { loginApi } from '@/api/user/user'
import { ROUTER_PATHS } from '@/constants'
import type { BaseUser, UserResponse } from '@/types/user'

interface AuthProvider {
  isAuthenticated(): boolean | null;
  user(): UserResponse | null;
  login(user: BaseUser): Promise<ApiResponse<UserResponse>>;
  logout(): void;
}

export const authProvider: AuthProvider = {
  isAuthenticated() {
    return getIsLoginStorageItem()
  },
  user() {
    return getUserStorageItem()
  },
  async login(user: BaseUser) {
    const response = await loginApi(user)
    if (!response.ok) {
      throw json(response.data, { status: response.status })
    }

    setUserStorageItem(response.data)
    setIsLoginStorageItem(true)
    redirect(ROUTER_PATHS.INBOX.path)
    return response
  },
  logout() {
    clearStorage()
  },
}
