import { Node } from '@tiptap/core'

export type QuickActionTokenType = 'project' | 'label'

export interface QuickActionTokenAttrs {
  tokenId: string;
  tokenType: QuickActionTokenType;
  entityId: string;
  label: string;
}

export const QuickActionToken = Node.create({
  name: 'quickActionToken',
  group: 'inline',
  inline: true,
  atom: true,
  selectable: true,

  addAttributes() {
    return {
      tokenId: { default: '' },
      tokenType: { default: 'label' },
      entityId: { default: '' },
      label: { default: '' },
    }
  },

  parseHTML() {
    return [{ tag: 'button[data-quick-action-token]' }]
  },

  renderHTML({ HTMLAttributes }) {
    const tokenType = HTMLAttributes.tokenType as QuickActionTokenType
    const label = String(HTMLAttributes.label ?? '')
    const prefix = tokenType === 'project' ? '#' : '@'

    return [
      'button',
      {
        ...HTMLAttributes,
        type: 'button',
        contenteditable: 'false',
        'data-quick-action-token': 'true',
        class: 'inline-flex items-center rounded-md border border-slate-300 bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-700',
      },
      `${prefix}${label}`,
    ]
  },
})
