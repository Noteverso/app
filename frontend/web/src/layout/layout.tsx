import { Outlet, useLoaderData, useLocation, useRevalidator } from 'react-router-dom'
import { PanelLeft } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { Nav } from './nav/nav'
import type { FullProject } from '@/types/project'

import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet/sheet'
import { Button } from '@/components/ui/button/button'
import { Toaster } from '@/components/ui/toast/toaster'
import { NoteCreateDialog } from '@/features/note/note-create-dialog'

export function getContentTopControlsClass() {
  return 'mb-2 flex h-10 items-center md:hidden'
}

export function upsertProjectList(projects: FullProject[], nextProject: FullProject) {
  const existingIndex = projects.findIndex(project => project.projectId === nextProject.projectId)

  if (existingIndex === -1) {
    return [...projects, nextProject]
  }

  return projects.map(project => (
    project.projectId === nextProject.projectId ? nextProject : project
  ))
}

export function Layout() {
  const [isSidebarVisible, setIsSidebarVisible] = useState(true)
  const [isNoteDialogOpen, setIsNoteDialogOpen] = useState(false)
  const sidebarRef = useRef<HTMLDivElement>(null)
  const [sidebarWidth, setSidebarWidth] = useState(0)
  const location = useLocation()

  const loadedProjects = useLoaderData() as FullProject[]
  const [projects, setProjects] = useState<FullProject[]>(loadedProjects)
  const revalidator = useRevalidator()

  // Update local state when loader data changes
  useEffect(() => {
    setProjects(loadedProjects)
  }, [loadedProjects])

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

  const refetchProjects = () => {
    revalidator.revalidate()
  }

  const upsertProject = (nextProject: FullProject) => {
    setProjects(prevProjects => upsertProjectList(prevProjects, nextProject))
  }

  return (
    <div id="app-layout" className={'flex items-start h-full overflow-auto transition-all duration-300 ease-in-out'}>
      <div
        ref={sidebarRef}
        style={{ '--copmuted-sidebar-width': `${sidebarWidth}px` } as React.CSSProperties}
        id="app-nav"
        className={`relative hidden md:block flex-shrink-0 flex-grow-0 md:w-[var(--sidebar-width)] min-w-0 transition-[margin-left] duration-300 ease-in-out ${isSidebarVisible ? 'ml-0' : '-ml-[var(--copmuted-sidebar-width)]'}`}>
        <Nav
          projects={projects}
          setProjects={setProjects}
          refetchProjects={refetchProjects}
          onAddNote={() => setIsNoteDialogOpen(true)}
          onToggle={handleNavToggle}
        />
      </div>

      <div id="app-layout__content" className="h-full flex-grow overflow-auto transition-all duration-300 ease-in-out">
        <div id="app-main" className="grid justify-items-center px-4 lg:px-6">
          <div id="app-main__content" className="w-full">
            <div className={getContentTopControlsClass()}>
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
                  <Nav
                    projects={projects}
                    setProjects={setProjects}
                    refetchProjects={refetchProjects}
                    onAddNote={() => setIsNoteDialogOpen(true)}
                  />
                </SheetContent>
              </Sheet>
            </div>
            <Outlet context={{
              projects: otherProjects,
              inboxProject,
              refetchProjects,
              upsertProject,
              isSidebarVisible,
              onToggleSidebar: handleNavToggle,
            }} />
            <NoteCreateDialog
              open={isNoteDialogOpen}
              onOpenChange={setIsNoteDialogOpen}
              projects={projects}
              inboxProject={inboxProject}
              pathname={location.pathname}
              refetchProjects={refetchProjects}
              upsertProject={upsertProject}
            />
            <Toaster />
          </div>
        </div>
      </div>
    </div>
  )
}
