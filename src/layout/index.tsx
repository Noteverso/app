import { useLoaderData } from 'react-router-dom'
import { PanelLeft } from 'lucide-react'
import { Main } from './main/main'
import { Nav } from './nav'
import { Project } from '@/api/project'

import { Sheet, SheetContent, SheetTrigger } from '@/components/sheet'
import { Button } from '@/components/button'

export function Layout() {
  const layoutLoaderData = useLoaderData() as { projectList: Project[] }

  return (
    <div className="app-layout">
      <div className="app-nav hidden border-r md:block">
        <Nav projectList={layoutLoaderData.projectList} />
      </div>
      <div className="app-layout__content">
        <header className="flex h-14 items-center gap-4 px-4 lg:h-[60px] lg:px-6">
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
              <Nav projectList={layoutLoaderData.projectList} />
            </SheetContent>
          </Sheet>
        </header>
        <Main />
      </div>
    </div>
  )
}
