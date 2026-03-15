import { test, expect } from '@playwright/test'

test.describe('Label Optimistic Updates', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173/login')
    await page.waitForSelector('input[type="email"]')
    await page.fill('input[type="email"]', 'abc@gmail.com')
    await page.fill('input[type="password"]', 'Admin123456')
    await page.click('button[type="submit"]')
    await page.waitForURL('**/app/inbox')
    await page.goto('http://localhost:5173/app/labels')
    await page.waitForLoadState('networkidle')
  })

  test('create label shows immediately', async ({ page }) => {
    await page.click('button:has-text("New Label")')
    await page.fill('input[placeholder="Label name"]', 'Optimistic Test')
    
    const startTime = Date.now()
    await page.click('button:has-text("Create")')
    
    await expect(page.locator('text=Optimistic Test')).toBeVisible({ timeout: 500 })
    const elapsed = Date.now() - startTime
    expect(elapsed).toBeLessThan(500)
  })

  test('buttons disabled during operation', async ({ page }) => {
    const newLabelBtn = page.locator('button:has-text("New Label")')
    
    await newLabelBtn.click()
    await page.fill('input[placeholder="Label name"]', 'Test Disabled')
    await page.click('button:has-text("Create")')
    
    await expect(newLabelBtn).toBeDisabled()
  })
})
