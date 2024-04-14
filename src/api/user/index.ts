import { request } from '@/lib/http'

export type UserForLogin = {
  username: string;
  password: string;
}

export type UserForSignup = {
  username: string;
  email: string;
  password: string;
  captchaCode: string;
}

export type UserResponse = {
  username: string;
  token: string;
}

export function loginApi(data: UserForLogin) {
  return request<UserResponse>({
    url: '/api/auth/login',
    method: 'post',
    data,
  })
}

export function signupApi(data: UserForSignup) {
  return request({
    url: '/api/auth/signup',
    method: 'post',
    data,
  })
}
