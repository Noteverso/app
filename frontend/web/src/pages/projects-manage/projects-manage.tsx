import { useState } from 'react'
import { Link, useOutletContext } from 'react-router-dom'
import {
  Archive,
  HashIcon,
  LayoutGrid,
  List,
  Plus,
  Rows3,
  Star,
} from 'lucide-react'
import { createProjectApi } from '@/api/project/project'
import type { FullProject, ProjectOutletContext } from '@/types/project'
import { Button } from '@/components/ui/button/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card/card'
import { useToast } from '@/components/ui/toast/use-toast'
import {
  ProjectFormDialog,
  type ProjectFormValues,
} from '@/features/project/project-form-dialog'
import { ROUTER_PATHS } from '@/constants'

type FilterType = 'all' | 'favorited' | 'archived'
type ViewMode = 'grid' | 'list'

export function ProjectsManage() {
  const { projects, refetchProjects, upsertProject } = useOutletContext<ProjectOutletContext>()
  const { toast } = useToast()
  const [filter, setFilter] = useState<FilterType>('all')
  const [viewMode, setViewMode] = useState<ViewMode>('grid')
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isCreatingProject, setIsCreatingProject] = useState(false)

  const allProjects = projects
  const favoritedProjects = allProjects.filter(project => project.isFavorite === 1)
  const archivedProjects: FullProject[] = []

  const filteredProjects = getFilteredProjects({
    filter,
    allProjects,
    favoritedProjects,
    archivedProjects,
  })

  function handleFilterClick(nextFilter: FilterType) {
    setFilter(nextFilter)
    refetchProjects()
  }

  async function handleCreateProject(values: ProjectFormValues) {
    const tempProjectId = `temp-${Date.now()}`
    const optimisticProject: FullProject = {
      projectId: tempProjectId,
      name: values.name,
      color: values.color,
      isFavorite: values.isFavorite ? 1 : 0,
      noteCount: 0,
      inboxProject: false,
    }

    setIsCreatingProject(true)
    upsertProject(optimisticProject)
    setIsCreateDialogOpen(false)

    try {
      await createProjectApi({
        name: values.name,
        color: values.color,
        isFavorite: values.isFavorite ? 1 : 0,
        noteCount: 0,
      })

      toast({
        title: '成功',
        description: '项目创建成功',
      })
    } catch (error) {
      toast({
        title: '错误',
        description: '项目创建失败',
        variant: 'destructive',
      })
    } finally {
      refetchProjects()
      setIsCreatingProject(false)
    }
  }

  return (
    <div className="container mx-auto max-w-6xl py-6">
      <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <h1 className="text-3xl font-bold">项目管理</h1>
          <p className="mt-2 text-muted-foreground">管理你的所有项目</p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <div className="inline-flex items-center gap-1 rounded-lg border bg-background p-1">
            <Button
              type="button"
              size="sm"
              variant={viewMode === 'grid' ? 'default' : 'ghost'}
              onClick={() => setViewMode('grid')}
              aria-pressed={viewMode === 'grid'}
            >
              <LayoutGrid className="mr-2 h-4 w-4" />
              卡片
            </Button>
            <Button
              type="button"
              size="sm"
              variant={viewMode === 'list' ? 'default' : 'ghost'}
              onClick={() => setViewMode('list')}
              aria-pressed={viewMode === 'list'}
            >
              <Rows3 className="mr-2 h-4 w-4" />
              列表
            </Button>
          </div>

          <Button
            type="button"
            onClick={() => setIsCreateDialogOpen(true)}
            data-testid="projects-page-create-project-btn"
          >
            <Plus className="mr-2 h-4 w-4" />
            新建项目
          </Button>
        </div>
      </div>

      <div className="mb-6 flex flex-wrap gap-2">
        <Button
          type="button"
          variant={filter === 'all' ? 'default' : 'outline'}
          onClick={() => handleFilterClick('all')}
          data-testid="projects-filter-all"
        >
          <List className="mr-2 h-4 w-4" />
          全部项目 ({allProjects.length})
        </Button>
        <Button
          type="button"
          variant={filter === 'favorited' ? 'default' : 'outline'}
          onClick={() => handleFilterClick('favorited')}
          data-testid="projects-filter-favorited"
        >
          <Star className="mr-2 h-4 w-4" />
          收藏 ({favoritedProjects.length})
        </Button>
        <Button
          type="button"
          variant={filter === 'archived' ? 'default' : 'outline'}
          onClick={() => handleFilterClick('archived')}
          data-testid="projects-filter-archived"
        >
          <Archive className="mr-2 h-4 w-4" />
          归档 ({archivedProjects.length})
        </Button>
      </div>

      {viewMode === 'grid' ? (
        <ProjectGrid projects={filteredProjects} emptyMessage={getEmptyMessage(filter)} />
      ) : (
        <ProjectList projects={filteredProjects} emptyMessage={getEmptyMessage(filter)} />
      )}

      <ProjectFormDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        mode="create"
        isLoading={isCreatingProject}
        onSubmit={handleCreateProject}
      />
    </div>
  )
}

