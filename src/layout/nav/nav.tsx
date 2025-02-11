import { Form, NavLink } from 'react-router-dom'
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
import { useEffect, useState } from 'react'
import { NavMainButton } from './nav-main-button'
import { BreadcrumbButton } from './nav-breadcrumb-button'
import { PROJECT_COLORS, ROUTER_PATHS } from '@/constants'
import type { FullProject } from '@/types/project'
import { Button } from '@/components/button/button'

import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/collapsible/collapsible'

import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuSeparator,
  ContextMenuShortcut,
  ContextMenuTrigger,
} from '@/components/context-menu/context-menu'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  // DropdownMenuLabel,
  // DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/dropdown-menu/dropdown-menu'

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  // DialogTrigger,
} from '@/components/dialog/dialog'
import { Input } from '@/components/input/input'
import { Label } from '@/components/label/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/select/select'

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
} from '@/components/alert-dialog/alert-dialog'

import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
  CommandShortcut,
} from '@/components/command/command'

import { Switch } from '@/components/switch/switch'
// import { Badge } from '@/components/badge/badge'

export type SidebarProprs = {
  projects: FullProject[];
  onToggle?: () => void;
}

export function Nav({
  projects,
  onToggle,
}: SidebarProprs) {
  const [isCollaOpen, setIsCollaOpen] = useState(true)
  const [isProjectDialogOpen, setIsProjectDialogOpen] = useState(false)
  const [isCreateProject, setIsCreateProject] = useState(false)
  const [isAlertDialogOpen, setIsAlertDialogOpen] = useState(false)
  const [project, setProject] = useState<FullProject | null>(null)
  const [operation, setOperation] = useState<'archive' | 'delete'>('archive')
  const [isCommandDialogOpen, setIsCommandDialogOpen] = useState(false)

  const inbox = projects.find(project => project.inboxProject) as FullProject
  const [inboxProject] = useState<FullProject>(inbox)

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

  function editProject(id: string) {
    if (id === '') {
      setIsCreateProject(true)
    }

    setIsProjectDialogOpen(true)
  }

  function archiveProject(project: FullProject) {
    setIsAlertDialogOpen(true)
    setProject(project)
    setOperation('archive')
  }

  function deleteProject(project: FullProject) {
    setIsAlertDialogOpen(true)
    setOperation('delete')
    setProject(project)
  }

  function handleSearch(_arg0: string): void {
    setIsCommandDialogOpen(true)
  }

  function handleNoteAdd(_arg0: string): void {
    throw new Error('Function not implemented.')
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

          <BreadcrumbButton onClick={onToggle} className="ml-auto absolute left-full" />
        </div>
      </div>

      <div className="flex flex-col w-full text-sm text-muted-foreground font-medium" >
        <div className="px-3">
          <Button
            variant="ghost"
            size="lg"
            className="flex items-center justify-start gap-3 h-10 py-2 px-2 transition-all text-foreground hover:bg-gray-100"
            onClick={() => handleNoteAdd('')}
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
              onClick={() => handleSearch('')}
            >
              <Search className="h-4 w-4" />
              <span className="">搜索</span>
            </Button>

            <NavMainButton
              route={{ routePath: ROUTER_PATHS.INBOX.path, routeName: ROUTER_PATHS.INBOX.name }}
              icon={Inbox}
              badge={inboxProject?.noteCount}
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
            <div className="flex items-center justify-between space-x-4 py-2 px-2 sticky top-0">
              <h4 className="text-sm font-semibold">
                项目
              </h4>
              <div className="ml-auto">
                <Button variant="ghost" size="sm" onClick={() => editProject('')}>
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
                          <span className="ml-auto bg-transparent text-muted-foreground flex h-6 w-6 shrink-0 items-center justify-center">
                            {project.noteCount ?? 0}
                          </span>
                        </NavLink>
                      </ContextMenuTrigger>
                      <ContextMenuContent className="w-64">
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => editProject(project.projectId)}>
                          <PenLine className="h-4 w-4" />
                          编辑项目
                          <ContextMenuShortcut>⌘E</ContextMenuShortcut>
                        </ContextMenuItem>
                        <ContextMenuItem className="flex gap-x-4 py-2">
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
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => archiveProject(project)}>
                          <Archive className="h-4 w-4" />
                          归档项目
                          <ContextMenuShortcut>⌘A</ContextMenuShortcut>
                        </ContextMenuItem>
                        <ContextMenuItem className="flex gap-x-4 py-2" onClick={() => deleteProject(project)}>
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
      <Dialog open={isProjectDialogOpen} onOpenChange={setIsProjectDialogOpen}>
        {/* <DialogTrigger asChild> */}
        {/*   <Button variant="outline">Edit Profile</Button> */}
        {/* </DialogTrigger> */}
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>{isCreateProject ? '新增项目' : '编辑项目'}</DialogTitle>
            <DialogDescription>
              Make changes to your profile here. Click save when you are done.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="name" className="text-right">
                名称
              </Label>
              <Input
                id="name"
                defaultValue=""
                placeholder="请输入项目名称"
                className="col-span-3"
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="color" className="text-right">
                颜色
              </Label>

              <Select>
                <SelectTrigger className="col-span-3">
                  <SelectValue placeholder="选择一个颜色" />
                </SelectTrigger>
                <SelectContent>
                  {
                    PROJECT_COLORS.map(color => (
                      <SelectItem key={color.value} value={color.value}>
                        <div className="flex items-center gap-x-2">
                          <div
                            className="w-3 h-3 rounded-full mr-2 bg-black"
                            style={{ backgroundColor: `var(--named-color-${color.value.replace('_', '-')})` }}
                          />
                          <span>{color.name}</span>
                        </div>
                      </SelectItem>
                    ))
                  }
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="add-to-favorites" className="text-right">
                添加到收藏
              </Label>
              <Switch id="add-to-favorites" />
            </div>
          </div>
          <DialogFooter>
            <Button type="reset" variant="secondary">取消</Button>
            <Button type="submit">保存</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <AlertDialog open={isAlertDialogOpen} onOpenChange={setIsAlertDialogOpen}>
        {/* <AlertDialogTrigger asChild> */}
        {/*   <Button variant="outline">Show Dialog</Button> */}
        {/* </AlertDialogTrigger> */}
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{operation === 'archive' ? '归档项目' : '删除项目'}</AlertDialogTitle>
            <AlertDialogDescription>
              {operation === 'archive'
                ? `这将归档 ${project?.name} 和它的所有笔记。`
                : `这将永久删除 ${project?.name} 和它的所有笔记。这是不可逆的。`}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>取消</AlertDialogCancel>
            <AlertDialogAction>{operation === 'archive' ? '归档' : '删除'}</AlertDialogAction>
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
