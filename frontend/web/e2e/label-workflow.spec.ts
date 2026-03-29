import { test, expect } from '@playwright/test'

test.describe('Label Workflow', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'test@gmail.com')
    await page.fill('input[type="password"]', 'Admin123456')
    await page.click('button[type="submit"]')
    await page.waitForURL('/notes')
  })

  test('should create and manage labels', async ({ page }) => {
    // Navigate to labels page
    await page.goto('/labels')
    
    // Create new label
    await page.click('button:has-text("New Label")')
    await page.fill('input[name="name"]', 'Test Label')
    await page.click('button[data-color="blue"]')
    await page.click('button:has-text("Create")')
    
    // Verify label appears
    await expect(page.locator('text=Test Label')).toBeVisible()
    
    // Assign label to note
    await page.goto('/notes')
    await page.click('.note-item:first-child')
    await page.click('button:has-text("Add Label")')
    await page.click('text=Test Label')
    
    // Filter notes by label
    await page.goto('/labels')
    await page.click('text=Test Label')
    await expect(page.locator('.note-item')).toHaveCount(1)
    
    // Delete label
    await page.goto('/labels')
    await page.click('button[aria-label="Delete Test Label"]')
    await page.click('button:has-text("Confirm")')
    
    // Verify label is deleted
    await expect(page.locator('text=Test Label')).not.toBeVisible()
  })

  test('should display empty state when no labels', async ({ page }) => {
    await page.goto('/labels')
    
    // Assuming no labels exist
    await expect(page.locator('text=No labels yet')).toBeVisible()
  })
})
