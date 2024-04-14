import axios from 'axios'
import type {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
} from 'axios'

export interface HttpRequestConfig extends AxiosRequestConfig {
  // 是否开启统一错误提示，设置 false 关闭
  showFailToast?: boolean;
}

export interface Result<T = any> {
  code: string | number;
  message: string;
  data: T;
}

export class Http {
  // 普通请求单例
  private static instance: Http | null = null
  // 文件请求单例
  private static fileInstance: Http | null = null

  axiosInstance: AxiosInstance
  baseConfig: HttpRequestConfig
  requestConfig: HttpRequestConfig

  constructor(isFile = false, config: AxiosRequestConfig) {
    this.baseConfig = {
      // baseURL: import.meta.env.DEV ? '/api' : 'https://noteverso.com',
      showFailToast: true,
    }

    this.requestConfig = Object.assign(this.baseConfig, config)
    this.axiosInstance = axios.create(this.requestConfig)
    this.requestInterceptors()

    if (isFile) {
      this.responseFileInterceptors()
    } else {
      this.responseInterceptors()
    }
  }

  // 请求拦截
  private requestInterceptors(): void {
    this.axiosInstance.interceptors.request.use(
      (config) => {
        return config
      },
      (error: AxiosError) => {
        return Promise.reject(error)
      },
    )
  }

  // 普通请求的响应拦截
  private responseInterceptors() {
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse) => {
        const { data }: Result = response.data

        return data
      },
      (error: AxiosError) => {
        return this.handleError(error)
      },
    )
  }

  // 导出文件请求的响应拦截
  private responseFileInterceptors() {
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse) => {
        return response
      },
      (error: AxiosError) => {
        return this.handleError(error)
      },
    )
  }

  private handleError(error: AxiosError) {
    let message: string

    const { status, data } = error.response!
    switch (status) {
      case 500:
        message = '服务器连接失败'
        break
      case 401:
        message = '登录过期，请重新登录'
        break
      default:
        message = '未知异常，请重新登录'
    }

    if (this.requestConfig.showFailToast) {
      // TODO: 调用错误提示
      console.warn(message)
    }

    return Promise.reject(data)
  }

  static getInstance(): Http
  static getInstance(axiosRequestConfig: HttpRequestConfig): Http
  static getInstance(axiosRequestConfig?: HttpRequestConfig) {
    if (!Http.instance) {
      Http.instance = new Http(false, axiosRequestConfig || {})
    }

    return Http.instance
  }

  static getFileInstance(): Http
  static getFileInstance(axiosRequestConfig: AxiosRequestConfig): Http
  static getFileInstance(axiosRequestConfig?: AxiosRequestConfig) {
    if (!Http.fileInstance) {
      Http.fileInstance = new Http(true, axiosRequestConfig || {})
    }

    return Http.fileInstance
  }
}

export function request<T = any>(config: HttpRequestConfig): Promise<T> {
  return Http.getInstance().axiosInstance(config)
}

export function requestFile<T = any>(config: HttpRequestConfig): Promise<T> {
  return Http.getFileInstance().axiosInstance(config)
}
