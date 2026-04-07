import { Form, NavLink, useNavigate, useLocation } from 'react-router-dom'
import {
  Archive,
  Calculator,
  Calendar,
  ChevronDown,
  ChevronRight,
  CirclePlus,
  CreditCard,
  HashIcon,
  Inbox,
  LogOut,
  Package2,
  Paperclip,
  PenLine,
  Plus,
  Search,
  Settings,
  Smile,
  Star,
  StarOff,
  Tag,
  Trash2,
  User,
} from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import { NavMainButton } from './nav-main-button'
import { BreadcrumbButton } from './nav-breadcrumb-button'
import { ROUTER_PATHS } from '@/constants'
import type { FullProject } from '@/types/project'
import { Button } from '@/components/ui/button/button'
import { useToast } from '@/components/ui/toast/use-toast'
import {
  createProjectApi,
  updateProjectApi,
  deleteProjectApi,
  archiveProjectApi,
  favoriteProjectApi,
  unfavoriteProjectApi,
} from '@/api/project/project'

import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible/collapsible'

import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuSeparator,
  ContextMenuShortcut,
  ContextMenuTrigger,
} from '@/components/ui/context-menu/context-menu'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  // DropdownMenuLabel,
  // DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu/dropdown-menu'

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  // AlertDialogTrigger,
} from '@/components/ui/alert-dialog/alert-dialog'

import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
  CommandShortcut,
} from '@/components/ui/command/command'
import {
  ProjectFormDialog,
  type ProjectFormValues,
} from '@/features/project/project-form-dialog'
// import { Badge } from '@/components/badge/badge'

export type SidebarProprs = {
  projects: FullProject[];
  setProjects: React.Dispatch<React.SetStateAction<FullProject[]>>;
  refetchProjects: () => void;
  onAddNote?: () => void;
  onToggle?: () => void;
}

