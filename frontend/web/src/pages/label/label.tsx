import { useEffect, useState } from 'react'
import { Plus, Trash2, Edit2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { getLabelsApi, createLabelApi, updateLabelApi, deleteLabelApi } from '@/api/label/label'
import type { LabelItem, LabelCreateRequest, LabelUpdateRequest } from '@/types/label'
import { Button } from '@/components/ui/button/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog/dialog'
import { Input } from '@/components/ui/input/input'
import { useToast } from '@/components/ui/toast/use-toast'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog/alert-dialog'
import { ROUTER_PATHS } from '@/constants'

const COLORS = ['#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16', '#22c55e', '#10b981', '#14b8a6', '#06b6d4', '#0ea5e9', '#3b82f6', '#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899']

export function Label() {
  const navigate = useNavigate()
  const [labels, setLabels] = useState<LabelItem[]>([])
  const [loading, setLoading] = useState(true)
  const [isLoading, setIsLoading] = useState(false)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingLabel, setEditingLabel] = useState<LabelItem | null>(null)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [deletingLabel, setDeletingLabel] = useState<LabelItem | null>(null)
  const [formData, setFormData] = useState({ name: '', color: COLORS[0] })
  const { toast } = useToast()

  useEffect(() => {
    loadLabels()
  }, [])

  const loadLabels = async () => {
    setLoading(true)
    const response = await getLabelsApi()
    if (response.ok) {
      setLabels(response.data)
    } else {
      toast({ title: 'Failed to load labels', variant: 'destructive' })
    }
    setLoading(false)
  }

  const handleCreate = async () => {
    if (!formData.name.trim()) {
      toast({ title: 'Label name is required', variant: 'destructive' })
      return
    }

    setIsLoading(true)
    const tempId = `temp-${Date.now()}`
    const newLabel: LabelItem = {
      labelId: tempId,
      name: formData.name,
      color: formData.color,
      noteCount: 0,
    }

    // Optimistic add
    setLabels(prev => [...prev, newLabel])
    setDialogOpen(false)
    setFormData({ name: '', color: COLORS[0] })

    try {
      const data: LabelCreateRequest = { name: formData.name, color: formData.color }
      const response = await createLabelApi(data)
      
      if (response.ok) {
        // Replace temp ID with real ID
        setLabels(prev => prev.map(l => l.labelId === tempId ? { ...l, labelId: response.data } : l))
        toast({ title: 'Label created successfully' })
      } else {
        throw new Error('Failed to create')
      }
    } catch (error) {
      // Revert on error
      setLabels(prev => prev.filter(l => l.labelId !== tempId))
      toast({ title: 'Failed to create label', variant: 'destructive' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleUpdate = async () => {
    if (!editingLabel || !formData.name.trim()) return

    setIsLoading(true)
    const oldLabel = editingLabel
    const updatedLabel: LabelItem = {
      ...editingLabel,
      name: formData.name,
      color: formData.color,
    }

    // Optimistic update
    setLabels(prev => prev.map(l => l.labelId === editingLabel.labelId ? updatedLabel : l))
    setDialogOpen(false)
    setEditingLabel(null)
    setFormData({ name: '', color: COLORS[0] })

    try {
      const data: LabelUpdateRequest = { name: formData.name, color: formData.color }
      const response = await updateLabelApi(editingLabel.labelId, data)
      
      if (response.ok) {
        toast({ title: 'Label updated successfully' })
      } else {
        throw new Error('Failed to update')
      }
    } catch (error) {
      // Revert on error
      setLabels(prev => prev.map(l => l.labelId === oldLabel.labelId ? oldLabel : l))
      toast({ title: 'Failed to update label', variant: 'destructive' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleDelete = async () => {
    if (!deletingLabel) return

    setIsLoading(true)
    const labelId = deletingLabel.labelId

    // Optimistic remove
    setLabels(prev => prev.filter(l => l.labelId !== labelId))
    setDeleteDialogOpen(false)
    setDeletingLabel(null)

    try {
      const response = await deleteLabelApi(labelId)
      
      if (response.ok) {
        toast({ title: 'Label deleted successfully' })
      } else {
        throw new Error('Failed to delete')
      }
    } catch (error) {
      // Refetch on error
      await loadLabels()
      toast({ title: 'Failed to delete label', variant: 'destructive' })
    } finally {
      setIsLoading(false)
    }
  }

  const openEditDialog = (label: LabelItem, e: React.MouseEvent) => {
    e.stopPropagation()
    setEditingLabel(label)
    setFormData({ name: label.name, color: label.color })
    setDialogOpen(true)
  }

  const openCreateDialog = () => {
    setEditingLabel(null)
    setFormData({ name: '', color: COLORS[0] })
    setDialogOpen(true)
  }

  const openDeleteDialog = (label: LabelItem, e: React.MouseEvent) => {
    e.stopPropagation()
    setDeletingLabel(label)
    setDeleteDialogOpen(true)
  }

  if (loading) return <div className="p-6">Loading...</div>

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Labels</h1>
        <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={openCreateDialog} disabled={isLoading}>
              <Plus className="w-4 h-4 mr-2" />
              New Label
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{editingLabel ? 'Edit Label' : 'Create Label'}</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <label className="text-sm font-medium">Name</label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Label name"
                />
              </div>
              <div>
                <label className="text-sm font-medium">Color</label>
                <div className="grid grid-cols-8 gap-2 mt-2">
                  {COLORS.map((color) => (
                    <button
                      key={color}
                      className={`w-8 h-8 rounded-full ${formData.color === color ? 'ring-2 ring-offset-2 ring-gray-400' : ''}`}
                      style={{ backgroundColor: color }}
                      onClick={() => setFormData({ ...formData, color })}
                    />
                  ))}
                </div>
              </div>
              <Button onClick={editingLabel ? handleUpdate : handleCreate} className="w-full" disabled={isLoading}>
                {editingLabel ? 'Update' : 'Create'}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {labels.map((label) => (
          <div 
            key={label.labelId} 
            className="border rounded-lg p-4 flex items-center justify-between cursor-pointer hover:bg-gray-50"
            onClick={() => navigate(`${ROUTER_PATHS.LABELS.path}/${label.labelId}`)}
          >
            <div className="flex items-center gap-3">
              <div className="w-4 h-4 rounded-full" style={{ backgroundColor: label.color }} />
              <div>
                <div className="font-medium">{label.name}</div>
                <div className="text-sm text-gray-500">{label.noteCount || 0} notes</div>
              </div>
            </div>
            <div className="flex gap-2">
              <Button variant="ghost" size="sm" onClick={(e) => openEditDialog(label, e)} disabled={isLoading}>
                <Edit2 className="w-4 h-4" />
              </Button>
              <Button variant="ghost" size="sm" onClick={(e) => openDeleteDialog(label, e)} disabled={isLoading}>
                <Trash2 className="w-4 h-4" />
              </Button>
            </div>
          </div>
        ))}
      </div>

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Label</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete "{deletingLabel?.name}"? This will remove the label from all notes.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isLoading}>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete} disabled={isLoading}>Delete</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
