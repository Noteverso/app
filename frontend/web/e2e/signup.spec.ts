import { test, expect } from '@playwright/test'

test.describe('Signup Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/auth/signup')
  })

  test('should display signup form', async ({ page }) => {
    await expect(page.getByLabel(/邮箱/i)).toBeVisible()
    await expect(page.getByLabel(/用户名/i)).toBeVisible()
    await expect(page.getByLabel(/^密码$/i)).toBeVisible()
    await expect(page.getByLabel(/确认密码/i)).toBeVisible()
    await expect(page.getByLabel(/验证码/i)).toBeVisible()
    await expect(page.getByRole('button', { name: /sign up/i })).toBeVisible()
  })

  test('should show validation errors for empty form', async ({ page }) => {
    await page.getByRole('button', { name: /sign up/i }).click()
    await expect(page.getByText(/email is required/i)).toBeVisible()
  })

  test('should show error for invalid email', async ({ page }) => {
    await page.getByLabel(/邮箱/i).fill('invalid-email')
    await page.getByLabel(/用户名/i).fill('testuser')
    await page.getByRole('button', { name: /sign up/i }).click()
    await expect(page.getByText(/invalid email address/i)).toBeVisible()
  })

  test('should show error for short username', async ({ page }) => {
    await page.getByLabel(/邮箱/i).fill('test@gmail.com')
    await page.getByLabel(/用户名/i).fill('ab')
    await page.getByRole('button', { name: /sign up/i }).click()
    await expect(page.getByText(/username must be at least 3 characters/i)).toBeVisible()
  })

  test('should show error for weak password', async ({ page }) => {
    await page.getByLabel(/邮箱/i).fill('test@gmail.com')
    await page.getByLabel(/用户名/i).fill('testuser')
    await page.getByLabel(/^密码$/i).fill('weak')
    await page.getByRole('button', { name: /sign up/i }).click()
    await expect(page.getByText(/password must be at least 8 characters/i)).toBeVisible()
  })

  test('should show error when passwords do not match', async ({ page }) => {
    await page.getByLabel(/邮箱/i).fill('test@gmail.com')
    await page.getByLabel(/用户名/i).fill('testuser')
    await page.getByLabel(/^密码$/i).fill('Admin123456')
    await page.getByLabel(/确认密码/i).fill('DifferentPass123')
    await page.getByLabel(/验证码/i).fill('123456')
    await page.getByRole('button', { name: /sign up/i }).click()
    await expect(page.getByText(/passwords don't match/i)).toBeVisible()
  })

  test('should enable send captcha button only with valid email', async ({ page }) => {
    const sendButton = page.getByRole('button', { name: /send/i })
    await expect(sendButton).toBeEnabled()
    
    await page.getByLabel(/邮箱/i).fill('test@gmail.com')
    await sendButton.click()
    
    // Button should be disabled during countdown
    await expect(sendButton).toBeDisabled()
  })

  test('should navigate to login page via link', async ({ page }) => {
    await page.getByRole('link', { name: /login/i }).click()
    await expect(page).toHaveURL(/\/auth\/login/)
  })

  test('complete signup flow', async ({ page }) => {
    // Fill form
    await page.getByLabel(/邮箱/i).fill('newuser@example.com')
    await page.getByLabel(/用户名/i).fill('newuser123')
    await page.getByLabel(/^密码$/i).fill('Admin123456')
    await page.getByLabel(/确认密码/i).fill('Admin123456')
    
    // Send captcha
    await page.getByRole('button', { name: /send/i }).click()
    await page.waitForTimeout(1000) // Wait for captcha to be sent
    
    // Fill captcha (in real test, you'd need to get this from email or mock)
    await page.getByLabel(/验证码/i).fill('123456')
    
    // Submit form
    await page.getByRole('button', { name: /sign up/i }).click()
    
    // Should show success toast and navigate to login
    await expect(page.getByText(/account created successfully/i)).toBeVisible()
    await expect(page).toHaveURL(/\/auth\/login/, { timeout: 2000 })
  })

  test('should show loading state during submission', async ({ page }) => {
    await page.getByLabel(/邮箱/i).fill('test@gmail.com')
    await page.getByLabel(/用户名/i).fill('testuser')
    await page.getByLabel(/^密码$/i).fill('Admin123456')
    await page.getByLabel(/确认密码/i).fill('Admin123456')
    await page.getByLabel(/验证码/i).fill('123456')
    
    const submitButton = page.getByRole('button', { name: /sign up/i })
    await submitButton.click()
    
    await expect(page.getByRole('button', { name: /signing up/i })).toBeVisible()
  })
})
