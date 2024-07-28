import type { LucideIcon } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import { Badge } from '@/components/badge/badge'

export interface NavMainButtonProps {
  routePath: string;
  routeName: string;
  icon: LucideIcon;
  badge?: number;
  showBadge?: boolean;

}

export function NavMainButton({
  routePath,
  routeName,
  icon: Icon,
  badge,
  showBadge = true,
}: NavMainButtonProps) {
  return (
    <NavLink
      to={routePath}
      className="group h-10 py-2 px-2 transition-all hover:text-primary"
    >
      <div className="flex items-center gap-3 group-[.active]:text-blue-500">
        <Icon className="h-4 w-4" />
        <span>{routeName}</span>
        {showBadge && <Badge className="ml-auto bg-transparent text-muted-foreground flex h-6 w-6 shrink-0 items-center justify-center">
          {badge ?? 0}
        </Badge>}
      </div>
    </NavLink>
  )
}