export function Nav({
  projects,
  setProjects,
  refetchProjects,
  onAddNote,
  onToggle,
}: SidebarProprs) {
  const navigate = useNavigate()
  const location = useLocation()
  const { toast } = useToast()
  const [isCollaOpen, setIsCollaOpen] = useState(true)
  const [isProjectDialogOpen, setIsProjectDialogOpen] = useState(false)
  const [isCreateProject, setIsCreateProject] = useState(false)
  const [isAlertDialogOpen, setIsAlertDialogOpen] = useState(false)
  const [curProject, setCurProject] = useState<FullProject | null>(null)
  const [operation, setOperation] = useState<'archive' | 'delete'>('archive')
  const [isCommandDialogOpen, setIsCommandDialogOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  // const initialInbox = projects.find(project => project.inboxProject) as FullProject
  const [inboxProject, setInboxProject] = useState<FullProject | null>(null)

  const inbox = useMemo(() => {
    return projects.find(project => project.inboxProject) as FullProject
  }, [projects])

  useEffect(() => {
    setInboxProject(inbox)
  }, [inbox])

  useEffect(() => {
    const inbox = projects.find(project => project.inboxProject) as FullProject
    setInboxProject(inbox)
  }, [projects])

  useEffect(() => {
    const down = (e: KeyboardEvent) => {
      if (e.key === 'j' && (e.metaKey || e.ctrlKey)) {
        e.preventDefault()
        setIsCommandDialogOpen(open => !open)
      }
    }
    document.addEventListener('keydown', down)
    return () => document.removeEventListener('keydown', down)
  }, [])

  // Open dialog for create or edit
  function openProjectDialog(project?: FullProject) {
    if (project) {
      // Edit mode
      setIsCreateProject(false)
      setCurProject(project)
    } else {
      // Create mode
      setIsCreateProject(true)
      setCurProject(null)
    }
    setIsProjectDialogOpen(true)
  }

  // Handle create/update project with optimistic update
  async function handleSaveProject(values: ProjectFormValues) {
    setIsLoading(true)

    try {
      if (isCreateProject) {
        // Create - optimistic add with temp ID
        const tempId = `temp-${Date.now()}`
        const newProject: FullProject = {
          projectId: tempId,
          name: values.name,
          color: values.color,
          isFavorite: values.isFavorite ? 1 : 0,
          noteCount: 0,
          inboxProject: false,
        }
        
        // Optimistic add
        setProjects(prev => [...prev, newProject])
        setIsProjectDialogOpen(false)

        // API call
        const realId = await createProjectApi({
          name: values.name,
          color: values.color,
          isFavorite: values.isFavorite ? 1 : 0,
          noteCount: 0,
        })

        // Replace temp ID with real ID
        setProjects(prev => prev.map(p => 
          p.projectId === tempId ? { ...p, projectId: realId } : p
        ))

        toast({ title: '成功', description: '项目创建成功' })
      } else {
        // Update - optimistic update with old values stored
        if (!curProject) return

        const updatedProject: FullProject = {
          ...curProject,
          name: values.name,
          color: values.color,
          isFavorite: values.isFavorite ? 1 : 0,
        }

        // Optimistic update
        setProjects(prev => prev.map(p => 
          p.projectId === curProject.projectId ? updatedProject : p
        ))
        setIsProjectDialogOpen(false)

        // API call
        await updateProjectApi(curProject.projectId, {
          name: values.name,
          color: values.color,
          isFavorite: values.isFavorite ? 1 : 0,
          noteCount: 0,
        })

        toast({ title: '成功', description: '项目更新成功' })
      }
    } catch (error) {
      // Revert on failure
      if (isCreateProject) {
        // Remove temp project
        setProjects(prev => prev.filter(p => !p.projectId.startsWith('temp-')))
      } else if (curProject) {
        // Restore old project
        setProjects(prev => prev.map(p => 
          p.projectId === curProject.projectId ? curProject : p
        ))
      }
      
      toast({ 
        title: '错误', 
        description: `项目${isCreateProject ? '创建' : '更新'}失败`,
        variant: 'destructive' 
      })
    } finally {
      setIsLoading(false)
    }
  }

  // Handle favorite toggle with optimistic update
  async function handleToggleFavorite(project: FullProject) {
    const newFavoriteStatus = project.isFavorite === 1 ? 0 : 1

    // Optimistic toggle
    setProjects(prev => prev.map(p => 
      p.projectId === project.projectId ? { ...p, isFavorite: newFavoriteStatus } : p
    ))

    try {
      // API call
      if (newFavoriteStatus === 1) {
        await favoriteProjectApi(project.projectId)
      } else {
        await unfavoriteProjectApi(project.projectId)
      }
    } catch (error) {
      // Revert on failure
      setProjects(prev => prev.map(p => 
        p.projectId === project.projectId ? { ...p, isFavorite: project.isFavorite } : p
      ))
      
      toast({ 
        title: '错误', 
        description: '操作失败',
        variant: 'destructive' 
      })
    }
  }

  // Open archive/delete confirmation
  function openConfirmDialog(project: FullProject, op: 'archive' | 'delete') {
    setCurProject(project)
    setOperation(op)
    setIsAlertDialogOpen(true)
  }

  // Handle archive/delete with optimistic update and refetch on failure
  async function handleConfirmAction() {
    if (!curProject) return

    setIsLoading(true)
    setIsAlertDialogOpen(false)

    // Optimistic remove
    setProjects(prev => prev.filter(p => p.projectId !== curProject.projectId))

    // Navigate away if on deleted/archived project page
    if (location.pathname.includes(curProject.projectId)) {
      navigate(ROUTER_PATHS.INBOX.path)
    }

    try {
      // API call
      if (operation === 'archive') {
        await archiveProjectApi(curProject.projectId)
        toast({ title: '成功', description: '项目已归档' })
      } else {
        await deleteProjectApi(curProject.projectId)
      }
    } catch (error) {
      // Refetch on failure (simpler than re-adding)
      refetchProjects()
      
      toast({ 
        title: '错误', 
        description: `项目${operation === 'archive' ? '归档' : '删除'}失败`,
        variant: 'destructive' 
      })
    } finally {
      setIsLoading(false)
    }
  }

  function handleSearch(): void {
    navigate('/app/search')
  }

  function handleNoteAdd() {
    onAddNote?.()
  }

  return (
    <div className="flex flex-col max-h-screen select-none">
      <div className="flex items-center min-h-[60px] px-3">
        <div className="flex items-center w-full px-2">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <div className="flex items-center gap-2 font-semibold">
                <Package2 className="h-6 w-6" />
                <span className="">Noteverso</span>
                <ChevronDown className="h-4 w-4" />
              </div>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              {/* <DropdownMenuLabel>My Account</DropdownMenuLabel> */}
              {/* <DropdownMenuSeparator /> */}
              {/* <DropdownMenuItem>Settings</DropdownMenuItem> */}
              {/* <DropdownMenuItem>Support</DropdownMenuItem> */}
              {/* <DropdownMenuSeparator /> */}
              <DropdownMenuItem>
                <Form
                  method="post"
                  action={ROUTER_PATHS.LOGOUT.path}
                  className="flex items-center gap-3 rounded-lg text-primary transition-all hover:text-primary"
                >
                  <LogOut className="h-4 w-4" />
                  <button type="submit" name="logout">{ROUTER_PATHS.LOGOUT.name}</button>
                </Form>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <BreadcrumbButton onClick={onToggle} className="ml-auto" />
        </div>
      </div>

      <div className="flex flex-col w-full text-sm text-muted-foreground font-medium" >
        <div className="px-3">
          <Button
            variant="ghost"
            size="lg"
            className="flex items-center justify-start gap-3 h-10 py-2 px-2 transition-all text-foreground hover:bg-gray-100"
            onClick={handleNoteAdd}
          >
            <CirclePlus className="h-4 w-4" />
            <span className="">添加笔记</span>
          </Button>
        </div>
      </div>

      <div className="flex flex-col w-full overflow-y-auto overscroll-y-contain text-sm text-muted-foreground font-medium">
        <div className="px-3">
          <div className="grid items-start flex-1">
            <Button
              variant="ghost"
              size="lg"
              className="flex items-center justify-start gap-3 h-10 py-2 px-2 transition-all text-foreground hover:bg-gray-100"
              onClick={handleSearch}
            >
              <Search className="h-4 w-4" />
              <span className="">搜索</span>
            </Button>

            <NavMainButton
              route={{ routePath: ROUTER_PATHS.INBOX.path, routeName: ROUTER_PATHS.INBOX.name }}
              icon={Inbox}
              badge={inboxProject?.noteCount}
              showBadge={!!inboxProject && inboxProject.noteCount > 0}
            />

            <NavMainButton
              route={{ routePath: ROUTER_PATHS.LABELS.path, routeName: ROUTER_PATHS.LABELS.name }}
              icon={Tag}
              showBadge={false}
            />

            <NavMainButton
              route={{ routePath: ROUTER_PATHS.ATTACHMENTS.path, routeName: ROUTER_PATHS.ATTACHMENTS.name }}
              icon={Paperclip}
              showBadge={false}
            />
          </div>

          <Collapsible
            open={isCollaOpen}
            onOpenChange={setIsCollaOpen}
            className="space-y-2"
          >
            <div 
              className="flex items-center justify-between space-x-4 py-2 px-2 sticky top-0 cursor-pointer hover:bg-accent rounded-md transition-colors"
              onClick={() => navigate(ROUTER_PATHS.PROJECTS.path)}
            >
              <h4 className="text-sm font-semibold">
                项目
              </h4>
              <div className="ml-auto flex items-center" onClick={(e) => e.stopPropagation()}>
                <Button variant="ghost" size="sm" onClick={() => openProjectDialog()} disabled={isLoading}>
                  <Plus className="h-4 w-4" />
                  <span className="sr-only">New project</span>
                </Button>
                <CollapsibleTrigger asChild>
                  <Button variant="ghost" size="sm" className="w-9 p-0">
                    {
                      isCollaOpen
                        ? <ChevronDown className="h-4 w-4" />
                        : <ChevronRight className="h-4 w-4" />
                    }
                    <span className="sr-only">Toggle</span>
                  </Button>
                </CollapsibleTrigger>
              </div>
            </div>
            <CollapsibleContent className="space-y-2">
              <div className="grid items-start">
                {
                  projects
                    .filter(project => !project.inboxProject)
                    .map(project => (
                    <ContextMenu key={project.projectId}>
                      <ContextMenuTrigger>
                        <NavLink
                          to={`${ROUTER_PATHS.PROJECTS.path}/${project.projectId}`}
                          key={project.projectId}
                          className={({ isActive }) => `group flex items-center gap-3 py-2 px-2 transition-all text-foreground hover:bg-gray-100 select-none rounded ${isActive ? 'active bg-gray-200' : ''}`}>
                          <HashIcon className="h-4 w-4 group-[.active]:text-blue-500"
                            style={{ color: `var(--named-color-${project.color.replace('_', '-')})` }}
                          />
                          <span>{project.name}</span>
                          { project.noteCount > 0 && <span className="ml-auto bg-transparent text-muted-foreground flex h-6 w-6 shrink-0 items-center justify-center">
                            {project.noteCount}
                            </span>
                          }
                        </NavLink>
                      </ContextMenuTrigger>
                      <ContextMenuContent className="w-64">
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => openProjectDialog(project)} disabled={isLoading}>
                          <PenLine className="h-4 w-4" />
                          编辑项目
                          <ContextMenuShortcut>⌘E</ContextMenuShortcut>
                        </ContextMenuItem>
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => handleToggleFavorite(project)} disabled={isLoading}>
                          {project.isFavorite
                            ? (
                              <>
                                <StarOff className="h-4 w-4" />
                                取消收藏项目
                              </>
                              )
                            : (
                              <>
                                <Star className="h-4 w-4" />
                                收藏项目
                              </>
                              )
                          }
                          <ContextMenuShortcut>⌘D</ContextMenuShortcut>
                        </ContextMenuItem>
                        <ContextMenuSeparator />
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => openConfirmDialog(project, 'archive')} disabled={isLoading}>
                          <Archive className="h-4 w-4" />
                          归档项目
                          <ContextMenuShortcut>⌘A</ContextMenuShortcut>
                        </ContextMenuItem>
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => openConfirmDialog(project, 'delete')} disabled={isLoading}>
                          <Trash2 className="h-4 w-4 text-red-600" />
                          <span className="text-red-600">删除项目</span>
                          <ContextMenuShortcut>⌘R</ContextMenuShortcut>
                        </ContextMenuItem>
                      </ContextMenuContent>
                    </ContextMenu>
                    ))
                }
              </div>
            </CollapsibleContent>
          </Collapsible>
        </div>
      </div>

      {/* <div className="mt-auto p-4"> */}
      {/*   <Card x-chunk="dashboard-02-chunk-0"> */}
      {/*     <CardHeader className="p-2 pt-0 md:p-4"> */}
      {/*       <CardTitle>Upgrade to Pro</CardTitle> */}
      {/*       <CardDescription> */}
      {/*         Unlock all features and get unlimited access to our support */}
      {/*         team. */}
      {/*       </CardDescription> */}
      {/*     </CardHeader> */}
      {/*     <CardContent className="p-2 pt-0 md:p-4 md:pt-0"> */}
      {/*       <Button size="sm" className="w-full"> */}
      {/*         Upgrade */}
      {/*       </Button> */}
      {/*     </CardContent> */}
      {/*   </Card> */}
      {/* </div> */}

      {/* All Dialogs */}
      <ProjectFormDialog
        open={isProjectDialogOpen}
        onOpenChange={setIsProjectDialogOpen}
        mode={isCreateProject ? 'create' : 'edit'}
        initialProject={curProject}
        isLoading={isLoading}
        onSubmit={handleSaveProject}
      />

      <AlertDialog open={isAlertDialogOpen} onOpenChange={setIsAlertDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{operation === 'archive' ? '归档项目' : '删除项目'}</AlertDialogTitle>
            <AlertDialogDescription>
              {operation === 'archive'
                ? `这将归档 ${curProject?.name} 和它的所有笔记。`
                : `这将永久删除 ${curProject?.name} 和它的所有笔记。这是不可逆的。`}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isLoading}>取消</AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmAction} disabled={isLoading}>
              {isLoading ? '处理中...' : (operation === 'archive' ? '归档' : '删除')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <CommandDialog open={isCommandDialogOpen} onOpenChange={setIsCommandDialogOpen}>
        <CommandInput placeholder="Type a command or search..." />
        <CommandList>
          <CommandEmpty>No results found.</CommandEmpty>
          <CommandGroup heading="Suggestions">
            <CommandItem>
              <Calendar className="mr-2 h-4 w-4" />
              <span>Calendar</span>
            </CommandItem>
            <CommandItem>
              <Smile className="mr-2 h-4 w-4" />
              <span>Search Emoji</span>
            </CommandItem>
            <CommandItem>
              <Calculator className="mr-2 h-4 w-4" />
              <span>Calculator</span>
            </CommandItem>
          </CommandGroup>
          <CommandSeparator />
          <CommandGroup heading="Settings">
            <CommandItem>
              <User className="mr-2 h-4 w-4" />
              <span>Profile</span>
              <CommandShortcut>⌘P</CommandShortcut>
            </CommandItem>
            <CommandItem>
              <CreditCard className="mr-2 h-4 w-4" />
              <span>Billing</span>
              <CommandShortcut>⌘B</CommandShortcut>
            </CommandItem>
            <CommandItem>
              <Settings className="mr-2 h-4 w-4" />
              <span>Settings</span>
              <CommandShortcut>⌘S</CommandShortcut>
            </CommandItem>
          </CommandGroup>
        </CommandList>
      </CommandDialog>
    </div>
  )
}
