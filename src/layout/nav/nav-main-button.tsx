import type { LucideIcon } from 'lucide-react'
import { NavLink } from 'react-router-dom'

export interface NavMainButtonProps {
  route: {
    routeName: string;
    routePath: string;
    state?: object
  },
  icon: LucideIcon;
  badge?: number;
  showBadge?: boolean;
}

export function NavMainButton({
  route,
  icon: Icon,
  badge,
  showBadge = true,
}: NavMainButtonProps) {
  return (
    <NavLink
      to={route.routePath}
      className={({ isActive }) => `group h-10 py-2 px-2 transition-all text-foreground hover:bg-gray-100  rounded ${isActive ? 'active bg-gray-200' : ''}`}
      state={route.state}
    >
      <div className="flex items-center gap-3">
        <Icon className="h-4 w-4" />
        <span>{route.routeName}</span>
        {showBadge && <span className="ml-auto bg-transparent text-muted-foreground flex h-6 w-6 shrink-0 items-center justify-center">
          {badge ?? 0}
        </span>}
      </div>
    </NavLink>
  )
}
