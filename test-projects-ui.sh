#!/bin/bash
# Manual UI Testing Script for Projects Management
# Run with: bash test-projects-ui.sh

echo "=== Projects Management UI Testing ==="
echo ""

# Test 1: Navigate to projects management page
echo "Test 1: Navigate to projects management page"
echo "Command: agent-browser open http://localhost:5173/app/inbox"
echo "Then: agent-browser snapshot -i"
echo "Then: agent-browser click @<projects-text-ref>"
echo "Expected: URL changes to /app/projects"
echo ""

# Test 2: Verify projects management page elements
echo "Test 2: Verify projects management page"
echo "Command: agent-browser snapshot -i"
echo "Expected: See filter buttons (All Projects, Favorited, Archived)"
echo "Expected: See project cards in grid layout"
echo ""

# Test 3: Create new project
echo "Test 3: Create new project"
echo "Command: agent-browser click @<create-project-btn-ref>"
echo "Then: agent-browser fill @<project-name-input-ref> 'Test Project'"
echo "Then: agent-browser click @<color-blue-ref>"
echo "Then: agent-browser click @<create-btn-ref>"
echo "Expected: Project appears immediately in sidebar"
echo ""

# Test 4: Toggle favorite
echo "Test 4: Toggle favorite"
echo "Command: agent-browser click @<favorite-btn-ref>"
echo "Expected: Star icon changes immediately"
echo ""

# Test 5: Filter projects
echo "Test 5: Filter projects"
echo "Command: agent-browser click @<favorited-filter-ref>"
echo "Expected: Only favorited projects shown"
echo ""

# Test 6: Edit project
echo "Test 6: Edit project"
echo "Command: Right-click project, click Edit"
echo "Then: Change name and save"
echo "Expected: Name updates immediately"
echo ""

# Test 7: Delete project
echo "Test 7: Delete project"
echo "Command: Right-click project, click Delete"
echo "Then: Confirm deletion"
echo "Expected: Project removed immediately"
echo ""

echo "=== Testing Instructions ==="
echo "1. Make sure backend and frontend are running"
echo "2. Open browser to http://localhost:5173"
echo "3. Login with test credentials"
echo "4. Follow the test steps above using agent-browser"
echo ""
echo "=== Agent Browser Commands ==="
echo "agent-browser open <url>          - Navigate to URL"
echo "agent-browser snapshot -i         - Get interactive elements"
echo "agent-browser click @<ref>        - Click element"
echo "agent-browser fill @<ref> 'text'  - Fill input"
echo "agent-browser screenshot          - Take screenshot"
