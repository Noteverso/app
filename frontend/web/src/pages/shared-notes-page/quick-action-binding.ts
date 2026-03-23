type JsonPrimitive = string | number | boolean | null
type JsonValue = JsonPrimitive | JsonObject | JsonArray
type JsonArray = JsonValue[]
type JsonObject = {
  [key: string]: JsonValue;
}

export type QuickTokenBinding = {
  sanitizedContentJson: object;
  projectId: string | null;
  labelIds: string[];
}

function getStringAttr(node: JsonObject, ...keys: string[]) {
  for (const key of keys) {
    const value = node[key]
    if (typeof value === 'string' && value) {
      return value
    }
    if (typeof value === 'number' && Number.isFinite(value)) {
      return String(value)
    }
  }

  return ''
}

export function extractQuickTokenBinding(contentJson: object): QuickTokenBinding {
  const labelIds: string[] = []
  let projectId: string | null = null

  const sanitize = (value: JsonValue): JsonValue | null => {
    if (Array.isArray(value)) {
      const nextItems = value
        .map(item => sanitize(item))
        .filter((item): item is JsonValue => item !== null)
      return nextItems
    }

    if (value === null || typeof value !== 'object') {
      return value
    }

    const node = value as JsonObject
    const nodeType = typeof node.type === 'string' ? node.type : ''
    if (nodeType === 'quickActionToken') {
      const attrs = node.attrs
      if (attrs && typeof attrs === 'object' && !Array.isArray(attrs)) {
        const normalizedAttrs = attrs as JsonObject
        const tokenType = getStringAttr(normalizedAttrs, 'tokenType', 'tokentype')
        const entityId = getStringAttr(normalizedAttrs, 'entityId', 'entityid')
        if (tokenType === 'project' && entityId) {
          projectId = entityId
        }
        if (tokenType === 'label' && entityId && !labelIds.includes(entityId)) {
          labelIds.push(entityId)
        }
      }
      return null
    }

    const result: JsonObject = {}
    for (const [key, fieldValue] of Object.entries(node)) {
      const sanitized = sanitize(fieldValue as JsonValue)
      if (sanitized !== null) {
        result[key] = sanitized
      }
    }
    return result
  }

  return {
    sanitizedContentJson: (sanitize(contentJson as JsonValue) ?? {}) as object,
    projectId,
    labelIds,
  }
}
