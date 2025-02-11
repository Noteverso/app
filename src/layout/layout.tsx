import { Outlet, useLoaderData } from 'react-router-dom'
import { PanelLeft } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { Nav } from './nav/nav'
import type { FullProject } from '@/types/project'

import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from '@/components/sheet/sheet'
import { Button } from '@/components/button/button'

export function Layout() {
  const [isSidebarVisible, setIsSidebarVisible] = useState(true)
  const sidebarRef = useRef<HTMLDivElement>(null)
  const [sidebarWidth, setSidebarWidth] = useState(0)

  const projects = useLoaderData() as FullProject[]
  const inboxProject = projects.find(project => project.inboxProject) as FullProject
  const otherProjects = projects.filter(project => !project.inboxProject)

  useEffect(() => {
    if (!sidebarRef.current) {
      return
    }

    const resizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        setSidebarWidth(entry.contentRect.width)
      }
    })

    resizeObserver.observe(sidebarRef.current)
    return () => {
      resizeObserver.disconnect()
    }
  }, [])

  const handleNavToggle = () => {
    setIsSidebarVisible(!isSidebarVisible)
  }

  return (
    <div id="app-layout" className={'flex items-start h-full overflow-auto transition-all duration-300 ease-in-out'}>
      <div
        ref={sidebarRef}
        style={{ '--copmuted-sidebar-width': `${sidebarWidth}px` } as React.CSSProperties}
        id="app-nav"
        className={`relative hidden md:block flex-shrink-0 flex-grow-0 md:w-[var(--sidebar-width)] min-w-0 transition-[margin-left] duration-300 ease-in-out ${isSidebarVisible ? 'ml-0' : '-ml-[var(--copmuted-sidebar-width)]'}`}>
        <Nav projects={projects} onToggle={handleNavToggle} />
      </div>

      <div id="app-layout__content" className="h-full flex-grow overflow-auto transition-all duration-300 ease-in-out">
        <header className="flex h-[60px] items-center gap-4 px-4 lg:px-6 mb-4">
          {/* 移动端侧边栏 */}
          <Sheet>
            <SheetTrigger asChild>
              <Button
                variant="outline"
                size="icon"
                className="shrink-0 md:hidden"
              >
                <PanelLeft className="h-5 w-5" />
                <span className="sr-only">Toggle navigation menu</span>
              </Button>
            </SheetTrigger>

            <SheetContent side="left" className="flex flex-col p-0">
              <SheetHeader className="hidden">
                <SheetTitle>Edit profile</SheetTitle>
                <SheetDescription>
                  The mobile device navigation
                </SheetDescription>
              </SheetHeader>
              <Nav projects={projects} />
            </SheetContent>
          </Sheet>
        </header>

        <div id="app-main" className="grid justify-items-center px-4 lg:px-6">
          <div id="app-main__content" className="w-full max-w-[var(--main-content-max-width)]">
            <Outlet context={{ projects: otherProjects, inboxProject }} />
          </div>
        </div>
      </div>
    </div>
  )
}
