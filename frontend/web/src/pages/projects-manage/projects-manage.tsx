import { useState } from 'react'
import { useRouteLoaderData, Link } from 'react-router-dom'
import { HashIcon, Star, Archive, List } from 'lucide-react'
import type { FullProject } from '@/types/project'
import { Button } from '@/components/ui/button/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card/card'
import { ROUTER_PATHS } from '@/constants'

type FilterType = 'all' | 'favorited' | 'archived'

export function ProjectsManage() {
  const projects = (useRouteLoaderData('app') as FullProject[]) || []
  const [filter, setFilter] = useState<FilterType>('all')

  const allProjects = projects.filter(p => !p.inboxProject)
  const favoritedProjects = allProjects.filter(p => p.isFavorite === 1)
  const archivedProjects: FullProject[] = [] // TODO: Add archived projects from API

  const getFilteredProjects = () => {
    switch (filter) {
      case 'favorited':
        return favoritedProjects
      case 'archived':
        return archivedProjects
      default:
        return allProjects
    }
  }

  const filteredProjects = getFilteredProjects()

  return (
    <div className="container mx-auto py-6 max-w-6xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">项目管理</h1>
        <p className="text-muted-foreground mt-2">管理你的所有项目</p>
      </div>

      <div className="flex gap-2 mb-6">
        <Button
          variant={filter === 'all' ? 'default' : 'outline'}
          onClick={() => setFilter('all')}
        >
          <List className="h-4 w-4 mr-2" />
          全部项目 ({allProjects.length})
        </Button>
        <Button
          variant={filter === 'favorited' ? 'default' : 'outline'}
          onClick={() => setFilter('favorited')}
        >
          <Star className="h-4 w-4 mr-2" />
          收藏 ({favoritedProjects.length})
        </Button>
        <Button
          variant={filter === 'archived' ? 'default' : 'outline'}
          onClick={() => setFilter('archived')}
        >
          <Archive className="h-4 w-4 mr-2" />
          归档 ({archivedProjects.length})
        </Button>
      </div>

      <ProjectGrid projects={filteredProjects} />
    </div>
  )
}

function ProjectGrid({ projects }: { projects: FullProject[] }) {
  if (projects.length === 0) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        <p>暂无项目</p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {projects.map(project => (
        <Link 
          key={project.projectId} 
          to={`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`}
        >
          <Card className="hover:shadow-lg transition-shadow cursor-pointer h-full">
            <CardHeader>
              <div className="flex items-center gap-3">
                <HashIcon 
                  className="h-5 w-5" 
                  style={{ color: `var(--named-color-${project.color.replace('_', '-')})` }}
                />
                <CardTitle className="text-lg">{project.name}</CardTitle>
                {project.isFavorite === 1 && (
                  <Star className="h-4 w-4 text-yellow-500 ml-auto" fill="currentColor" />
                )}
              </div>
            </CardHeader>
            <CardContent>
              <CardDescription>
                {project.noteCount || 0} 条笔记
              </CardDescription>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  )
}
