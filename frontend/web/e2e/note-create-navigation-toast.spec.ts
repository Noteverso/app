import type { APIRequestContext, Page } from '@playwright/test'
import { expect, test } from '@playwright/test'

const AUTH_EMAIL = 'test@gmail.com'
const AUTH_PASSWORD = 'Admin123456'
const INBOX_PROJECT_ID = '313956719430602752'
const MYSQL_PROJECT_ID = '313956886909161472'

async function authenticate(page: Page, request: APIRequestContext, baseURL?: string) {
  if (!baseURL) {
    throw new Error('baseURL is required for note-create navigation toast tests')
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

async function getSidebarCount(page: Page, label: string) {
  const link = page.getByRole('link', { name: new RegExp(`^${label}\\s+\\d+$`) })
  await expect(link).toBeVisible()
  const text = await link.textContent()
  const match = text?.match(/(\d+)\s*$/)

  expect(match).not.toBeNull()
  return Number(match?.[1])
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

async function expectComposerCleared(contentInput: ReturnType<Page['locator']>, saveButton: ReturnType<Page['getByRole']>, noteText: string) {
  await expect(contentInput).not.toHaveValue(new RegExp(noteText))
  await expect(saveButton).toBeDisabled()
}

test.describe('Note create navigation toast', () => {
  test.beforeEach(async ({ page, request, baseURL }) => {
    await authenticate(page, request, baseURL)
  })

  test('same-project create clears immediately and does not show a cross-project toast', async ({ page }) => {
    const noteText = `same-project-toast-check-${Date.now()}`
    const form = page.locator('form').first()
    const contentInput = form.locator('input[name="contentJson"]')
    const saveButton = page.getByRole('button', { name: '保存' })

    await page.goto('/app/inbox')
    await expect(page.getByRole('heading', { name: '收件箱' })).toBeVisible()

    await enterNoteContent(page, noteText)
    await expect(contentInput).toHaveValue(new RegExp(noteText))
    await expect(saveButton).toBeEnabled()

    const createResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes') && response.request().method() === 'POST',
    )

    await saveButton.click()

    await expectComposerCleared(contentInput, saveButton, noteText)
    await createResponsePromise

    await expect(page.getByText(noteText)).toBeVisible()
    await expect(page.getByText(/^Note created in /)).toHaveCount(0)
  })

  test('cross-project create from inbox increments the destination count, shows the toast, and navigates', async ({ page }) => {
    const noteText = `cross-project-inbox-${Date.now()}`
    const form = page.locator('form').first()
    const contentInput = form.locator('input[name="contentJson"]')
    const projectInput = form.locator('input[name="projectId"]')
    const saveButton = page.getByRole('button', { name: '保存' })

    await page.goto('/app/inbox')
    await expect(page.getByRole('heading', { name: '收件箱' })).toBeVisible()
    const mysqlCountBefore = await getSidebarCount(page, 'MySQL')

    await enterNoteContent(page, noteText)
    await pickProjectInComposer(page, 'MySQL')
    await expect(projectInput).toHaveValue(MYSQL_PROJECT_ID)

    const createResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes') && response.request().method() === 'POST',
    )

    await saveButton.click()

    await expectComposerCleared(contentInput, saveButton, noteText)
    await createResponsePromise

    await expect(page.getByText('Note created in MySQL', { exact: true })).toBeVisible()
    await expect(page.getByRole('button', { name: 'Open project' })).toBeVisible()
    await expect(page.getByText(noteText)).toHaveCount(0)
    await expect.poll(async () => getSidebarCount(page, 'MySQL')).toBe(mysqlCountBefore + 1)

    await page.getByRole('button', { name: 'Open project' }).click()
    await page.waitForURL(`**/app/projects/${MYSQL_PROJECT_ID}`)
  })

  test('cross-project create from a project route to inbox updates inbox count and targets /app/inbox', async ({ page }) => {
    const noteText = `cross-project-to-inbox-${Date.now()}`
    const form = page.locator('form').first()
    const contentInput = form.locator('input[name="contentJson"]')
    const projectInput = form.locator('input[name="projectId"]')
    const saveButton = page.getByRole('button', { name: '保存' })

    await page.goto(`/app/projects/${MYSQL_PROJECT_ID}`)
    await expect(page.getByRole('heading', { name: 'MySQL' })).toBeVisible()
    const inboxCountBefore = await getSidebarCount(page, '收件箱')

    await enterNoteContent(page, noteText)
    await pickProjectInComposer(page, 'Inbox')
    await expect(projectInput).toHaveValue(INBOX_PROJECT_ID)

    const createResponsePromise = page.waitForResponse(response =>
      response.url().includes('/api/v1/notes') && response.request().method() === 'POST',
    )

    await saveButton.click()

    await expectComposerCleared(contentInput, saveButton, noteText)
    await createResponsePromise

    await expect(page.getByText('Note created in 收件箱', { exact: true })).toBeVisible()
    await expect(page.getByRole('button', { name: 'Open project' })).toBeVisible()
    await expect.poll(async () => getSidebarCount(page, '收件箱')).toBe(inboxCountBefore + 1)

    await page.getByRole('button', { name: 'Open project' }).click()
    await page.waitForURL('**/app/inbox')
  })
})
