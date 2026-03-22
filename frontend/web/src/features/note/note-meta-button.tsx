import type { ButtonHTMLAttributes } from 'react'
import type { LucideIcon } from 'lucide-react'
import { Button } from '@/components/ui/button/button'

export interface NoteMetaButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  icon: LucideIcon;
  text: string;
  hover?: boolean;
}

export function NoteMetaButton({
  icon: Icon,
  text,
  className = '',
  hover = false,
  type = 'button',
  ...props
}: NoteMetaButtonProps) {
  return (
    <Button
      variant="link"
      size="icon"
      type={type}
      className={`w-auto gap-x-1 text-gray-400 ${hover ? 'hover:underline' : ''} ${className}`}
      {...props}
    >
      {Icon && <Icon className="h-4 w-4" />}
      <span>{text}</span>
    </Button>
  )
}
