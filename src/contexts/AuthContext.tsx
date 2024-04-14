import { createContext, useState } from 'react'
import type { ReactNode } from 'react'
import { type UserForLogin, UserForSignup, loginApi, signupApi } from '@/api/user'
import {
  clearStorage,
  getIsLoginStorageItem,
  getUserStorageItem,
  setIsLoginStorageItem,
  setUserStorageItem,
} from '@/lib/auth'

function useProviderAuth() {
  const [user, setUser] = useState(getUserStorageItem())
  const [isLogin, setIsLogin] = useState(getIsLoginStorageItem() ?? false)

  async function signin(user: UserForLogin, callback: VoidFunction) {
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

  async function signup(newUser: UserForSignup, callback: VoidFunction) {
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

export function AuthProvider({ children }: { children : ReactNode }) {
  const auth = useProviderAuth()
  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  )
}
