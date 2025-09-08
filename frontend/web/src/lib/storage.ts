import type { UserResponse } from '@/types/user'

enum StorageKeys {
  CURRENT_USER = 'user',
  IS_LOGIN = 'is_login',
  EDITOR_CONTENT = 'editor_content',
}

type StorageItem<T> = T | null

export function getItem<T>(key: StorageKeys): StorageItem<T> {
  const item = window.localStorage.getItem(key)
  return item ? JSON.parse(item) : null
}

export function setItem<T>(key: StorageKeys, value: T) {
  window.localStorage.setItem(key, JSON.stringify(value))
}

export function removeItem(key: StorageKeys) {
  window.localStorage.removeItem(key)
}

export function clearStorage() {
  window.localStorage.clear()
}

export function getUserStorageItem() {
  return getItem<UserResponse>(StorageKeys.CURRENT_USER)
}

export function getEditorContent() {
  return getItem<string>(StorageKeys.EDITOR_CONTENT)
}

export function getIsLoginStorageItem() {
  return getItem<boolean>(StorageKeys.IS_LOGIN)
}

export function setUserStorageItem(user: UserResponse) {
  setItem(StorageKeys.CURRENT_USER, user)
}

export function setIsLoginStorageItem(isLogin: boolean) {
  setItem(StorageKeys.IS_LOGIN, isLogin)
}

export function setEditorContent(content: string) {
  setItem(StorageKeys.EDITOR_CONTENT, content)
}

export function clearEditorContent() {
  removeItem(StorageKeys.EDITOR_CONTENT)
}
