# Note Editor UX Simplification Plan & Progress

## Phases
- [x] Phase 1: Add fixed in-editor `#/@` quick-action menu and editor overlay support.
- [x] Phase 2: Remove project dropdown, move chips/suggestions into editor body, wire icon-driven picker flow.
- [x] Phase 3: Add save-time cleanup to strip only menu-inserted quick-action tokens from saved content.
- [x] Phase 4: Update e2e flow for new in-editor quick-action interactions.
- [x] Phase 5: Run final checks and close all phases.

## Execution Rules
- Max 3 changed files per phase.
- Run `pnpm --filter @noteverso/web exec tsc --noEmit` after each phase.
- Mark phase complete here immediately after passing phase checks.

## Progress Log
- Phase 1 completed:
  - Updated `frontend/web/src/features/editor/text-editor.tsx`
  - Added fixed `#/@` quick-action menu inside editor body.
  - Added `quickActionOverlay` slot to render chips/suggestions inside editor.
  - Added `insertQuickActionToken` method on editor ref.
- Phase 2 completed:
  - Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`
  - Removed project dropdown from composer footer.
  - Moved quick suggestions and selected chips into in-editor overlay.
  - Wired fixed `#/@` icon menu to open quick-action pickers.
- Phase 3 completed:
  - Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`
  - Added controlled quick-action token tracking for icon-inserted tokens.
  - Sanitized `contentJson` before submit to strip only controlled `#/@` tokens.
  - Cleared controlled token state after successful save.
- Phase 4 completed:
  - Updated `frontend/web/e2e/quick-actions-agent-browser.e2e.sh`
  - Switched project assignment flow to fixed in-editor `#` icon menu.
  - Updated assertions to check in-editor project chip and empty editor reset.
- Phase 5 completed:
  - Updated `frontend/web/src/pages/shared-notes-page/action.ts` return type to remove `any`.
  - Updated `frontend/web/src/pages/shared-notes-page/action.test.ts` for nullable action return.
  - Final checks:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
    - `pnpm web:test` passed (2 files, 5 tests).
    - `pnpm web:build` passed.

## Follow-up Task: Edge Cases & Additional Tests
- [x] Move `#/@` menu icons into the top formatting toolbar.
- [x] Remove editor-body fixed quick-action strip and reduce visual separation.
- [x] Keep `#project/@label` inline in note content instead of separate chip lane.
- [x] Handle edge cases by sanitizing only wrapped system-inserted tokens.
- [x] Add additional tests for punctuation, multiline, and manual token preservation.

### Follow-up Execution Log
- Updated `frontend/web/src/features/editor/text-editor.tsx`:
  - Moved quick-action icons to formatting toolbar area.
  - Replaced bottom fixed action lane with toolbar-integrated controls.
  - Unified editor visual sections by reducing extra splits.
- Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`:
  - Removed separate quick-action chip lane from editor body.
  - Switched save sanitizer usage to wrapped-token-based cleanup.
- Added `frontend/web/src/pages/shared-notes-page/quick-action-sanitize.ts`:
  - Wrapped-token strategy for icon-inserted quick actions.
  - JSON sanitizer that removes only wrapped tokens, preserving manual hashtags/mentions.
- Added `frontend/web/src/pages/shared-notes-page/quick-action-sanitize.test.ts`:
  - Validates manual token retention.
  - Validates punctuation/multiline cleanup.
  - Validates multi-word and special-character token cleanup.
- Follow-up checks:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
  - `pnpm web:test` passed (3 files, 8 tests).

## Follow-up Task 2: Token Button UX & Cursor-Anchored Dropdown
- [x] Move `#/@` interaction to toolbar and show dropdown near cursor.
- [x] Render active tokens as inline button-style nodes in editor content.
- [x] Enforce latest `#project` token active while older project tokens downgrade to plain text.
- [x] Pin save button to editor bottom and cap editor content max height at `60vw`.
- [x] Add binding extraction tests for token-node serialization behavior.

### Follow-up Task 2 Log
- Added `frontend/web/src/features/editor/quick-action-token.ts` for inline atomic token node.
- Updated `frontend/web/src/features/editor/text-editor.tsx`:
  - Added token insert/replace/cursor-anchor methods.
  - Added token click callback support.
  - Added editor footer slot and `max-h-[60vw]` scrollable content area.
- Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`:
  - Switched quick-action dropdown to cursor/token anchored fixed popup.
  - Wired token click to reopen dropdown and replace token value.
  - Saved payload now derives from token-node extraction (`projectId`, `labels`, sanitized content).
- Added `frontend/web/src/pages/shared-notes-page/quick-action-binding.ts` and tests:
  - `frontend/web/src/pages/shared-notes-page/quick-action-binding.test.ts`
- Validation:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
  - `pnpm web:test` passed (4 files, 10 tests).

## Follow-up Task 3: Layout Header Simplification & Fixed Editor Height
- [x] Phase A: Set editor content max height to `280px`.
- [x] Phase B: Move desktop sidebar toggle into left navigation area.
- [x] Phase C: Remove right content header and add top inline controls in content area.
- [x] Phase D: Add/adjust tests for layout control visibility and editor height.

### Follow-up Task 3 Log
- Phase A completed:
  - Updated `frontend/web/src/features/editor/text-editor.tsx`
  - Changed editor content max height from `60vw` to fixed `280px`.
  - Validation:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
- Phase B completed:
  - Updated `frontend/web/src/layout/nav/nav.tsx`
  - Moved desktop sidebar toggle button to stay inside left navigation header.
  - Validation:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
- Phase C completed:
  - Updated `frontend/web/src/layout/layout.tsx`
  - Removed right content header and moved controls into content-top inline row.
  - Mobile now uses a compact top button; desktop shows restore toggle only when sidebar is hidden.
  - Validation:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
- Phase D (part 1) completed:
  - Updated `frontend/web/src/features/editor/text-editor.tsx`
  - Updated `frontend/web/src/features/editor/text-editor.test.ts`
  - Added fixed-height class constant test coverage for `280px`.
  - Validation:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
- Phase D (part 2) completed:
  - Updated `frontend/web/src/layout/layout.tsx`
  - Added `frontend/web/src/layout/layout.test.ts`
  - Added pure-function coverage for top-control visibility and desktop breadcrumb toggle display logic.
  - Validation:
    - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
    - `pnpm web:test` passed (5 files, 13 tests).

## Follow-up Task 4: Hidden Sidebar Breadcrumb Line with Project Name
- [x] Replace hidden-sidebar restore icon with inline breadcrumb line containing current project title.
- [x] Keep mobile top nav trigger while reducing desktop visual separation.
- [x] Extend layout tests for route-to-title resolution.

### Follow-up Task 4 Log
- Updated `frontend/web/src/layout/layout.tsx`:
  - Replaced hidden-state desktop restore icon with inline button (`PanelLeft + current title`) in main content top row.
  - Added `getHiddenSidebarTitle(pathname, projects)` mapping for inbox/project/labels/attachments routes.
- Updated `frontend/web/src/layout/layout.test.ts`:
  - Added hidden-sidebar title resolution assertions for project and inbox routes.
- Validation:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
  - `pnpm web:test` passed (5 files, 14 tests).

## Follow-up Task 5: Hidden Sidebar Toggle Beside Page H1
- [x] Remove breadcrumb-like title line from layout top controls.
- [x] Show only icon toggle beside current page `h1` title when sidebar is hidden.
- [x] Keep mobile top nav trigger behavior unchanged.

### Follow-up Task 5 Log
- Updated `frontend/web/src/layout/layout.tsx`:
  - Removed hidden-sidebar desktop title line from the global top controls row.
  - Passed `isSidebarVisible` and `onToggleSidebar` through outlet context.
- Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`:
  - Added hidden-sidebar desktop icon button to the left of the page `h1`.
- Updated `frontend/web/src/layout/layout.test.ts`:
  - Removed title-resolution assertions tied to removed breadcrumb behavior.
- Validation:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
  - `pnpm web:test` passed (5 files, 13 tests).

## Follow-up Task 6: Remove Extra Main-Top Wrapper Gap
- [x] Remove desktop empty top-controls spacing by making main-top controls mobile-only.
- [x] Increase title row whitespace slightly for better readability.
- [x] Adjust layout tests to match simplified top-controls behavior.

### Follow-up Task 6 Log
- Updated `frontend/web/src/layout/layout.tsx`:
  - Simplified top controls class to mobile-only, preventing empty desktop row when sidebar is hidden.
- Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`:
  - Adjusted title row spacing (`mb-5`, `gap-3`) for more breathing room.
- Updated `frontend/web/src/layout/layout.test.ts`:
  - Replaced old sidebar-state assertions with mobile-only top-control class assertion.
- Validation:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
  - `pnpm web:test` passed (5 files, 12 tests).

## Follow-up Task 7: Add Top Margin for H1 Parent Row
- [x] Increase top spacing on the `h1` parent container in shared notes page.

### Follow-up Task 7 Log
- Updated `frontend/web/src/pages/shared-notes-page/shared-notes-page.tsx`:
  - Added top margin to title row container (`mt-2`).
- Validation:
  - `pnpm --filter @noteverso/web exec tsc --noEmit` passed.
