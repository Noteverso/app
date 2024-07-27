import { PanelLeft } from 'lucide-react'
import { Button } from '@/components/button/button'

export function BreadcrumbButton({
  onClick,
  className,
}: { onClick?: () => void; className?: string }) {
  return (
    <Button
      variant="outline"
      size="icon"
      className={`shrink-0 hidden md:flex border-none ${className}`}
      onClick={onClick}
    >
      <PanelLeft className="h-5 w-5" />
      <span className="sr-only">Toggle navigation menu</span>
    </Button>
  )
}
