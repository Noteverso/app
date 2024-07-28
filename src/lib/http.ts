import axios from 'axios'
import type {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
} from 'axios'
import { authProvider } from '@/lib/auth'

export interface RequestOptions extends AxiosRequestConfig {
}

export class Http {
  // 普通请求单例
  private static instance: Http | null = null

  axiosInstance: AxiosInstance
  baseOptions: RequestOptions
  options: RequestOptions

  constructor(config: RequestOptions) {
    this.baseOptions = {
      // baseURL: import.meta.env.DEV ? '/api' : 'https://noteverso.com',
    }

    this.options = Object.assign(this.baseOptions, config)
    this.axiosInstance = axios.create(this.options)
    this.requestInterceptors()
    this.responseInterceptors()
  }

  // 请求拦截
  private requestInterceptors(): void {
    this.axiosInstance.interceptors.request.use(
      (config) => {
        const user = authProvider.user()
        config.headers = config.headers ?? {}
        if (user !== null) {
          const token = user.token
          config.headers.Authorization = `Bearer ${token}`
        }

        return config
      },
      (error: AxiosError) => {
        if (error.response) {
          return Promise.reject(error.response)
        }

        return Promise.reject(error)
      },
    )
  }

  // 普通请求的响应拦截
  private responseInterceptors() {
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse) => {
        return response
      },
      (error: AxiosError) => {
        if (error.response) {
          return Promise.reject(error.response)
        }

        return Promise.reject(error)
      },
    )
  }

  static getInstance(): Http
  static getInstance(options: RequestOptions): Http
  static getInstance(options?: RequestOptions) {
    if (!Http.instance) {
      Http.instance = new Http(options || {})
    }

    return Http.instance
  }
}

export interface ApiSuccessResponse<T = any> {
  ok: true;
  data: T
}

export interface ApiErrorResponse {
  ok: false;
  error: string
}

export type ApiResponse<T = any> = ApiSuccessResponse<T> | ApiErrorResponse

export async function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  try {
    const response: AxiosResponse<T> = await Http.getInstance().axiosInstance(options)
    return { ok: true, data: response.data }
  } catch (error) {
    return handleError(error)
  }
}

export function handleError(error: unknown): ApiErrorResponse {
  let errorMessage: string
  if (error instanceof Error) {
    errorMessage = error.message
  } else if (typeof error === 'string') {
    errorMessage = error
  } else {
    errorMessage = 'An unknow error occurred'
  }

  return { ok: false, error: errorMessage }
}

export type ApiMethod = 'get' | 'post' | 'put' | 'delete'

export function createApi(method: ApiMethod) {
  return async function <T>(url: string, options?: RequestOptions): Promise<ApiResponse> {
    try {
      let response: AxiosResponse<T>

      if (method === 'get' || method === 'delete') {
        response = await Http.getInstance().axiosInstance[method]<T>(url, options)
      } else {
        response = await Http.getInstance().axiosInstance[method]<T>(url, options?.data, options)
      }

      return { ok: true, data: response.data }
    } catch (error) {
      return handleError(error)
    }
  }
}

export const api = {
  get: createApi('get'),
  post: createApi('post'),
  put: createApi('put'),
  delete: createApi('delete'),
}