type FilteredProjectsArgs = {
  filter: FilterType;
  allProjects: FullProject[];
  favoritedProjects: FullProject[];
  archivedProjects: FullProject[];
}

function getFilteredProjects({
  filter,
  allProjects,
  favoritedProjects,
  archivedProjects,
}: FilteredProjectsArgs) {
  switch (filter) {
    case 'favorited':
      return favoritedProjects
    case 'archived':
      return archivedProjects
    default:
      return allProjects
  }
}

function getEmptyMessage(filter: FilterType) {
  switch (filter) {
    case 'favorited':
      return '暂无收藏项目'
    case 'archived':
      return '暂无归档项目'
    default:
      return '暂无项目'
  }
}

function ProjectGrid({
  projects,
  emptyMessage,
}: {
  projects: FullProject[];
  emptyMessage: string;
}) {
  if (projects.length === 0) {
    return <ProjectsEmptyState message={emptyMessage} />
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
        <Link
          key={project.projectId}
          to={`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`}
          data-testid="project-card"
          data-project-id={project.projectId}
        >
          <Card className="h-full cursor-pointer transition-shadow hover:shadow-lg">
            <CardHeader>
              <div className="flex items-center gap-3">
                <HashIcon
                  className="h-5 w-5"
                  style={{ color: `var(--named-color-${project.color.replace('_', '-')})` }}
                />
                <CardTitle className="text-lg">{project.name}</CardTitle>
                {project.isFavorite === 1 && (
                  <Star
                    className="ml-auto h-4 w-4 text-yellow-500"
                    fill="currentColor"
                    data-favorite="true"
                  />
                )}
              </div>
            </CardHeader>
            <CardContent>
              <CardDescription>{project.noteCount || 0} 条笔记</CardDescription>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  )
}

function ProjectList({
  projects,
  emptyMessage,
}: {
  projects: FullProject[];
  emptyMessage: string;
}) {
  if (projects.length === 0) {
    return <ProjectsEmptyState message={emptyMessage} />
  }

  return (
    <div className="flex flex-col gap-3">
      {projects.map((project) => (
        <Link
          key={project.projectId}
          to={`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`}
          data-testid="project-list-item"
          data-project-id={project.projectId}
        >
          <Card className="cursor-pointer transition-colors hover:border-primary/30 hover:bg-accent/30">
            <CardContent className="flex flex-col gap-4 p-4 sm:flex-row sm:items-center">
              <div className="flex min-w-0 items-center gap-3">
                <HashIcon
                  className="h-5 w-5 shrink-0"
                  style={{ color: `var(--named-color-${project.color.replace('_', '-')})` }}
                />
                <div className="min-w-0">
                  <p className="truncate font-medium text-foreground">{project.name}</p>
                  <p className="text-sm text-muted-foreground">{project.noteCount || 0} 条笔记</p>
                </div>
              </div>

              <div className="flex items-center gap-2 sm:ml-auto">
                {project.isFavorite === 1 && (
                  <Star
                    className="h-4 w-4 text-yellow-500"
                    fill="currentColor"
                    data-favorite="true"
                  />
                )}
                <span className="text-sm text-muted-foreground">查看项目</span>
              </div>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  )
}

function ProjectsEmptyState({ message }: { message: string }) {
  return (
    <div className="py-12 text-center text-muted-foreground">
      <p>{message}</p>
    </div>
  )
}
