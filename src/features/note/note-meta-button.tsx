import type { LucideIcon } from 'lucide-react'
import { Button } from '@/components/button/button'

export interface NoteMetaButtonProps {
  icon: LucideIcon;
  text: string;
  onClick?: () => void;
  className?: string;
  hover?: boolean;
}

export function NoteMetaButton({
  icon: Icon,
  text,
  onClick,
  className = '',
  hover = false,
}: NoteMetaButtonProps) {
  return (
    <Button
      variant="link"
      size="icon"
      className={`w-auto gap-x-1 text-gray-400 ${hover ? 'hover:underline' : ''} ${className}`}
      onClick={onClick}
    >
      {Icon && <Icon className="h-4 w-4" />}
      <span>{text}</span>
    </Button>
  )
}

