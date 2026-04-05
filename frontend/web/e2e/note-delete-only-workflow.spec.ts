import type { APIRequestContext, Page } from '@playwright/test'
import { expect, test } from '@playwright/test'

const AUTH_EMAIL = 'test@gmail.com'
const AUTH_PASSWORD = 'Admin123456'

async function authenticate(page: Page, request: APIRequestContext, baseURL?: string) {
  if (!baseURL) throw new Error('baseURL is required')
  const loginResponse = await request.post(`${baseURL}/api/auth/login`, {
    data: { email: AUTH_EMAIL, password: AUTH_PASSWORD },
  })
  expect(loginResponse.ok()).toBe(true)
  const user = await loginResponse.json()
  await page.addInitScript((authUser) => {
    window.localStorage.setItem('user', JSON.stringify(authUser))
    window.localStorage.setItem('is_login', JSON.stringify(true))
  }, user)
}

async function enterNoteContent(page: Page, noteText: string) {
  const editor = page.locator('[contenteditable="true"]').first()
  await editor.click()
  await page.keyboard.type(noteText)
}

async function createNote(page: Page, noteText: string) {
  await enterNoteContent(page, noteText)
  const createResponsePromise = page.waitForResponse(response =>
    response.url().includes('/api/v1/notes') && response.request().method() === 'POST',
  )
  await page.getByRole('button', { name: '保存' }).click()
  await createResponsePromise
  await expect(page.getByText(noteText)).toBeVisible()
}

test('delete note via overflow menu moves it out of current list', async ({ page, request, baseURL }) => {
  await authenticate(page, request, baseURL)
  const noteText = `e2e-delete-only-${Date.now()}`
  await page.goto('/app/inbox')
  await createNote(page, noteText)

  const noteItem = page.locator('li').filter({ hasText: noteText }).first()
  await noteItem.hover()
  await noteItem.getByRole('button').last().click()
  await page.getByRole('menuitem', { name: '删除' }).click()

  await expect(page.getByText('删除这条笔记？')).toBeVisible()
  const deleteResponsePromise = page.waitForResponse(response =>
    response.url().includes('/api/v1/notes/') && response.request().method() === 'DELETE',
  )
  await page.getByRole('button', { name: 'Delete' }).click()
  await deleteResponsePromise

  await expect(page.getByText(noteText)).toHaveCount(0)
})
