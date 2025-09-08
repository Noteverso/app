import { request } from '@/lib/http'
import type { BaseUser, NewUser, UserResponse } from '@/types/user'

export function loginApi(data: BaseUser) {
  return request<UserResponse>({
    url: '/api/auth/login',
    method: 'post',
    data,
  })
}

export function signupApi(data: NewUser) {
  return request({
    url: '/api/auth/signup',
    method: 'post',
    data,
  })
}
