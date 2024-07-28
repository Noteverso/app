import { ArrowDownLeft, ArrowUpRight, Hash, Paperclip, Tag } from 'lucide-react'
import parse from 'html-react-parser'
import { useNavigate } from 'react-router-dom'
import { NoteMetaButton } from './note-meta-button'
import { ROUTER_PATHS } from '@/routes/path'

export interface NoteCardProps {
  content: string;
  timeStamp: string;
  labels?: {
    name: string,
    labelId: string
  }[] | null;
  project: {
    name: string,
    projectId: string
  };
  fileNumber?: number | null;
  linkedNoteNumber?: number | null;
  linkingNoteNumber?: number | null;
}

export function NoteCard(props: NoteCardProps) {
  const navigate = useNavigate()

  const {
    labels,
    content,
    timeStamp,
    project,
    fileNumber,
    linkedNoteNumber,
    linkingNoteNumber,
  } = props

  return (
    <>
      <p className="text-gray-600">
        <time dateTime={timeStamp}>{timeStamp}</time>
      </p>
      <div className="flex flex-wrap gap-x-4">
        <NoteMetaButton
          icon={Hash}
          text={project.name}
          className="w-auto gap-x-1 text-gray-400"
          onClick={() => navigate(`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`)}
          hover
        />
        <NoteMetaButton
          icon={Paperclip}
          text={fileNumber ?? 0}
          className="w-auto gap-x-1 text-gray-400"
        />
        <NoteMetaButton
          icon={ArrowUpRight}
          text={linkingNoteNumber ?? 0}
          className="w-auto gap-x-1 text-gray-400"
        />
        <NoteMetaButton
          icon={ArrowDownLeft}
          text={linkedNoteNumber ?? 0}
          className="w-auto gap-x-1 text-gray-400"
        />
        {labels && labels.map(label => (
          <NoteMetaButton
            key={label?.labelId}
            icon={Tag}
            text={label?.name}
            className="w-auto gap-x-1 text-gray-400"
            onClick={() => navigate(`${ROUTER_PATHS.LABELS.path}/${label.labelId}`)}
            hover
          />
        ))}
      </div>
      <div className="prose prose-sm sm:prose-base focus:outline-none max-w-full tiptap">{parse(content)}</div>
    </>
  )
}
