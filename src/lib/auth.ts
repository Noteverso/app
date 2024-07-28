import {
  clearStorage,
  getIsLoginStorageItem,
  getUserStorageItem,
  setIsLoginStorageItem,
  setUserStorageItem,
} from './storage'
import { loginApi } from '@/api/user/user'
import type { BaseUser, UserResponse } from '@/types/user'

interface AuthProvider {
  isAuthenticated(): boolean | null;
  user(): UserResponse | null;
  login(user: BaseUser): Promise<UserResponse>;
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
    const userResponse = await loginApi(user)
    setUserStorageItem(userResponse)
    setIsLoginStorageItem(true)
    return userResponse
  },
  logout() {
    clearStorage()
  },
}
