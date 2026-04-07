import { type KeyboardEvent as ReactKeyboardEvent, type ReactNode, forwardRef, useCallback, useImperativeHandle, useRef, useState } from 'react'
import {
  EditorContent,
  useEditor,
} from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Link from '@tiptap/extension-link'
import Highlight from '@tiptap/extension-highlight'
import Image from '@tiptap/extension-image'
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight'
import TextStyle from '@tiptap/extension-text-style'
import Placeholder from '@tiptap/extension-placeholder'
import TaskItem from '@tiptap/extension-task-item'
import TaskList from '@tiptap/extension-task-list'
import { Color } from '@tiptap/extension-color'
import {
  Bold,
  Code,
  CodeXml,
  Heading1,
  Heading2,
  Heading3, Highlighter, Image as ImageIcon, Italic, Link as LinkIcon, List, ListChecks, ListOrdered, Palette, Pilcrow, RemoveFormatting, Strikethrough,
  TextQuote,
  Hash,
  AtSign,
} from 'lucide-react'
import classnames from 'classnames'
import { common, createLowlight } from 'lowlight'
import css from 'highlight.js/lib/languages/css'
import js from 'highlight.js/lib/languages/javascript'
import ts from 'highlight.js/lib/languages/typescript'
import html from 'highlight.js/lib/languages/xml'
import { PopoverTrigger } from '@radix-ui/react-popover'
import { TrailingNode } from './tailing-node'
import { Button } from '@/components/ui/button/button'

import {
  Popover,
  PopoverContent,
} from '@/components/ui/popover/popover'
import { Input } from '@/components/ui/input/input'
import { QuickActionToken, type QuickActionTokenAttrs, type QuickActionTokenType } from './quick-action-token'

const lowlight = createLowlight(common)
lowlight.register('html', html)
lowlight.register('css', css)
lowlight.register('js', js)
lowlight.register('ts', ts)

export interface TextEditorProps {
  className?: string;
  onChange?: (contentJson: object, hasContent: boolean) => void;
  onQuickActionQuery?: (query: QuickActionQuery | null) => void;
  onQuickActionKeyDown?: (event: globalThis.KeyboardEvent) => boolean;
  onQuickActionIconClick?: (type: 'project' | 'label') => void;
  quickActionPanel?: ReactNode;
  onQuickActionTokenClick?: (payload: QuickActionTokenClickPayload) => void;
  footer?: ReactNode;
}

export interface QuickActionTokenClickPayload extends QuickActionTokenAttrs {
  rect: DOMRect;
}

export interface EditorMethods {
  reset: () => void;
  setContentJson: (contentJson: object) => void
  consumeQuickActionToken: () => void
  insertQuickActionToken: (payload: QuickActionTokenAttrs) => void
  replaceQuickActionToken: (tokenId: string, payload: QuickActionTokenAttrs) => void
  getCursorAnchor: () => { left: number; top: number; bottom: number } | null
  // isEmpty: () => boolean;
}

export interface QuickActionQuery {
  type: 'project' | 'label';
  keyword: string;
  token: string;
}

export const EDITOR_CONTENT_MAX_HEIGHT_CLASS = 'max-h-[280px]'

