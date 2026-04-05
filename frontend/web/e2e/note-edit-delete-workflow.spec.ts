import type { APIRequestContext, Page } from '@playwright/test'
import { expect, test } from '@playwright/test'

const AUTH_EMAIL = 'test@gmail.com'
const AUTH_PASSWORD = 'Admin123456'
const INBOX_PROJECT_ID = '313956719430602752'
const MYSQL_PROJECT_ID = '313956886909161472'

async function authenticate(page: Page, request: APIRequestContext, baseURL?: string) {
  if (!baseURL) {
    throw new Error('baseURL is required for note edit/delete workflow tests')
  }

  const loginResponse = await request.post(`${baseURL}/api/auth/login`, {
    data: {
      email: AUTH_EMAIL,
      password: AUTH_PASSWORD,
    },
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

async function pickProjectInComposer(page: Page, projectName: string) {
  const form = page.locator('form').first()
  await form.getByRole('button', { name: 'Quick assign project' }).click()
  await form.getByRole('button', { name: projectName, exact: true }).click()
}

async function createNoteViaComposer(page: Page, noteText: string) {
  const saveButton = page.getByRole('button', { name: '保存' })

  await enterNoteContent(page, noteText)
  await expect(saveButton).toBeEnabled()

  const createResponsePromise = page.waitForResponse(response =>
    response.url().includes('/api/v1/notes') && response.request().method() === 'POST',
  )

  await saveButton.click()
  await createResponsePromise
  await expect(page.getByText(noteText)).toBeVisible()
}

async function openNoteActionsMenuForText(page: Page, noteText: string) {
  const noteItem = page.locator('li').filter({ hasText: noteText }).first()
  await expect(noteItem).toBeVisible()
  await noteItem.hover()
  await noteItem.getByRole('button').last().click()
  return noteItem
}

test.describe('Note edit/delete workflow', () => {
  test.beforeEach(async ({ page, request, baseURL }) => {
    await authenticate(page, request, baseURL)
  })

  test('edits a note with the shared top editor and then moves it to trash', async ({ page }) => {
    const originalText = `e2e-edit-delete-original-${Date.now()}`
    const updatedText = `e2e-edit-delete-updated-${Date.now()}`

    await page.goto('/app/inbox')
    await expect(page.getByRole('heading', { name: '收件箱' })).toBeVisible()

    await createNoteViaComposer(page, originalText)

    await openNoteActionsMenuForText(page, originalText)
    await page.getByRole('menuitem', { name: '编辑' }).click()

    await expect(page.getByText('Editing note')).toBeVisible()
    await expect(page.getByRole('button', { name: '更新' })).toBeVisible()

    const editor = page.locator('[contenteditable="true"]').first()
    await editor.click()
    await page.keyboard.press(process.platform === 'darwin' ? 'Meta+A' : 'Control+A')
    await page.keyboard.type(updatedText)
    await pickProjectInComposer(page, 'Inbox')

    const updateResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes/') && response.request().method() === 'PATCH',
    )

    await page.getByRole('button', { name: '更新' }).click()
    await updateResponsePromise

    await expect(page.getByText('Editing note')).toHaveCount(0)
    await expect(page.getByText(updatedText)).toBeVisible()
    await expect(page.getByText(originalText)).toHaveCount(0)

    await openNoteActionsMenuForText(page, updatedText)
    await page.getByRole('menuitem', { name: '删除' }).click()

    await expect(page.getByText('删除这条笔记？')).toBeVisible()

    const deleteResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes/') && response.request().method() === 'DELETE',
    )

    await page.getByRole('button', { name: 'Delete' }).click()
    await deleteResponsePromise

    await expect(page.getByText('删除这条笔记？')).toHaveCount(0)
    await expect(page.getByText(updatedText)).toHaveCount(0)
  })

  test('removes the note from the current project list after editing it into another project', async ({ page }) => {
    const originalText = `e2e-cross-project-edit-original-${Date.now()}`

    await page.goto('/app/inbox')
    await expect(page.locator('form').first()).toBeVisible()

    await createNoteViaComposer(page, originalText)

    await openNoteActionsMenuForText(page, originalText)
    await page.getByRole('menuitem', { name: '编辑' }).click()

    await expect(page.getByText('Editing note')).toBeVisible()
    await pickProjectInComposer(page, 'MySQL')

    const updateResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes/') && response.request().method() === 'PATCH',
    )

    await page.getByRole('button', { name: '更新' }).click()
    await updateResponsePromise

    await expect(page.getByText('Note moved to MySQL')).toBeVisible()
    await expect(page.getByText(originalText)).toHaveCount(0)

    await page.goto(`/app/projects/${MYSQL_PROJECT_ID}`)
    await expect(page.locator('form').first()).toBeVisible()
    await expect(page.getByText(originalText)).toBeVisible()
  })
})
