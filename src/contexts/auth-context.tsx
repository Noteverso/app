import { createContext, useState } from 'react'
import type { ReactNode } from 'react'
import { loginApi, signupApi } from '@/api/user/user'
import type { BaseUser, NewUser } from '@/types/user'
import {
  clearStorage,
  getIsLoginStorageItem,
  getUserStorageItem,
  setIsLoginStorageItem,
  setUserStorageItem,
} from '@/lib/storage'

function useProviderAuth() {
  const [user, setUser] = useState(getUserStorageItem())
  const [isLogin, setIsLogin] = useState(getIsLoginStorageItem() ?? false)

  async function signin(user: BaseUser, callback: VoidFunction) {
    try {
      const userResponse = await loginApi(user)

      setUser(userResponse)
      setIsLogin(true)

      // 缓存用户信息
      setUserStorageItem(userResponse)
      setIsLoginStorageItem(true)
      callback()
    } catch (error) {
      console.error('login error', error)
    }
  }

  async function signup(newUser: NewUser, callback: VoidFunction) {
    await signupApi(newUser)
    callback()
  }

  function logout() {
    setUser(null)
    setIsLogin(false)
    clearStorage()
  }

  return { signin, signup, logout, user, isLogin }
}

type AuthContextType = ReturnType<typeof useProviderAuth>
export const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const auth = useProviderAuth()
  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  )
}
