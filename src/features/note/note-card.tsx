import { ArrowDownLeft, ArrowUpRight, Hash, Paperclip, Tag } from 'lucide-react'
import parse from 'html-react-parser'
import { Button } from '@/components/button'

interface NoteCardProps {
  content: string,
  timeStamp: string,
  tags?: {
    name: string,
    tagId: string
  }[] | null,
  project?: {
    name: string,
    projectId: string
  },
  fileNumber?: number | null,
  linkedNoteNumber?: number | null,
  linkingNoteNumber?: number | null
}

function NoteCard(props: NoteCardProps) {
  const {
    tags,
    content,
    timeStamp,
    project,
    fileNumber,
    linkedNoteNumber,
    linkingNoteNumber,
  } = props

  return (
    <>
      <p>
        <time dateTime={timeStamp}>{timeStamp}</time>
      </p>
      <div className="flex flex-wrap gap-x-4">
        <Button
          variant="ghost"
          size="icon"
          className="w-auto gap-x-1 text-gray-400 hover:underline"
        >
          <Hash className="h-4 w-4" />
          <span>{project?.name}</span>
        </Button>
        <Button
          variant="ghost"
          size="icon"
          className="w-auto gap-x-1 text-gray-400"
        >
          <Paperclip className="h-4 w-4" />
          <span>{fileNumber ?? 0}</span>
        </Button>
        <Button
          variant="ghost"
          size="icon"
          className="w-auto gap-x-1 text-gray-400"
        >
          <ArrowUpRight className="h-4 w-4" />
          <span>{linkingNoteNumber ?? 0}</span>
        </Button>
        <Button
          variant="ghost"
          size="icon"
          className="w-auto gap-x-1 text-gray-400"
        >
          <ArrowDownLeft className="h-4 w-4" />
          <span>{linkedNoteNumber ?? 0}</span>
        </Button>
        {tags && tags.map(tag => (
          <Button
            variant="ghost"
            size="icon"
            key={tag?.tagId}
            className="w-auto gap-x-1 text-gray-400 hover:underline"
          >
            <Tag className="h-4 w-4" />
            <span>{tag?.name}</span>
          </Button>
        ))}
      </div>
      <div className="prose prose-sm sm:prose-base focus:outline-none max-w-full tiptap">{parse(content)}</div>
    </>
  )
}

export default NoteCard
