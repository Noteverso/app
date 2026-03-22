# CLAUDE.md

Reusable collaboration guide for AI coding agents.

## Project instructions

- This monorepo uses host-provided external services for E2E.
- Do not start frontend, backend, db, or redis inside Codex unless explicitly asked.

## 1) Core Working Agreement

1. Before writing code, provide a short implementation approach and wait for approval.
2. Before killing any process/port or running force-cleanup commands, check current state and wait for approval.
3. If requirements are ambiguous, ask clarifying questions before implementation.
4. After code changes, always list edge cases and suggest test cases.
5. If a task needs changes in more than 3 files, split into smaller sub-tasks first.
6. When corrected, reflect briefly on what failed and how to prevent recurrence.
7. Test philosophy:
   1. Investigate root cause.
   2. Fix the bug.
   3. Keep/add tests to prevent regression.

## 2) Plan-to-Execution Handoff

- In Plan Mode, once the user explicitly approves and asks to execute (for example: "Implement the plan"), treat that as execution approval.
- After this handoff, do not pause again for intermediate coding/testing approvals.
- Only pause when:
  - destructive or high-risk actions are required,
  - external credentials/access are missing,
  - product decisions are still ambiguous and cannot be inferred.

## 3) Autonomous Execution Loop (After Approval)

Run continuously until completion:

1. Implement changes.
2. Continuously run type/compile checks during implementation:
   - Frontend (TypeScript): run typecheck every logical batch and once more before finalizing.
   - Backend (Java): run compile check every logical batch and once more before finalizing.
3. Run relevant unit/integration tests.
4. Run affected user-flow checks (e2e/browser automation where applicable).
5. If failures appear, fix and rerun.
6. Return final summary with:
   - what changed,
   - test evidence,
   - edge cases and residual risks.

Do not stop before integration/e2e validation unless blocked by a real external dependency.

## 4) Safety and Change Control

- Prefer non-destructive commands by default.
- Never use force-reset/destructive cleanup unless explicitly approved.
- Do not revert unrelated local changes.
- Use minimal scope edits and keep diffs focused.

## 5) Testing Standards

- Add or update tests for each behavior change.
- Include at least:
  - happy path,
  - failure path,
  - boundary/edge inputs.
- If skipping tests, explain why and what risk remains.
- Prefer deterministic, fast tests first; run broader suites when risk warrants.
- Type/compile check frequency guidance:
  - small change: once after implementation + once before final response,
  - medium/large change: every logical sub-task + final full pass.

## 6) Browser Automation Lifecycle (agent-browser / Playwright-like tools)

- Reuse a single session name per task (for example: `qa`) when possible.
- At task end, always run teardown:
  1. list active sessions,
  2. close all sessions created in this task,
  3. report residual processes and request approval before force cleanup.
- If environment enforces permission prompts, use approved command prefixes and persistent policy where available.

## 7) Command and Environment Conventions

- Keep project-specific startup/test commands in dedicated docs or scripts:
  - `docs/RUNNING.md`
  - `docs/RUNNING_TEST.md`
  - `scripts/*.sh`
- In this file, keep only reusable policy and workflow rules.
- Prefer stable script entry points over long inline shell pipelines.
- Summary-style documents must be saved under `docs/summary/*/**.md` (`summary` subfolders are the category).

## 8) Domain-Specific Rules (Optional Extension Point)

Project/domain-specific patterns (for example, data validation conventions or mapper/service contracts) should live in dedicated docs and be referenced here:

- Example: `docs/patterns/empty-collection-handling.md`
- Example: `docs/patterns/api-error-handling.md`

Keep this file concise and policy-focused.
