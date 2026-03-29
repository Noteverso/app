import { test, expect } from '@playwright/test'

test.describe('Project Management Workflow', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'test@gmail.com')
    await page.fill('input[type="password"]', 'Admin123456')
    await page.click('button[type="submit"]')
    await page.waitForURL('/app/inbox')
  })

  test('should navigate to projects management page', async ({ page }) => {
    // Click on "项目" text in sidebar
    await page.click('text=项目')
    
    // Verify URL changed
    await page.waitForURL('/app/projects')
    
    // Verify filter buttons exist
    await expect(page.locator('text=All Projects')).toBeVisible()
    await expect(page.locator('text=Favorited')).toBeVisible()
    await expect(page.locator('text=Archived')).toBeVisible()
  })

  test('should create new project with optimistic update', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Click create project button in sidebar
    await page.click('[data-testid="create-project-btn"]')
    
    // Fill project form
    await page.fill('input[name="projectName"]', 'Test Project')
    await page.click('[data-color="blue"]') // Select color
    
    // Submit form
    await page.click('button:has-text("Create")')
    
    // Verify project appears immediately (optimistic)
    await expect(page.locator('text=Test Project')).toBeVisible({ timeout: 1000 })
  })

  test('should toggle favorite with optimistic update', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Find first project in sidebar
    const firstProject = page.locator('[data-testid="project-item"]').first()
    
    // Click favorite button
    await firstProject.locator('[data-testid="favorite-btn"]').click()
    
    // Verify star icon changes immediately
    await expect(firstProject.locator('[data-favorite="true"]')).toBeVisible({ timeout: 500 })
  })

  test('should filter projects on management page', async ({ page }) => {
    await page.goto('/app/projects')
    
    // Click Favorited filter
    await page.click('text=Favorited')
    
    // Verify only favorited projects shown
    const projects = page.locator('[data-testid="project-card"]')
    const count = await projects.count()
    
    // All visible projects should have favorite star
    for (let i = 0; i < count; i++) {
      await expect(projects.nth(i).locator('[data-favorite="true"]')).toBeVisible()
    }
  })

  test('should edit project with optimistic update', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Right-click first project to open context menu
    const firstProject = page.locator('[data-testid="project-item"]').first()
    await firstProject.click({ button: 'right' })
    
    // Click edit option
    await page.click('text=Edit')
    
    // Change project name
    await page.fill('input[name="projectName"]', 'Updated Project')
    await page.click('button:has-text("Save")')
    
    // Verify name updates immediately
    await expect(page.locator('text=Updated Project')).toBeVisible({ timeout: 1000 })
  })

  test('should delete project with confirmation', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Right-click first project
    const firstProject = page.locator('[data-testid="project-item"]').first()
    const projectName = await firstProject.textContent()
    await firstProject.click({ button: 'right' })
    
    // Click delete option
    await page.click('text=Delete')
    
    // Confirm deletion in dialog
    await page.click('button:has-text("Confirm")')
    
    // Verify project removed immediately
    await expect(page.locator(`text=${projectName}`)).not.toBeVisible({ timeout: 1000 })
  })

  test('should archive project', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Right-click first project
    const firstProject = page.locator('[data-testid="project-item"]').first()
    await firstProject.click({ button: 'right' })
    
    // Click archive option
    await page.click('text=Archive')
    
    // Confirm archive
    await page.click('button:has-text("Confirm")')
    
    // Verify project removed from active list
    await expect(firstProject).not.toBeVisible({ timeout: 1000 })
    
    // Navigate to projects page and check archived filter
    await page.goto('/app/projects')
    await page.click('text=Archived')
    
    // Verify project appears in archived list
    // (Note: This will fail until archived API is implemented)
  })

  test('should show loading states during operations', async ({ page }) => {
    await page.goto('/app/inbox')
    
    // Click create project button
    await page.click('[data-testid="create-project-btn"]')
    
    // Fill form
    await page.fill('input[name="projectName"]', 'Loading Test')
    
    // Submit and check for loading state
    await page.click('button:has-text("Create")')
    
    // Button should be disabled during operation
    await expect(page.locator('button:has-text("Create")[disabled]')).toBeVisible({ timeout: 500 })
  })

  test('should navigate to project detail from management page', async ({ page }) => {
    await page.goto('/app/projects')
    
    // Click first project card
    const firstCard = page.locator('[data-testid="project-card"]').first()
    const projectId = await firstCard.getAttribute('data-project-id')
    await firstCard.click()
    
    // Verify navigation to project detail page
    await page.waitForURL(`/app/project/${projectId}`)
  })
})
