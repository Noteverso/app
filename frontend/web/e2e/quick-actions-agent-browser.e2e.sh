#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:5174}"
SESSION="${SESSION:-quickactions-e2e}"
EMAIL="${E2E_EMAIL:-byodian@gmail.com}"
PASSWORD="${E2E_PASSWORD:-Admin123456}"
LABEL_NAME="qa_label_$(date +%s)"

run() {
  agent-browser --session "$SESSION" "$@"
}

assert_contains() {
  local haystack="$1"
  local needle="$2"
  local message="$3"
  if [[ "$haystack" != *"$needle"* ]]; then
    echo "Assertion failed: $message"
    echo "Expected to find: $needle"
    echo "In:"
    echo "$haystack"
    exit 1
  fi
}

echo "[1/7] Open login page"
run open "$BASE_URL/auth/login" >/dev/null

echo "[2/7] Login"
run fill 'input[type="email"]' "$EMAIL" >/dev/null
run fill 'input[type="password"]' "$PASSWORD" >/dev/null
run click 'button[type="submit"]' >/dev/null
run wait 1200 >/dev/null
login_url="$(run get url)"
assert_contains "$login_url" "/app/inbox" "Login should redirect to inbox"

echo "[3/7] Type content and bind project from in-editor # menu"
run click '.tiptap.ProseMirror' >/dev/null
run keyboard type 'quick-actions-e2e ' >/dev/null
run click 'button[aria-label="Quick assign project"]' >/dev/null
run click 'button:has-text("MySQL")' >/dev/null
project_chip_state="$(run eval "(() => Array.from(document.querySelectorAll('span')).some(el => (el.textContent || '').includes('MySQL')))()")"
assert_contains "$project_chip_state" "true" "Project chip should reflect selected #project"

echo "[4/7] Bind label with @ token (create on select)"
run keyboard type " @$LABEL_NAME" >/dev/null
run click "button:has-text(\"$LABEL_NAME Create\")" >/dev/null
remove_button_state="$(run eval "(() => !!document.querySelector('button[aria-label=\"Remove $LABEL_NAME\"]'))()")"
assert_contains "$remove_button_state" "true" "Label chip remove button should exist after @label selection"

echo "[5/7] Ensure save is enabled"
save_state="$(run eval "(() => Array.from(document.querySelectorAll('button')).find(b => b.textContent?.includes('保存'))?.hasAttribute('disabled') ?? true)()")"
assert_contains "$save_state" "false" "Save button should be enabled"

echo "[6/7] Save note and verify post-save state"
run click 'button:has-text("保存")' >/dev/null
run wait 1500 >/dev/null
editor_text_length_after_save="$(run eval "(() => (document.querySelector('.tiptap.ProseMirror')?.textContent || '').trim().length)()")"
save_state_after_save="$(run eval "(() => Array.from(document.querySelectorAll('button')).find(b => b.textContent?.includes('保存'))?.hasAttribute('disabled') ?? false)()")"
assert_contains "$editor_text_length_after_save" "0" "Editor should be empty after save reset"
assert_contains "$save_state_after_save" "true" "Save should be disabled after successful reset"

echo "[7/7] E2E quick-action flow passed"