export function parseQuickActionFromTextBefore(textBefore: string): QuickActionQuery | null {
  const match = textBefore.match(/(?:^|\s)([#@][^\s#@.,!?;:(){}\[\]"'`]+)$/)

  if (!match) {
    return null
  }

  const token = match[1]
  return {
    type: token.startsWith('#') ? 'project' : 'label',
    keyword: token.slice(1),
    token,
  }
}

const TextEditor = forwardRef<EditorMethods, TextEditorProps>(({ className = '', onChange, onQuickActionQuery, onQuickActionKeyDown, onQuickActionIconClick, quickActionPanel, onQuickActionTokenClick, footer }: TextEditorProps, ref) => {
  const [isImagePopoverOpen, setIsImagePopoverOpen] = useState(false)
  const [isLinkPopoverOpen, setIsLinkPopoverOpen] = useState(false)
  const [textLink, setTextLink] = useState('')
  const [imageLink, setImageLink] = useState('')
  const quickActionRangeRef = useRef<{ from: number; to: number } | null>(null)

  const emitQuickActionQuery = useCallback((editorInstance: NonNullable<typeof editor>) => {
    if (!onQuickActionQuery) {
      return
    }

    const selection = editorInstance.state.selection
    if (!selection.empty) {
      quickActionRangeRef.current = null
      onQuickActionQuery(null)
      return
    }

    const from = selection.from
    const textBefore = selection.$from.parent.textBetween(0, selection.$from.parentOffset, ' ', ' ')
    const query = parseQuickActionFromTextBefore(textBefore)
    if (!query) {
      quickActionRangeRef.current = null
      onQuickActionQuery(null)
      return
    }

    const token = query.token
    quickActionRangeRef.current = {
      from: from - token.length,
      to: from,
    }

    onQuickActionQuery(query)
  }, [onQuickActionQuery])

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        heading: {
          levels: [1, 2, 3],
        },
        codeBlock: false,
      }),
      Link,
      Highlight,
      Image,
      CodeBlockLowlight.configure({
        lowlight,
      }),
      TextStyle,
      Color,
      Placeholder.configure({
        placeholder: 'Writing something, or press \'/\' for commands',
      }),
      TrailingNode,
      TaskList,

      TaskItem.configure({
        nested: true,
      }),
      QuickActionToken,
    ],
    content: '',
    editorProps: {
      attributes: {
        class: 'prose prose-sm sm:prose-base focus:outline-none max-w-full',
      },
      handleKeyDown: (_, event) => onQuickActionKeyDown?.(event) ?? false,
      handleClickOn: (_, __, node, nodePos) => {
        if (node.type.name !== 'quickActionToken') {
          return false
        }

        const tokenDom = editor?.view.nodeDOM(nodePos)
        if (!(tokenDom instanceof HTMLElement)) {
          return false
        }

        const attrs = node.attrs as QuickActionTokenAttrs
        onQuickActionTokenClick?.({
          tokenId: attrs.tokenId,
          tokenType: attrs.tokenType,
          entityId: attrs.entityId,
          label: attrs.label,
          rect: tokenDom.getBoundingClientRect(),
        })
        return true
      },
    },
    onUpdate: (props) => {
      const contentJson = props.editor?.getJSON() || {}
      onChange && onChange(contentJson, !props.editor?.isEmpty)
      if (props.editor) {
        emitQuickActionQuery(props.editor)
      }
    },
  })

  useImperativeHandle(ref, () => ({
    reset() {
      quickActionRangeRef.current = null
      onQuickActionQuery?.(null)
      editor?.commands.clearContent(true)
    },
    setContentJson(contentJson: object) {
      editor?.commands.setContent(contentJson)
    },
    consumeQuickActionToken() {
      if (!editor || !quickActionRangeRef.current) {
        return
      }

      editor.chain().focus().deleteRange(quickActionRangeRef.current).run()
      quickActionRangeRef.current = null
      onQuickActionQuery?.(null)
    },
    insertQuickActionToken(token: QuickActionTokenAttrs) {
      if (!editor) {
        return
      }

      const tokenType = editor.schema.nodes.quickActionToken
      if (!tokenType) {
        return
      }

      let tr = editor.state.tr
      if (token.tokenType === 'project') {
        const replacements: Array<{ from: number; to: number; label: string }> = []
        editor.state.doc.descendants((node, pos) => {
          if (node.type.name === 'quickActionToken' && (node.attrs.tokenType as QuickActionTokenType) === 'project') {
            replacements.push({
              from: pos,
              to: pos + node.nodeSize,
              label: String(node.attrs.label),
            })
          }
        })

        replacements
          .sort((a, b) => b.from - a.from)
          .forEach((replacement) => {
            tr = tr.replaceWith(replacement.from, replacement.to, editor.state.schema.text(`#${replacement.label}`))
          })
      }

      const insertPos = tr.mapping.map(editor.state.selection.from)
      tr = tr.insert(insertPos, tokenType.create(token))
      tr = tr.insertText(' ', insertPos + 1)
      editor.view.dispatch(tr.scrollIntoView())
    },
    replaceQuickActionToken(tokenId: string, payload: QuickActionTokenAttrs) {
      if (!editor) {
        return
      }

      const tokenType = editor.schema.nodes.quickActionToken
      if (!tokenType) {
        return
      }

      let tr = editor.state.tr
      const replacements: Array<{ from: number; to: number; node: { type: 'token' | 'text'; label: string } }> = []

      editor.state.doc.descendants((node, pos) => {
        if (node.type.name !== 'quickActionToken') {
          return
        }

        const attrs = node.attrs as QuickActionTokenAttrs
        if (attrs.tokenType !== payload.tokenType) {
          return
        }

        if (payload.tokenType === 'project') {
          if (attrs.tokenId === tokenId) {
            replacements.push({
              from: pos,
              to: pos + node.nodeSize,
              node: { type: 'token', label: payload.label },
            })
          } else {
            replacements.push({
              from: pos,
              to: pos + node.nodeSize,
              node: { type: 'text', label: attrs.label },
            })
          }
          return
        }

        if (attrs.tokenId === tokenId) {
          replacements.push({
            from: pos,
            to: pos + node.nodeSize,
            node: { type: 'token', label: payload.label },
          })
        }
      })

      replacements
        .sort((a, b) => b.from - a.from)
        .forEach((replacement) => {
          if (replacement.node.type === 'token') {
            tr = tr.replaceWith(replacement.from, replacement.to, tokenType.create(payload))
          } else {
            tr = tr.replaceWith(replacement.from, replacement.to, editor.state.schema.text(`#${replacement.node.label}`))
          }
        })

      editor.view.dispatch(tr.scrollIntoView())
    },
    getCursorAnchor() {
      if (!editor) {
        return null
      }

      const selectionPos = editor.state.selection.from
      const coords = editor.view.coordsAtPos(selectionPos)
      return {
        left: coords.left,
        top: coords.top,
        bottom: coords.bottom,
      }
    },
  }))

  const handleLinkPaste = useCallback((event: ReactKeyboardEvent) => {
    if (event.key === 'Enter') {
      if (textLink === '') {
        editor?.chain().focus().extendMarkRange('link').unsetLink().run()
        return
      }

      editor?.chain().focus().extendMarkRange('link').setLink({ href: textLink }).run()
      setIsLinkPopoverOpen(false)
      setTextLink('')
    }
  }, [editor, textLink])

  const handleImagePaste = useCallback((event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      if (imageLink === '') {
        return
      }

      editor?.chain().focus().setImage({ src: imageLink }).run()
      setIsImagePopoverOpen(false)
      setImageLink('')
    }
  }, [editor, imageLink])

  const buttonVariants = (tag: string, options = {}) => {
    const isActive = editor?.isActive(tag, options) ? 'bg-white text-slate-900 ring-1 ring-slate-200 shadow-sm' : ''

    return classnames(isActive, 'hover:text-slate-800 hover:bg-white/90')
  }

  return (
    <div className={`${className}`}>
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center gap-1 px-3 pb-1 pt-3">
          {editor && <div className="Button-group flex flex-wrap items-center gap-1">
            <Button
              type="button"
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={() => onQuickActionIconClick?.('project')}
              aria-label="Quick assign project"
            >
              <Hash className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={() => onQuickActionIconClick?.('label')}
              aria-label="Quick assign label"
            >
              <AtSign className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleHeading({ level: 1 }).run()}
              className={buttonVariants('heading', { level: 1 })}
            >
              <Heading1 className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}
              className={buttonVariants('heading', { level: 2 })}
            >
              <Heading2 className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleHeading({ level: 3 }).run()}
              className={buttonVariants('heading', { level: 3 })}
            >
              <Heading3 className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().setParagraph().run()}
              className={buttonVariants('paragraph')}
            >
              <Pilcrow className="h-4 w-4" />
            </Button>
            <Button
              onClick={() => editor.chain().focus().toggleBold().run()}
              variant="ghost"
              size="icon"
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleBold()
                  .run()
              }
              className={buttonVariants('bold')}
            >
              <Bold className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleItalic().run()}
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleItalic()
                  .run()
              }
              className={buttonVariants('italic')}
            >
              <Italic className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleStrike().run()}
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleStrike()
                  .run()
              }
              className={buttonVariants('strike')}
            >
              <Strikethrough className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleCode().run()}
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleCode()
                  .run()
              }
              className={buttonVariants('code')}
            >
              <Code className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleCodeBlock().run()}
              className={buttonVariants('codeBlock')}
            >
              <CodeXml className="h-4 w-4" />
            </Button>

            <Popover open={isLinkPopoverOpen} onOpenChange={() => {
              if (!editor.can().chain().focus().toggleLink({ href: textLink }).run()) {
                return
              }
              setIsLinkPopoverOpen(!isLinkPopoverOpen)
            }}>
              <PopoverTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className={buttonVariants('link')}
                  onClick={() => setTextLink(editor.getAttributes('link').href)}
                  disabled={
                    !editor.can()
                      .chain()
                      .focus()
                      .toggleLink({ href: textLink })
                      .run()
                  }
                >
                  <LinkIcon className="h-4 w-4" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="p-0 outline-0 border-none shadow-none">
                <div className="flex">
                  <Input
                    className="h-8"
                    placeholder="paste the link and enter to save"
                    onKeyDown={handleLinkPaste}
                    value={textLink}
                    onChange={e => setTextLink(e.target.value)}
                  />
                </div>
              </PopoverContent>
            </Popover>

            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().unsetAllMarks().clearNodes().run()}
              className={buttonVariants('unsetAllMarks')}
            >
              <RemoveFormatting className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleBulletList().run()}
              className={buttonVariants('bulletList')}
            >
              <List className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleOrderedList().run()}
              className={buttonVariants('orderedList')}
            >
              {/* ListChecks */}
              {/* ListTodo */}
              <ListOrdered className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleTaskList().run()}
              className={buttonVariants('taskList')}
            >
              {/* ListChecks */}
              <ListChecks className="h-4 w-4" />
            </Button>

            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleBlockquote().run()}
              className={buttonVariants('blockquote')}
            >
              <TextQuote className="h-4 w-4" />
            </Button>
            {/* <Button */}
            {/*   variant="ghost" */}
            {/*   size="icon" */}
            {/*   onClick={() => editor.chain().focus().undo().run()} */}
            {/*   disabled={ */}
            {/*     !editor.can() */}
            {/*       .chain() */}
            {/*       .focus() */}
            {/*       .undo() */}
            {/*       .run() */}
            {/*   } */}
            {/* > */}
            {/*   <Undo className="h-4 w-4" /> */}
            {/* </Button> */}
            {/* <Button */}
            {/*   variant="ghost" */}
            {/*   size="icon" */}
            {/*   onClick={() => editor.chain().focus().redo().run()} */}
            {/*   disabled={ */}
            {/*     !editor.can() */}
            {/*       .chain() */}
            {/*       .focus() */}
            {/*       .redo() */}
            {/*       .run() */}
            {/*   } */}
            {/* > */}
            {/*   <Redo className="h-4 w-4" /> */}
            {/* </Button> */}
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().toggleHighlight().run()}
              className={buttonVariants('highlight')}
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleLink({ href: textLink })
                  .run()
              }
            >
              <Highlighter className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => editor.chain().focus().setColor('#958DF1').run()}
              className={buttonVariants('textStyle', { color: '#958DF1' })}
              disabled={
                !editor.can()
                  .chain()
                  .focus()
                  .toggleLink({ href: textLink })
                  .run()
              }
            >
              <Palette className="h-4 w-4" />
            </Button>
            <Popover open={isImagePopoverOpen} onOpenChange={setIsImagePopoverOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                >
                  <ImageIcon className="h-4 w-4" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="p-0 outline-0 border-none shadow-none">
                <div className="flex">
                  <Input
                    className="h-8"
                    placeholder="paste the image link and enter to save"
                    onKeyDown={handleImagePaste}
                    value={imageLink}
                    onChange={(e => setImageLink(e.target.value))}
                  />
                </div>
              </PopoverContent>
            </Popover>
          </div>}
        </div>
        {quickActionPanel && (
          <div className="px-3 pb-2">
            {quickActionPanel}
          </div>
        )}

        <div className={`overflow-auto ${EDITOR_CONTENT_MAX_HEIGHT_CLASS}`}>
          <EditorContent editor={editor} className="p-4 pt-2" />
        </div>
        {footer && (
          <div className="sticky bottom-0 border-t border-slate-100 px-3 py-2">
            {footer}
          </div>
        )}
      </div>
    </div>
  )
})

TextEditor.displayName = 'TextEditor'
export { TextEditor }
