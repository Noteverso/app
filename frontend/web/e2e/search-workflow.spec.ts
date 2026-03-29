import { test, expect } from '@playwright/test'

test.describe('Search Workflow', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'test@gmail.com')
    await page.fill('input[type="password"]', 'Admin123456')
    await page.click('button[type="submit"]')
    await page.waitForURL('/notes')
  })

  test('should search with no results', async ({ page }) => {
    // Navigate to search
    await page.click('button[aria-label="Search"]')
    
    // Enter search query that returns no results
    await page.fill('input[placeholder*="Search"]', 'nonexistentquery12345')
    await page.press('input[placeholder*="Search"]', 'Enter')
    
    // Verify empty state
    await expect(page.locator('text=No results found')).toBeVisible()
  })

  test('should search with results', async ({ page }) => {
    // Create a note first
    await page.goto('/notes')
    await page.click('button:has-text("New Note")')
    await page.fill('textarea', 'Meeting notes for project discussion')
    await page.click('button:has-text("Save")')
    
    // Search for the note
    await page.click('button[aria-label="Search"]')
    await page.fill('input[placeholder*="Search"]', 'meeting')
    await page.press('input[placeholder*="Search"]', 'Enter')
    
    // Verify results
    await expect(page.locator('text=Meeting notes')).toBeVisible()
  })

  test('should apply filters', async ({ page }) => {
    await page.click('button[aria-label="Search"]')
    
    // Open filters
    await page.click('button[aria-label="Filter"]')
    
    // Apply status filter
    await page.click('text=Pinned only')
    
    // Apply label filter
    await page.click('text=Select labels')
    await page.click('text=Work')
    
    // Apply sort
    await page.selectOption('select[name="sortBy"]', 'updatedAt')
    
    // Submit search
    await page.click('button:has-text("Apply")')
    
    // Verify filters are applied
    await expect(page.locator('.search-results')).toBeVisible()
  })

  test('should clear filters', async ({ page }) => {
    await page.click('button[aria-label="Search"]')
    await page.fill('input[placeholder*="Search"]', 'test')
    
    // Open filters and apply some
    await page.click('button[aria-label="Filter"]')
    await page.click('text=Pinned only')
    
    // Clear filters
    await page.click('button:has-text("Clear")')
    
    // Verify filters are cleared
    await expect(page.locator('text=Pinned only')).not.toBeChecked()
  })
})
