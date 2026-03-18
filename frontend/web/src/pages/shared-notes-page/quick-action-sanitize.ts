type JsonPrimitive = string | number | boolean | null
type JsonValue = JsonPrimitive | JsonObject | JsonArray
type JsonArray = JsonValue[]
type JsonObject = {
  [key: string]: JsonValue;
}

const QUICK_ACTION_TOKEN_START = '\u2063'
const QUICK_ACTION_TOKEN_END = '\u2064'

function removeWrappedQuickActionTokens(text: string): string {
  const tokenPattern = new RegExp(`${QUICK_ACTION_TOKEN_START}[^${QUICK_ACTION_TOKEN_END}\r\n]+${QUICK_ACTION_TOKEN_END}`, 'g')
  const hasWrappedToken = text.includes(QUICK_ACTION_TOKEN_START) && text.includes(QUICK_ACTION_TOKEN_END)
  const withoutTokens = text.replace(tokenPattern, '')

  const normalized = withoutTokens
    .replace(/ +/g, ' ')
    .replace(/\s+([,.;!?])/g, '$1')
    .replace(/ \n/g, '\n')
    .replace(/\n /g, '\n')

  return hasWrappedToken ? normalized.trim() : normalized
}

export function wrapQuickActionTokenForEditor(token: string): string {
  return `${QUICK_ACTION_TOKEN_START}${token}${QUICK_ACTION_TOKEN_END}`
}

export function sanitizeQuickActionContentJson(contentJson: object): object {
  const sanitize = (value: JsonValue): JsonValue => {
    if (Array.isArray(value)) {
      return value.map(item => sanitize(item))
    }

    if (value === null || typeof value !== 'object') {
      return value
    }

    const result: JsonObject = {}
    for (const [key, fieldValue] of Object.entries(value)) {
      if (key === 'text' && typeof fieldValue === 'string') {
        result[key] = removeWrappedQuickActionTokens(fieldValue)
      } else {
        result[key] = sanitize(fieldValue as JsonValue)
      }
    }

    return result
  }

  return sanitize(contentJson as JsonValue) as object
}
