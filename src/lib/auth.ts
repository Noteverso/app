import {
  clearStorage,
  getIsLoginStorageItem,
  getUserStorageItem,
  setIsLoginStorageItem,
  setUserStorageItem,
} from './storage'
import { UserForLogin, UserResponse/* , loginApi */ } from '@/api/user'

interface AuthProvider {
  isAuthenticated(): boolean | null;
  user(): UserResponse | null;
  login(user: UserForLogin): Promise<void>;
  logout(): void;
}

export const authProvider: AuthProvider = {
  isAuthenticated() {
    return getIsLoginStorageItem()
  },
  user() {
    return getUserStorageItem()
  },
  async login(user: UserForLogin) {
    // const userResponse = await loginApi(user)
    console.warn(user)
    const userResponse = {
      username: 'test',
      token: 'testtoken',
    }
    setUserStorageItem(userResponse)
    setIsLoginStorageItem(true)
  },
  logout() {
    clearStorage()
  },
}
