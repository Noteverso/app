import { useEffect, useState } from 'react'
import { PROJECT_COLORS } from '@/constants'
import type { FullProject } from '@/types/project'
import { Button } from '@/components/ui/button/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog/dialog'
import { Input } from '@/components/ui/input/input'
import { Label } from '@/components/ui/label/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select/select'
import { Switch } from '@/components/ui/switch/switch'
import { useToast } from '@/components/ui/toast/use-toast'

export type ProjectFormValues = {
  name: string;
  color: string;
  isFavorite: boolean;
}

type ProjectFormDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  initialProject?: FullProject | null;
  isLoading?: boolean;
  onSubmit: (values: ProjectFormValues) => void | Promise<void>;
}

const DEFAULT_PROJECT_COLOR = PROJECT_COLORS[0].value

function getInitialValues(project?: FullProject | null): ProjectFormValues {
  if (!project) {
    return {
      name: '',
      color: DEFAULT_PROJECT_COLOR,
      isFavorite: false,
    }
  }

  return {
    name: project.name,
    color: project.color,
    isFavorite: project.isFavorite === 1,
  }
}

export function ProjectFormDialog({
  open,
  onOpenChange,
  mode,
  initialProject,
  isLoading = false,
  onSubmit,
}: ProjectFormDialogProps) {
  const { toast } = useToast()
  const [projectName, setProjectName] = useState('')
  const [projectColor, setProjectColor] = useState<string>(DEFAULT_PROJECT_COLOR)
  const [projectIsFavorite, setProjectIsFavorite] = useState(false)

  useEffect(() => {
    if (!open) {
      return
    }

    const values = getInitialValues(mode === 'edit' ? initialProject : null)
    setProjectName(values.name)
    setProjectColor(values.color)
    setProjectIsFavorite(values.isFavorite)
  }, [initialProject, mode, open])

  async function handleSubmit() {
    const nextProjectName = projectName.trim()

    if (!nextProjectName) {
      toast({
        title: '错误',
        description: '项目名称不能为空',
        variant: 'destructive',
      })
      return
    }

    if (!projectColor) {
      toast({
        title: '错误',
        description: '请选择项目颜色',
        variant: 'destructive',
      })
      return
    }

    await onSubmit({
      name: nextProjectName,
      color: projectColor,
      isFavorite: projectIsFavorite,
    })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{mode === 'create' ? '新增项目' : '编辑项目'}</DialogTitle>
          <DialogDescription>
            {mode === 'create' ? '创建一个新项目来组织你的笔记' : '修改项目信息'}
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="project-name" className="text-right">
              名称
            </Label>
            <Input
              id="project-name"
              name="projectName"
              value={projectName}
              onChange={(event) => setProjectName(event.target.value)}
              placeholder="请输入项目名称"
              className="col-span-3"
              disabled={isLoading}
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="project-color" className="text-right">
              颜色
            </Label>
            <Select value={projectColor} onValueChange={setProjectColor} disabled={isLoading}>
              <SelectTrigger id="project-color" className="col-span-3">
                <SelectValue placeholder="选择一个颜色" />
              </SelectTrigger>
              <SelectContent>
                {PROJECT_COLORS.map((color) => (
                  <SelectItem
                    key={color.value}
                    value={color.value}
                    data-color={color.value}
                  >
                    <div className="flex items-center gap-x-2">
                      <div
                        className="h-3 w-3 rounded-full"
                        style={{ backgroundColor: `var(--named-color-${color.value.replace('_', '-')})` }}
                      />
                      <span>{color.name}</span>
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="project-favorite" className="text-right">
              添加到收藏
            </Label>
            <Switch
              id="project-favorite"
              checked={projectIsFavorite}
              onCheckedChange={setProjectIsFavorite}
              disabled={isLoading}
            />
          </div>
        </div>
        <DialogFooter>
          <Button
            type="button"
            variant="secondary"
            onClick={() => onOpenChange(false)}
            disabled={isLoading}
          >
            取消
          </Button>
          <Button type="button" onClick={handleSubmit} disabled={isLoading}>
            {isLoading ? '保存中...' : mode === 'create' ? '创建项目' : '保存'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
