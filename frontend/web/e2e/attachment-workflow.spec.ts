import { test, expect } from '@playwright/test'

test.describe('Attachment Workflow', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'test@example.com')
    await page.fill('input[type="password"]', 'password123')
    await page.click('button[type="submit"]')
    await page.waitForURL('/notes')
  })

  test('should upload and manage attachments', async ({ page }) => {
    // Navigate to attachments page
    await page.goto('/attachments')
    
    // Check empty state
    await expect(page.locator('text=No attachments')).toBeVisible()
    
    // Upload file
    await page.click('button:has-text("Upload")')
    const fileInput = page.locator('input[type="file"]')
    await fileInput.setInputFiles({
      name: 'test.pdf',
      mimeType: 'application/pdf',
      buffer: Buffer.from('test content'),
    })
    
    // Wait for upload to complete
    await expect(page.locator('text=test.pdf')).toBeVisible()
    
    // Download file
    const downloadPromise = page.waitForEvent('download')
    await page.click('button[aria-label="Download test.pdf"]')
    const download = await downloadPromise
    expect(download.suggestedFilename()).toBe('test.pdf')
    
    // Delete attachment
    await page.click('button[aria-label="Delete test.pdf"]')
    await page.click('button:has-text("Confirm")')
    
    // Verify empty state again
    await expect(page.locator('text=No attachments')).toBeVisible()
  })

  test('should display empty state when no attachments', async ({ page }) => {
    await page.goto('/attachments')
    
    await expect(page.locator('text=No attachments')).toBeVisible()
    await expect(page.locator('button:has-text("Upload")')).toBeVisible()
  })
})
