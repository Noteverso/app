import { MoreHorizontal, Pencil, Star, Trash } from 'lucide-react'
import { Button } from '@/components/ui/button/button'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu/dropdown-menu'
import { cn } from '@/lib/utils'

interface NoteActionsProps {
  onEdit: () => void
  onDelete: () => void
  onFavorite: () => void
  className?: string
}
export function NoteActions({ onEdit, onDelete, onFavorite, className }: NoteActionsProps) {
  return (
    <div className={cn('flex gap-2', className)}>
      <Button variant="ghost" size="icon" onClick={onEdit} className="invisible group-hover/item:visible">
        <Pencil className="w-4 h-4" />
      </Button>
      <Button variant="ghost" size="icon" onClick={onFavorite} className="invisible group-hover/item:visible">
        <Star className="w-4 h-4" />
      </Button>
      <DropdownMenu>
        <DropdownMenuTrigger asChild className="invisible data-[state=open]:visible group-hover/item:visible">
          <Button variant="ghost" size="icon">
            <MoreHorizontal className="w-4 h-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={onEdit}>
            <Pencil className="w-4 h-4 mr-2" /> 编辑
          </DropdownMenuItem>
          <DropdownMenuItem onClick={onDelete} className="text-red-500">
            <Trash className="w-4 h-4 mr-2" /> 删除
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  )
}
