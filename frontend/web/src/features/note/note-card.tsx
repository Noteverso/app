import { ArrowDownLeft, ArrowUpRight, Paperclip, Tag } from 'lucide-react'
import parse from 'html-react-parser'
import { useNavigate } from 'react-router-dom'
import { NoteMetaButton } from './note-meta-button'
import { NoteActions } from './note-actions'
import { ROUTER_PATHS } from '@/constants'
import { dateFormat } from '@/lib/utils'

export interface NoteCardProps {
  content: string;
  addedAt: string;
  labels?: {
    name: string,
    labelId: string
  }[] | null;
  project: {
    name: string,
    projectId: string
  };
  attachmentCount?: number | null;
  referencedCount?: number | null;
  referencingCount?: number | null;
}

export function NoteCard(props: NoteCardProps) {
  const navigate = useNavigate()

  const {
    labels,
    content,
    addedAt,
    // project,
    attachmentCount,
    referencedCount,
    referencingCount,
  } = props

  return (
    <div className="group/item">
      <div className="flex justify-between">
        <div className="text-gray-600 mb-2">
          <time dateTime={addedAt}>{dateFormat(addedAt)}</time>
        </div>
        <NoteActions onDelete={() => {}} onEdit={() => {}} onFavorite={() => {}} className="" />
      </div>

      <div className="flex flex-wrap gap-x-4">
        {/* <NoteMetaButton
          icon={Hash}
          text={project.name}
          className="w-auto gap-x-1 text-gray-400"
          onClick={() => navigate(`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`)}
          hover
        /> */}
        { attachmentCount && <NoteMetaButton
            icon={Paperclip}
            text={`${attachmentCount} ${attachmentCount === 1 ? 'file' : 'files'}`}
            className="w-auto gap-x-1 text-gray-400"
          />
        }
        { referencingCount && <NoteMetaButton
            icon={ArrowUpRight}
            text={`${referencingCount} ${referencingCount === 1 ? 'link' : 'links'}`}
            className="w-auto gap-x-1 text-gray-400"
          />
        }

        { referencedCount && <NoteMetaButton
            icon={ArrowDownLeft}
            text={`${referencedCount} ${referencedCount === 1 ? 'backlink' : 'backlinks'}`}
            className="w-auto gap-x-1 text-gray-400"
          />
        }
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
    </div>
  )
}
