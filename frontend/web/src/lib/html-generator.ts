// Simple HTML generator for JSON content
// This is a fallback implementation that doesn't require @tiptap/html

export function generateHtmlFromJson(contentJson: object): string {
  if (!contentJson) {
    return ''
  }

  try {
    // Simple implementation - extract text content from JSON
    const jsonStr = JSON.stringify(contentJson)
    
    // Extract text content using regex (basic implementation)
    const textMatches = jsonStr.match(/"text":"([^"]+)"/g)
    if (!textMatches) {
      return ''
    }

    const textContent = textMatches
      .map(match => match.replace(/"text":"([^"]+)"/, '$1'))
      .join(' ')

    return `<p>${escapeHtml(textContent)}</p>`
  } catch (error) {
    console.error('Failed to generate HTML from JSON:', error)
    return ''
  }
}

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}
