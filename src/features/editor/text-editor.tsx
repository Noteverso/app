import { type KeyboardEvent, useCallback, useState } from 'react'
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
} from 'lucide-react'
import classnames from 'classnames'
import { common, createLowlight } from 'lowlight'
import css from 'highlight.js/lib/languages/css'
import js from 'highlight.js/lib/languages/javascript'
import ts from 'highlight.js/lib/languages/typescript'
import html from 'highlight.js/lib/languages/xml'
import { PopoverTrigger } from '@radix-ui/react-popover'
import { TrailingNode } from './tailing-node'
import { Button } from '@/components/button/button'

import {
  Popover,
  PopoverContent,
} from '@/components/popover/popover'
import { Input } from '@/components/input/input'

const lowlight = createLowlight(common)
lowlight.register('html', html)
lowlight.register('css', css)
lowlight.register('js', js)
lowlight.register('ts', ts)

export function TextEditor() {
  const [isImagePopoverOpen, setIsImagePopoverOpen] = useState(false)
  const [isLinkPopoverOpen, setIsLinkPopoverOpen] = useState(false)
  const [textLink, setTextLink] = useState('')
  const [imageLink, setImageLink] = useState('')

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
    ],
    content: '',
    editorProps: {
      attributes: {
        class: 'prose prose-sm sm:prose-base focus:outline-none max-w-full',
      },
    },
    onUpdate: (_props) => {
      // const text = props.editor?.getHTML()
      // console.warn(text)
    },
  })

  const handleNoteSave = async () => {
    const content = editor?.getHTML()
    console.warn(content)
  }

  const handleLinkPaste = useCallback((event: KeyboardEvent) => {
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
    const isActive = editor?.isActive(tag, options) ? 'bg-black/5' : ''

    return classnames(isActive, 'hover:text-neutral-700 hover:bg-black/5 w-8 h-8')
  }

  return (
    <div>
      <div className="border-2 border-black rounded">
        <div className="p-4 flex items-center">
          {editor && <div className="Button-group">
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
                  className="h-8 w-8"
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

        <div className="h-32 max-h-64 overflow-auto bg-white relative">
          <EditorContent editor={editor} className="p-4 h-40" />
        </div>
      </div>
      <div className="flex mt-4">
        <div className="text-right ml-auto">
          <Button onClick={handleNoteSave}>
            保存
          </Button>
        </div>
      </div>
    </div>
  )
}
